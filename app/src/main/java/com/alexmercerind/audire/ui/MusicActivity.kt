package com.alexmercerind.audire.ui

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.WindowCompat
import coil.ImageLoader
import coil.disk.DiskCache
import coil.load
import coil.request.CachePolicy
import com.alexmercerind.audire.databinding.ActivityMusicBinding
import com.alexmercerind.audire.models.Music

class MusicActivity : AppCompatActivity() {
    companion object {
        const val MUSIC = "MUSIC"
    }

    private lateinit var music: Music
    private lateinit var binding: ActivityMusicBinding

    private val diskCache by lazy {
        DiskCache.Builder()
            .directory(cacheDir)
            .maxSizePercent(1.0)
            .maxSizeBytes(100 * 1024 * 1024)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // https://developer.android.com/develop/ui/views/layout/edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false

        music = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(MUSIC) as Music
        } else {
            intent.getSerializableExtra(MUSIC, Music::class.java)!!
        }

        binding.titleTextView.text = music.title
        binding.artistsTextView.text = music.artists

        if (music.year != null) {
            binding.yearChip.text = " ${music.year}"
        } else {
            binding.yearChip.visibility = View.GONE
        }
        if (music.album != null) {
            binding.albumChip.text = " ${music.album}"
        } else {
            binding.albumChip.visibility = View.GONE
        }
        if (music.year != null) {
            binding.labelChip.text = " ${music.label}"
        } else {
            binding.labelChip.visibility = View.GONE
        }
        if (music.lyrics != null) {
            binding.lyricsBodyTextView.text = music.lyrics
        } else {
            binding.lyricsTitleTextView.visibility = View.GONE
            binding.lyricsBodyTextView.visibility = View.GONE
        }


        binding.coverImageView.load(
            music.cover,
            ImageLoader.Builder(this)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .diskCache(diskCache)
                .build()
        ) {
            crossfade(true)
        }
    }
}
