package com.alexmercerind.audire.ui

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder.AudioSource
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexmercerind.audire.utils.Constants
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext


class IdentifyFragmentViewModel : ViewModel() {
    // Currently recorded duration in seconds.
    val duration = MutableLiveData<Int>(0)

    // Whether recording is under process.
    val recording = MutableLiveData<Boolean>(false)

    companion object {
        private const val DURATION = 12
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

    @Throws(SecurityException::class)
    fun start() {
        createAudioRecord()
        synchronized(lock) {
            if (job == null) {
                viewModelScope.launch(Dispatchers.IO) {
                    Log.d(Constants.LOG_TAG, "IdentifyFragmentViewModel: AudioRecord.startRecording()")
                    instance?.startRecording()

                    recording.postValue(true)

                    job = async { record() }
                    try {
                        val result = job?.await()
                        if (result != null) {
                            // TODO: Handle result.
                        }
                    } catch (e: Throwable) {
                        e.printStackTrace()
                    }
                    Log.d(Constants.LOG_TAG, "IdentifyFragmentViewModel: AudioRecord.stop()")
                    instance?.stop()
                    job = null

                    recording.postValue(false)
                }
            }
        }
    }

    fun stop() {
        createAudioRecord()
        synchronized(lock) {
            if (job != null) {
                instance?.stop()
                job?.cancel()
                job = null

                recording.postValue(false)
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
            if (duration.value != current) {
                duration.postValue(current)
            }

            // The recorded duration exceeds the required duration... exit the polling loop.
            if (current >= DURATION) {
                Log.d(Constants.LOG_TAG, "IdentifyFragmentViewModel: Record complete")
                break
            }

            val buffer = ByteArray(BUFFER_SIZE)
            instance?.read(buffer, 0, buffer.size)
            result.addAll(buffer.toList())

            Log.d(Constants.LOG_TAG, "IdentifyFragmentViewModel: current = $current")
        }

        return try {
            result.subList(0, DURATION * SAMPLE_RATE * SAMPLE_WIDTH * CHANNEL_COUNT).toByteArray()
        } catch (e: Throwable) {
            null
        }
    }

    private fun convertByteArrayToShortArray(data: ByteArray): ShortArray {
        val result = ShortArray(data.size / 2)
        for (i in 0..result.size step 2) {
            result[i / 2] = (data[i].toInt() and 0xFF or (data[i + 1].toInt() shl 8)).toShort()
        }
        return result
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
