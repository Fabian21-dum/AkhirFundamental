package com.example.awalfundamental.ui

import android.content.Intent
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.awalfundamental.R
import com.example.awalfundamental.data.response.ListEventsItem
import com.example.awalfundamental.databinding.FragmentHomeBinding
import com.example.awalfundamental.databinding.ItemEventBinding

class EventAdapter (private val onBookmarkClick: (ListEventsItem) -> Unit
) : ListAdapter<ListEventsItem, EventAdapter.EventViewHolder>(DIFF_CALLBACK) {

    inner class EventViewHolder(private val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: ListEventsItem) {
            binding.tvItemName.text = event.name
            binding.tvItemDescription.text = Html.fromHtml(event.description, Html.FROM_HTML_MODE_COMPACT)

            Glide.with(binding.root.context)
                .load(event.imageLogo)
                .into(binding.imgItemPhoto)

            val ivBookmark = binding.favFab
            if (event.isBookmarked) {
                ivBookmark.setImageDrawable(ContextCompat.getDrawable(ivBookmark.context, R.drawable.baseline_favorite_24))
            } else {
                ivBookmark.setImageDrawable(ContextCompat.getDrawable(ivBookmark.context, R.drawable.baseline_favorite_border_24))
            }

            ivBookmark.setOnClickListener {
                onBookmarkClick(event)
            }

            binding.root.setOnClickListener {
                val context = binding.root.context
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("EVENT_ID", event.id)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
    }

    fun submitEvents(eventData: List<ListEventsItem>) {
        submitList(eventData)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListEventsItem>() {
            override fun areItemsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListEventsItem, newItem: ListEventsItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}

