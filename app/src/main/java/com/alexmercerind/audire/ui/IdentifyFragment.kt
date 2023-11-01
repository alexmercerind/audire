package com.alexmercerind.audire.ui

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.alexmercerind.audire.R
import com.alexmercerind.audire.databinding.FragmentIdentifyBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IdentifyFragment : Fragment() {
    private var _binding: FragmentIdentifyBinding? = null
    private val binding get() = _binding!!

    private val identifyViewModel: IdentifyViewModel by viewModels()
    private val historyViewModel: HistoryViewModel by viewModels()

    private lateinit var idleFloatingActionButtonObjectAnimator: ObjectAnimator
    private lateinit var visibilityRecordFloatingActionButtonObjectAnimator: ObjectAnimator
    private lateinit var visibilityStopButtonObjectAnimator: ObjectAnimator
    private lateinit var visibilityWaveViewObjectAnimator: ObjectAnimator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIdentifyBinding.inflate(inflater, container, false)
        val view = binding.root
        val launcher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it) {
                    identifyViewModel.start()
                } else {
                    showRecordAudioPermissionNotAvailableDialog()
                }
            }
        binding.recordFloatingActionButton.setOnClickListener {
            // Request Manifest.permission.RECORD_AUDIO.
            if (ActivityCompat.checkSelfPermission(
                    requireActivity(), Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                identifyViewModel.start()
            } else {
                try {
                    launcher.launch(Manifest.permission.RECORD_AUDIO)
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
        binding.stopButton.setOnClickListener {
            identifyViewModel.stop()
        }

        // https://stackoverflow.com/a/55049571/12825435
        // https://stackoverflow.com/a/70718428/12825435
        identifyViewModel.idle.observe(viewLifecycleOwner) {
            when (it) {
                true -> animateToRecordButton()
                false -> animateToStopButton()
            }
        }
        identifyViewModel.duration.observe(viewLifecycleOwner) {
            binding.stopButton.text = DateUtils.formatElapsedTime(it.toLong())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            identifyViewModel.music.collect {
                // Show the MusicActivity.
                if (isVisible) {
                    val intent = Intent(context, MusicActivity::class.java).apply {
                        putExtra(MusicActivity.MUSIC, it)
                    }
                    startActivity(intent)

                    // Add to Room database.
                    withContext(Dispatchers.IO) {
                        try {
                            historyViewModel.insert(it)
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            identifyViewModel.error.collect {
                Snackbar.make(view, R.string.identify_error, Snackbar.LENGTH_LONG).apply {
//                    setAction(R.string.identify_error_details) {
//                        MaterialAlertDialogBuilder(requireActivity(), R.style.Base_Theme_Audire_MaterialAlertDialog)
//                            .setTitle(R.string.identify_error)
//                            .setMessage(error)
//                            .setPositiveButton(R.string.ok) { dialog, _ -> dialog?.dismiss() }
//                            .create()
//                            .show()
//                    }
                    anchorView = requireActivity().findViewById(R.id.bottomNavigationView)
                    show()
                }
            }
        }

        idleFloatingActionButtonObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(
            binding.recordFloatingActionButton,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1.0F, 1.2F),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.0F, 1.2F),
        ).apply {
            duration = 2000L
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        visibilityRecordFloatingActionButtonObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(
            binding.recordFloatingActionButton,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 0.5F, 1.0F),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.5F, 1.0F),
            PropertyValuesHolder.ofFloat(View.ALPHA, 0.0F, 1.0F),
        ).apply {
            duration = 200L
            interpolator = AccelerateDecelerateInterpolator()
        }
        visibilityStopButtonObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(
            binding.stopButton,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 0.5F, 1.0F),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.5F, 1.0F),
            PropertyValuesHolder.ofFloat(View.ALPHA, 0.0F, 1.0F),
        ).apply {
            duration = 200L
            interpolator = AccelerateDecelerateInterpolator()
        }
        visibilityWaveViewObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(
            binding.waveView,
            PropertyValuesHolder.ofFloat(View.ALPHA, 0.0F, 1.0F),
        ).apply {
            duration = 500L
            interpolator = AccelerateDecelerateInterpolator()
        }

        if (identifyViewModel.idle.value == true) {
            binding.recordFloatingActionButton.scaleX = 1.0F
            binding.recordFloatingActionButton.scaleY = 1.0F
            binding.recordFloatingActionButton.alpha = 1.0F
            binding.stopButton.scaleX = 0.5F
            binding.stopButton.scaleY = 0.5F
            binding.stopButton.alpha = 0.0F
            binding.waveView.alpha = 0.0F
            idleFloatingActionButtonObjectAnimator.start()
        } else {
            binding.recordFloatingActionButton.scaleX = 0.5F
            binding.recordFloatingActionButton.scaleY = 0.5F
            binding.recordFloatingActionButton.alpha = 0.0F
            binding.stopButton.scaleX = 1.0F
            binding.stopButton.scaleY = 1.0F
            binding.stopButton.alpha = 1.0F
            binding.waveView.alpha = 1.0F
            idleFloatingActionButtonObjectAnimator.cancel()
        }

        return view
    }

    private fun animateToRecordButton() {
        idleFloatingActionButtonObjectAnimator.start()

        if (binding.recordFloatingActionButton.alpha == 0.0F) {
            visibilityRecordFloatingActionButtonObjectAnimator.start()
        }
        if (binding.stopButton.alpha == 1.0F) {
            visibilityStopButtonObjectAnimator.reverse()
        }
        if (binding.waveView.alpha == 1.0F) {
            visibilityWaveViewObjectAnimator.reverse()
        }
    }

    private fun animateToStopButton() {
        idleFloatingActionButtonObjectAnimator.cancel()

        if (binding.recordFloatingActionButton.alpha == 1.0F) {
            visibilityRecordFloatingActionButtonObjectAnimator.reverse()
        }
        if (binding.stopButton.alpha == 0.0F) {
            visibilityStopButtonObjectAnimator.start()
        }
        if (binding.waveView.alpha == 0.0F) {
            visibilityWaveViewObjectAnimator.start()
        }
    }

    private fun showRecordAudioPermissionNotAvailableDialog() {
        MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.Base_Theme_Audire_MaterialAlertDialog
        ).setTitle(R.string.identify_record_permission_alert_dialog_not_available_title)
            .setMessage(R.string.identify_record_permission_alert_dialog_not_available_message)
            .setPositiveButton(R.string.ok) { dialog, _ -> dialog?.dismiss() }.create().show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        identifyViewModel.stop()
    }

}
