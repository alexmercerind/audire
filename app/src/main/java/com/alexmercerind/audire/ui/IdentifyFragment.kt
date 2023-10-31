package com.alexmercerind.audire.ui

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.alexmercerind.audire.R
import com.alexmercerind.audire.databinding.FragmentIdentifyBinding
import com.alexmercerind.audire.models.Music
import com.alexmercerind.audire.utils.Constants
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class IdentifyFragment : Fragment() {
    private var _binding: FragmentIdentifyBinding? = null
    private val binding get() = _binding!!

    private val viewModel: IdentifyFragmentViewModel by viewModels()

    private lateinit var idleFloatingActionButtonObjectAnimator: ObjectAnimator
    private lateinit var visibilityRecordFloatingActionButtonObjectAnimator: ObjectAnimator
    private lateinit var visibilityStopButtonObjectAnimator: ObjectAnimator
    private lateinit var visibilityWaveViewObjectAnimator: ObjectAnimator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIdentifyBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.recordFloatingActionButton.setOnClickListener {
            // Request Manifest.permission.RECORD_AUDIO.
            requestRecordAudioPermission()
            if (checkRecordAudioPermission()) {
                viewModel.start()
            } else {
                // Manifest.permission.RECORD_AUDIO is not available.
                showRecordAudioPermissionNotAvailableDialog()
            }
        }
        binding.stopButton.setOnClickListener {
            viewModel.stop()
        }

        // https://stackoverflow.com/a/55049571/12825435
        // https://stackoverflow.com/a/70718428/12825435
        viewModel.idle.observe(viewLifecycleOwner) {
            when (it) {
                true -> animateToRecordButton()
                false -> animateToStopButton()
            }
        }
        viewModel.duration.observe(viewLifecycleOwner) {
            binding.stopButton.text = DateUtils.formatElapsedTime(it.toLong())
        }

        lifecycleScope.launch {
            viewModel.music.collect {
                // Show the MusicActivity.
                val intent = Intent(context, MusicActivity::class.java).apply {
                    putExtra(MusicActivity.MUSIC, it)
                }
                startActivity(intent)
            }
        }
        lifecycleScope.launch {
            viewModel.error.collect {
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

        if (viewModel.idle.value == true) {
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

    private fun requestRecordAudioPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), 0
        )
    }

    private fun checkRecordAudioPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireActivity(), Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showRecordAudioPermissionNotAvailableDialog() {
        MaterialAlertDialogBuilder(requireActivity(), R.style.Base_Theme_Audire_MaterialAlertDialog)
            .setTitle(R.string.identify_record_permission_alert_dialog_not_available_title)
            .setMessage(R.string.identify_record_permission_alert_dialog_not_available_message)
            .setPositiveButton(R.string.ok) { dialog, _ -> dialog?.dismiss() }
            .create()
            .show()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        viewModel.stop()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == 0) {
            Log.d(Constants.LOG_TAG, permissions.toString())
            Log.d(Constants.LOG_TAG, grantResults.toString())
        }
    }

}
