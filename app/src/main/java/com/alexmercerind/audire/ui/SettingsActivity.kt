package com.alexmercerind.audire.ui

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.alexmercerind.audire.R
import com.alexmercerind.audire.databinding.ActivitySettingsBinding
import com.alexmercerind.audire.utils.Constants
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {
    private val settingsViewModel: SettingsViewModel by viewModels()

    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val exportLauncher =
            registerForActivityResult(ActivityResultContracts.CreateDocument(getString(R.string.settings_backup_file_mime))) { uri ->
                if (uri != null) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            settingsViewModel.export(uri)
                            Snackbar.make(binding.root, R.string.settings_backup_export_success, Snackbar.LENGTH_LONG).show()
                        } catch (e: Throwable) {
                            e.printStackTrace()
                            Snackbar.make(binding.root, R.string.settings_backup_export_fail, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }
        val importLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
                if (uri != null) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            settingsViewModel.import(uri)
                            Snackbar.make(binding.root, R.string.settings_backup_import_success, Snackbar.LENGTH_LONG).show()
                        } catch (e: Throwable) {
                            e.printStackTrace()
                            Snackbar.make(binding.root, R.string.settings_backup_import_fail, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
            }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsViewModel.theme.filterNotNull().distinctUntilChanged().collect {
                    Log.d(Constants.LOG_TAG, "SettingsActivity: $it")
                    binding.settingsAppearanceThemeSupportingText.text = it
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                settingsViewModel.systemColorScheme.filterNotNull().distinctUntilChanged().collect {
                    Log.d(Constants.LOG_TAG, "SettingsActivity: $it")
                    if (binding.settingsAppearanceSystemColorSchemeMaterialSwitch.isChecked != it) {
                        binding.settingsAppearanceSystemColorSchemeMaterialSwitch.isChecked = it
                    }
                }
            }
        }

        binding.settingsAppearanceThemeLinearLayout.setOnClickListener {
            val popup = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) PopupMenu(
                this,
                binding.settingsAppearanceThemePopupMenuAnchor,
                Gravity.CENTER,
            ) else PopupMenu(
                this,
                binding.settingsAppearanceThemePopupMenuAnchor,
                Gravity.CENTER,
                com.google.android.material.R.attr.popupMenuStyle,
                0
            )
            popup.apply {
                setOnMenuItemClickListener { item ->

                    // EDIT:
                    settingsViewModel.setTheme(item.toString())

                    true
                }
                menu.add(R.string.settings_appearance_theme_light)
                menu.add(R.string.settings_appearance_theme_dark)
                menu.add(R.string.settings_appearance_theme_system)
                show()
            }

        }

        binding.settingsAppearanceSystemColorSchemeLinearLayout.setOnClickListener {

            // EDIT:
            settingsViewModel.setSystemColorScheme(!binding.settingsAppearanceSystemColorSchemeMaterialSwitch.isChecked)

            Snackbar.make(binding.root, R.string.settings_application_restart_required, Snackbar.LENGTH_LONG).apply {
                setAction(R.string.ok) { dismiss() }
                show()
            }

        }
        binding.settingsAppearanceSystemColorSchemeMaterialSwitch.setOnCheckedChangeListener { view, checked ->

            // EDIT:
            settingsViewModel.setSystemColorScheme(checked)

            if (view.isPressed) {
                Snackbar.make(binding.root, R.string.settings_application_restart_required, Snackbar.LENGTH_LONG).apply {
                    setAction(R.string.ok) { dismiss() }
                    show()
                }
            }
        }

        binding.settingsBackupExportLinearLayout.setOnClickListener {
            exportLauncher.launch(getString(R.string.settings_backup_file_name))
        }
        binding.settingsBackupImportLinearLayout.setOnClickListener {
            importLauncher.launch(arrayOf(getString(R.string.settings_backup_file_mime)))
        }
    }
}
