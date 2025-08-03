package com.alexmercerind.audire.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexmercerind.audire.models.Music
import com.alexmercerind.audire.repository.ShazamIdentifyRepository
import com.alexmercerind.audire.utils.AudioRecorder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class IdentifyViewModel : ViewModel() {
    val error get() = _error.asSharedFlow()
    val music get() = _music.asSharedFlow()
    val active get() = audioRecorder.active
    val duration get() = audioRecorder.duration

    private val _error = MutableSharedFlow<Unit>()
    private val _music = MutableSharedFlow<Music>()

    private val audioRecorder = AudioRecorder(viewModelScope)
    private val repository = ShazamIdentifyRepository()

    fun start() {
        audioRecorder.start()
    }

    fun stop() {
        audioRecorder.stop()
    }

    override fun onCleared() {
        super.onCleared()
        audioRecorder.stop()
    }

    init {
        combine(audioRecorder.duration, audioRecorder.buffer) { duration, buffer ->
            duration to buffer
        }
            .sampleImmediate(2000L)
            .onEach { (duration, buffer) ->
                runCatching {
                    if (buffer.isEmpty()) return@onEach
                    if (duration < MIN_DURATION) return@onEach
                    if (duration > MAX_DURATION) {
                        _error.emit(Unit)
                        audioRecorder.stop()
                        return@onEach
                    }
                    repository.identify(duration, buffer)?.let {
                        // HACK: Prevent obscure music from being displayed if duration lesser than MAX_DURATION.
                        if (it.album.isNullOrEmpty() && duration < MAX_DURATION) return@onEach
                        _music.emit(it)
                        audioRecorder.stop()
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun <T> Flow<T>.sampleImmediate(periodMillis: Long): Flow<T> = channelFlow {
        var lastEmitTime = 0L
        collect { value ->
            val now = System.currentTimeMillis()
            if (lastEmitTime == 0L || now - lastEmitTime >= periodMillis) {
                lastEmitTime = now
                trySend(value)
            }
        }
    }

    companion object {
        const val MIN_DURATION = 3
        const val MAX_DURATION = 12
    }
}
