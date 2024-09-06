package com.alexmercerind.audire.ui

import android.app.SearchManager
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import coil.ImageLoader
import coil.load
import coil.request.CachePolicy
import com.alexmercerind.audire.R
import com.alexmercerind.audire.databinding.ActivityMusicBinding
import com.alexmercerind.audire.models.Music
import com.google.android.material.snackbar.Snackbar


class MusicActivity : AppCompatActivity() {
    companion object {
        const val MUSIC = "MUSIC"
        const val SPOTIFY_PACKAGE_NAME = "com.spotify.music"
        const val YOUTUBE_SEARCH_URL = "https://www.youtube.com/results?search_query="
    }

    private lateinit var music: Music
    private lateinit var binding: ActivityMusicBinding

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
                .build()
        ) {
            crossfade(true)
        }

        binding.searchMaterialButton.setOnClickListener {
            try {
                val uri = Uri.parse("https://www.duckduckgo.com/?q=${music.createSearchQuery()}")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            } catch (e: Throwable) {
                showFailureSnackbar()
                e.printStackTrace()
            }
        }
        binding.spotifyMaterialButton.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_MAIN).apply {
                    action = MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH
                    component =
                        ComponentName(SPOTIFY_PACKAGE_NAME, "$SPOTIFY_PACKAGE_NAME.MainActivity")
                    putExtra(SearchManager.QUERY, music.createSearchQuery())
                }
                startActivity(intent)
            } catch (e: Throwable) {
                showFailureSnackbar()
                e.printStackTrace()
            }
        }
        binding.youtubeMaterialButton.setOnClickListener {
            try {
                val query = java.net.URLEncoder.encode(music.createSearchQuery(), "utf-8")
                val url = YOUTUBE_SEARCH_URL + query
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

                startActivity(intent)
            } catch (e: Throwable) {
                showFailureSnackbar()
                e.printStackTrace()
            }
        }
    }

    private fun showFailureSnackbar() {
        Snackbar.make(binding.root, R.string.action_view_error, Snackbar.LENGTH_LONG).show()
    }
}

fun Music.createSearchQuery() = "$title $artists ${year ?: ""} ${album ?: ""}"
