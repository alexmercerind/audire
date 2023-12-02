package com.alexmercerind.audire.ui

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.alexmercerind.audire.R
import com.alexmercerind.audire.databinding.ActivityMainBinding
import com.alexmercerind.audire.services.FindMusicTileService
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // https://stackoverflow.com/a/50537193/12825435
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.content) as NavHostFragment
        binding.bottomNavigationView.setupWithNavController(navHostFragment.navController)
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                if (!FindMusicTileService.handled) {
                    FindMusicTileService.handled = true
                    val identifyViewModel by viewModels<IdentifyViewModel>()
                    identifyViewModel.start()
                }
            }
        }
    }
}
