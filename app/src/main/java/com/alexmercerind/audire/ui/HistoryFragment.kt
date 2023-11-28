package com.alexmercerind.audire.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexmercerind.audire.R
import com.alexmercerind.audire.adapters.HistoryItemAdapter
import com.alexmercerind.audire.databinding.FragmentHistoryBinding
import com.google.android.material.appbar.MaterialToolbar

class HistoryFragment : Fragment() {
    private lateinit var imm: InputMethodManager

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val historyViewModel: HistoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        binding.searchTextInputLayout.visibility = View.GONE
        binding.searchTextInputLayout.setEndIconOnClickListener {
            binding.primaryMaterialToolbar.visibility = View.VISIBLE
            binding.searchTextInputLayout.visibility = View.GONE
            binding.searchTextInputLayout.clearFocus()
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
        }

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

        binding.root.findViewById<MaterialToolbar>(R.id.primaryMaterialToolbar)
            .setOnMenuItemClickListener {
                if (it.itemId == R.id.search) {
                    binding.primaryMaterialToolbar.visibility = View.GONE
                    binding.searchTextInputLayout.visibility = View.VISIBLE
                    binding.searchTextInputLayout.requestFocus()
                    imm.showSoftInput(binding.searchTextInputEditText, 0)
                } else {
                    val intent = when (it.itemId) {
                        R.id.settings -> Intent(context, SettingsActivity::class.java)
                        R.id.about -> Intent(context, AboutActivity::class.java)
                        else -> null
                    }
                    if (intent != null) {
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
