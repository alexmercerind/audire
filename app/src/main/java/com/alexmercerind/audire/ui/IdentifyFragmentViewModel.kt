package com.alexmercerind.audire.ui

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder.AudioSource
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexmercerind.audire.data.ShazamIdentifyDataSource
import com.alexmercerind.audire.models.Music
import com.alexmercerind.audire.repository.IdentifyRepository
import com.alexmercerind.audire.utils.Constants
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext


class IdentifyFragmentViewModel : ViewModel() {
    val idle
        get() = _idle as LiveData<Boolean>
    val duration
        get() = _duration as LiveData<Int>
    val music
        get() = _music.asSharedFlow()
    val error
        get() = _error.asSharedFlow()

    private val _idle = MutableLiveData(true)
    private val _duration = MutableLiveData(0)
    private val _music = MutableSharedFlow<Music>()
    private val _error = MutableSharedFlow<String>()

    companion object {
        private const val SAMPLE_RATE = 16000
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT

        // AudioFormat.CHANNEL_IN_MONO = 1
        // AudioFormat.CHANNEL_IN_STEREO = 2
        private const val CHANNEL_COUNT = 1

        // AudioFormat.ENCODING_PCM_8BIT = 2
        // AudioFormat.ENCODING_PCM_16BIT = 2
        private const val SAMPLE_WIDTH = 2

        private const val BUFFER_SIZE_MULTIPLIER = 8

        private val BUFFER_SIZE = AudioRecord.getMinBufferSize(
            SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT
        ) * BUFFER_SIZE_MULTIPLIER
    }

    private var instance: AudioRecord? = null
    private var job: Deferred<ByteArray?>? = null

    private val lock = Any()

    private val repository = IdentifyRepository(ShazamIdentifyDataSource())

    @Throws(SecurityException::class)
    fun start() {
        createAudioRecord()
        synchronized(lock) {
            if (_idle.value != false) {
                _idle.postValue(false)
            }

            if (job == null) {
                viewModelScope.launch(Dispatchers.IO) {
                    Log.d(Constants.LOG_TAG, "IdentifyFragmentViewModel: AudioRecord.startRecording()")

                    instance?.startRecording()

                    job = async { record() }
                    try {
                        val data = job?.await()
                        val result = repository.identify(data!!)
                        _music.emit(result!!)
                    } catch (e: CancellationException) {
                        e.printStackTrace()
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        _error.emit(e.stackTraceToString())
                    }

                    stop()
                }
            }
        }
    }

    fun stop() {
        createAudioRecord()
        synchronized(lock) {
            if (job != null) {
                Log.d(Constants.LOG_TAG, "IdentifyFragmentViewModel: AudioRecord.stop()")

                instance?.stop()
                job?.cancel()
                job = null
            }

            if (_idle.value != true) {
                _idle.postValue(true)
            }
        }
    }

    private suspend fun record(): ByteArray? {
        val result = mutableListOf<Byte>()
        while (coroutineContext.isActive) {
            // Calculate the currently recorded duration from number of samples:
            // DURATION = NUMBER_OF_SAMPLES / (SAMPLE_RATE * SAMPLE_WIDTH * CHANNEL_COUNT)
            val current = result.size / (SAMPLE_RATE * SAMPLE_WIDTH * CHANNEL_COUNT)

            // Notify LiveData for UI updates.
            if (_duration.value != current) {
                _duration.postValue(current)
            }

            // The recorded duration exceeds the required duration... exit the polling loop.
            if (current >= Constants.IDENTIFY_RECORD_DURATION) {
                Log.d(Constants.LOG_TAG, "IdentifyFragmentViewModel: Record complete")
                break
            }

            val buffer = ByteArray(BUFFER_SIZE)
            instance?.read(buffer, 0, buffer.size)
            result.addAll(buffer.toList())

            Log.d(Constants.LOG_TAG, "IdentifyFragmentViewModel: current = $current")
        }

        return try {
            result.subList(
                0,
                Constants.IDENTIFY_RECORD_DURATION * SAMPLE_RATE * SAMPLE_WIDTH * CHANNEL_COUNT
            ).toByteArray()
        } catch (e: Throwable) {
            null
        }
    }

    @Throws(SecurityException::class)
    private fun createAudioRecord() {
        synchronized(lock) {
            if (instance == null) {
                instance = AudioRecord(
                    AudioSource.MIC, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, BUFFER_SIZE
                )
                Log.d(Constants.LOG_TAG, "IdentifyFragmentViewModel: AudioRecord")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
        instance?.apply {
            stop()
            release()
        }
    }
}
