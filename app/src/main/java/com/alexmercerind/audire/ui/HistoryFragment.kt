package com.alexmercerind.audire.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexmercerind.audire.AboutActivity
import com.alexmercerind.audire.R
import com.alexmercerind.audire.adapters.HistoryItemAdapter
import com.alexmercerind.audire.databinding.FragmentHistoryBinding
import com.google.android.material.appbar.MaterialToolbar

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val historyViewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        binding.historyLinearLayout.visibility = View.GONE
        binding.historyRecyclerView.visibility = View.GONE

        binding.historyRecyclerView.layoutManager = LinearLayoutManager(context)

        historyViewModel.getAll().observe(viewLifecycleOwner) {

            binding.historyRecyclerView.adapter = HistoryItemAdapter(it, historyViewModel)

            if (it.isEmpty()) {
                binding.historyLinearLayout.visibility = View.VISIBLE
                binding.historyRecyclerView.visibility = View.GONE
            } else {
                binding.historyLinearLayout.visibility = View.GONE
                binding.historyRecyclerView.visibility = View.VISIBLE
            }
        }

        binding.root.findViewById<MaterialToolbar>(R.id.primaryMaterialToolbar).setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.settings -> {
                    // TODO:
                }

                R.id.about -> {
                    val intent = Intent(context, AboutActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
