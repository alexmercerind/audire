package com.alexmercerind.audire.ui

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.PopupMenu
import com.alexmercerind.audire.R
import com.alexmercerind.audire.databinding.ActivitySettingsBinding
import com.alexmercerind.audire.utils.Constants

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
                    true
                }
                menu.add(R.string.settings_appearance_theme_light)
                menu.add(R.string.settings_appearance_theme_dark)
                menu.add(R.string.settings_appearance_theme_system)
                show()
            }

        }
        binding.settingsAppearanceSystemColorSchemeLinearLayout.setOnClickListener {}
        binding.settingsAppearanceSystemColorSchemeMaterialSwitch.setOnCheckedChangeListener { _, checked ->
            {}
        }
    }
}
