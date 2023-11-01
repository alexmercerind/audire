package com.alexmercerind.audire.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.load
import coil.request.CachePolicy
import com.alexmercerind.audire.converters.toMusic
import com.alexmercerind.audire.databinding.HistoryItemBinding
import com.alexmercerind.audire.models.HistoryItem
import com.alexmercerind.audire.ui.MusicActivity

class HistoryItemAdapter(private val items: List<HistoryItem>) :
    RecyclerView.Adapter<HistoryItemAdapter.HistoryItemViewHolder>() {

    inner class HistoryItemViewHolder(val binding: HistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        HistoryItemViewHolder(
            HistoryItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        holder.binding.apply {
            val context = root.context
            coverImageView.load(
                items[position].cover,
                ImageLoader.Builder(context)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .build()
            ) {
                crossfade(true)
            }
            titleTextView.text = items[position].title
            artistTextView.text = items[position].artists
            root.setOnClickListener {
                val intent = Intent(context, MusicActivity::class.java).also {
                    it.putExtra(MusicActivity.MUSIC, items[position].toMusic())
                    context.startActivity(it)
                }
            }
        }

    }
}
