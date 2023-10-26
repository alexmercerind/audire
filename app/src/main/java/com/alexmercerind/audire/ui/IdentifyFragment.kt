package com.alexmercerind.audire.ui

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.alexmercerind.audire.databinding.FragmentIdentifyBinding
import com.alexmercerind.audire.utils.Constants

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
    ): View? {
        _binding = FragmentIdentifyBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.recordFloatingActionButton.setOnClickListener {
            viewModel.start()

            hideRecordFloatingActionButton()
        }

        binding.stopButton.setOnClickListener {
            viewModel.stop()

            showRecordFloatingActionButton()
        }

        viewModel.seconds.observe(requireActivity()) {
            binding.stopButton.text = String.format("00:%02d", it)
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

            start()
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

            addListener(object : AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    binding.stopButton.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animator) {
                    binding.stopButton.visibility = View.VISIBLE
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }

        visibilityWaveViewObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(
            binding.waveView,
            PropertyValuesHolder.ofFloat(View.ALPHA, 0.0F, 1.0F),
        ).apply {
            duration = 500L
            interpolator = AccelerateDecelerateInterpolator()

            addListener(object : AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    binding.waveView.visibility = View.VISIBLE
                }

                override fun onAnimationEnd(animation: Animator) {
                    binding.waveView.visibility = View.VISIBLE
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }

        return view
    }

    private fun showRecordFloatingActionButton() {
        idleFloatingActionButtonObjectAnimator.start()
        visibilityRecordFloatingActionButtonObjectAnimator.start()
        visibilityStopButtonObjectAnimator.reverse()
        visibilityWaveViewObjectAnimator.reverse()
    }

    private fun hideRecordFloatingActionButton() {
        idleFloatingActionButtonObjectAnimator.cancel()
        visibilityRecordFloatingActionButtonObjectAnimator.reverse()
        visibilityStopButtonObjectAnimator.start()
        visibilityWaveViewObjectAnimator.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
