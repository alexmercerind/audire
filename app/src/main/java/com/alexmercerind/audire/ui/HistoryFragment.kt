package com.alexmercerind.audire.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.alexmercerind.audire.R
import com.alexmercerind.audire.databinding.FragmentHistoryBinding
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!

    private val historyViewModel: HistoryViewModel by viewModels()

    private lateinit var imm: InputMethodManager

    private val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            historyViewModel.term = s.toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        binding.searchLinearLayout.visibility = View.GONE
        binding.historyLinearLayout.visibility = View.GONE
        binding.historyRecyclerView.visibility = View.GONE
        binding.historyRecyclerView.layoutManager = LinearLayoutManager(context)

        imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        binding.searchTextInputLayout.visibility = View.GONE

        binding.searchTextInputLayout.setEndIconOnClickListener {
            binding.searchTextInputEditText.text?.clear()
            binding.primaryMaterialToolbar.visibility = View.VISIBLE
            binding.searchTextInputLayout.visibility = View.GONE
            binding.searchTextInputLayout.clearFocus()
            imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                historyViewModel.adapter.filterNotNull().collect {
                    if (it.items.isEmpty()) {
                        if (historyViewModel.term.isEmpty()) {
                            // No HistoryItem(s) by default.
                            binding.historyLinearLayout.visibility = View.VISIBLE
                            binding.searchLinearLayout.visibility = View.GONE
                        } else {
                            // No HistoryItem(s) due to search.
                            binding.historyLinearLayout.visibility = View.GONE
                            binding.searchLinearLayout.visibility = View.VISIBLE
                        }
                        binding.historyRecyclerView.visibility = View.GONE
                    } else {
                        // HistoryItem(s) are present i.e. RecyclerView must be VISIBLE.
                        binding.historyLinearLayout.visibility = View.GONE
                        binding.searchLinearLayout.visibility = View.GONE
                        binding.historyRecyclerView.visibility = View.VISIBLE
                        binding.historyRecyclerView.adapter = it
                    }
                }
            }
        }


        binding.primaryMaterialToolbar.setOnMenuItemClickListener {
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

    override fun onStart() {
        super.onStart()
        binding.searchTextInputEditText.addTextChangedListener(watcher)
    }

    override fun onStop() {
        super.onStop()
        binding.searchTextInputEditText.removeTextChangedListener(watcher)
        binding.searchTextInputEditText.text?.clear()

        historyViewModel.term = ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
