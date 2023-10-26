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

class IdentifyFragmentViewModel : ViewModel() {

    // Currently recorded sample duration in seconds.
    val seconds = MutableLiveData<Int>()

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

    init {
        // TODO: Create AudioRecord instance after permission grant.
        synchronized(lock) {
            try {
                // AudioRecord is used instead of MediaRecorder due to low-level access to PCM frames.
                instance = AudioRecord(
                    AudioSource.MIC,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSizeInBytes
                )
            } catch (e: SecurityException) {
                // The Manifest.permission.RECORD_AUDIO is not available.
                e.printStackTrace()
            }
        }
    }

    fun start() {
        Log.d(Constants.LOG_TAG, "IdentifyFragmentViewModel: start")

        synchronized(lock) {
            if (record == null) {
                instance?.startRecording()
                viewModelScope.launch(Dispatchers.IO) {
                    record = async {
                        samples.clear()
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

                            // The recorded duration exceeds the target.
                            if (current >= DURATION) {
                                samples = samples.subList(0, DURATION * SAMPLE_RATE * 2 * 1)
                                stop()
                                break
                            }

                            val data = ByteArray(bufferSizeInBytes)
                            instance?.read(data, 0, data.size)
                            samples.addAll(data.toList())

                            Log.d(
                                Constants.LOG_TAG, "IdentifyFragmentViewModel: current=$current"
                            )
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

    override fun onCleared() {
        Log.d(Constants.LOG_TAG, "IdentifyFragmentViewModel: onCleared")

        record?.cancel()
        instance?.apply {
            stop()
            release()
        }
        super.onCleared()
    }
}
