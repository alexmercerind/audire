package com.alexmercerind.audire.ui

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder.AudioSource
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexmercerind.audire.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.jvm.Throws

class IdentifyFragmentViewModel : ViewModel() {

    // Currently recorded sample duration in seconds.
    val seconds = MutableLiveData<Int>()

    // The resulting recorded audio.
    val data = MutableLiveData<ByteArray>()

    val recording
        get() = record != null

    companion object {
        // Target duration of the audio samples to be recorded.
        private const val DURATION = 10

        // Target sample rate of the audio samples to be recorded.
        // 44100 is guaranteed to work on all devices:
        // https://developer.android.com/reference/android/media/AudioRecord#AudioRecord(int,%20int,%20int,%20int,%20int)
        private const val SAMPLE_RATE = 44100
    }

    // The buffer size that may be read using AudioRecord.read.
    private val bufferSizeInBytes = AudioRecord.getMinBufferSize(
        SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT
    )

    // The AudioRecord instance.
    private var instance: AudioRecord? = null

    // The AudioRecord job running on another coroutine, collecting samples.
    private var record: Job? = null

    // Audio samples recorded so far.
    private var samples = mutableListOf<Byte>()

    // Synchronization.
    private val lock = Any()

    @Throws(SecurityException::class)
    fun start() {
        Log.d(Constants.LOG_TAG, "IdentifyFragmentViewModel: start")

        seconds.postValue(0)

        // Create the AudioRecorder instance if not already.
        create()

        synchronized(lock) {
            if (record == null) {
                viewModelScope.launch(Dispatchers.IO) {
                    record = async {
                        // Clear previously recorded samples.
                        samples.clear()
                        // Start the AudioRecord instance.
                        instance?.startRecording()

                        while (isActive) {
                            // Currently recorded duration in seconds:
                            // N / (R * W * C)
                            // N = Number of samples.
                            // R = Sample rate.
                            // W = Sample width i.e. 2 or 16BIT.
                            // C = Number of channels i.e. 1 or MONO.
                            val current = samples.size / (SAMPLE_RATE * 2 * 1)

                            // Notify LiveData.
                            if (seconds.value != current) {
                                seconds.postValue(current)
                            }

                            // COMPLETE:
                            // The recorded duration exceeds the target.
                            if (current >= DURATION) {
                                data.postValue(samples.subList(0, DURATION * SAMPLE_RATE * 2 * 1).toByteArray())
                                stop()
                                break
                            }

                            val buffer = ByteArray(bufferSizeInBytes)
                            instance?.read(buffer, 0, buffer.size)
                            samples.addAll(buffer.toList())

                            Log.d(Constants.LOG_TAG, "IdentifyFragmentViewModel: current=$current")
                        }
                    }
                }
            }
        }
    }

    fun stop() {
        Log.d(Constants.LOG_TAG, "IdentifyFragmentViewModel: stop")

        synchronized(lock) {
            if (record != null) {
                record?.cancel()
                instance?.stop()
                record = null
            }
        }
    }

    @Throws(SecurityException::class)
    private fun create() {
        synchronized(lock) {
            if (instance == null) {
                // AudioRecord is used instead of MediaRecorder due to low-level access to PCM frames.
                instance = AudioRecord(
                    AudioSource.MIC,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSizeInBytes
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        Log.d(Constants.LOG_TAG, "IdentifyFragmentViewModel: onCleared")

        record?.cancel()
        instance?.apply {
            stop()
            release()
        }
    }
}
