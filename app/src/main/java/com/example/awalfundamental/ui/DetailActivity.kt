package com.example.awalfundamental.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.awalfundamental.R
import com.example.awalfundamental.data.favs.Favorite
import com.example.awalfundamental.databinding.ActivityDetailBinding
import com.example.awalfundamental.ui.viewmodels.DetailViewModel
import com.example.awalfundamental.ui.viewmodels.ViewModelFactory

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private lateinit var detailViewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = SettingPreferences.getInstance(this)
        val factory = ViewModelFactory.getInstance(this, sharedPref)
        detailViewModel = ViewModelProvider(this, factory)[DetailViewModel::class.java]

        val eventId = intent.getIntExtra("EVENT_ID", 0)
        if (eventId != 0){
            detailViewModel.fetchEventDetail(eventId.toString())
            observeEventDetail()
        }else{
            Toast.makeText(this, "Invalid Event ID", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnRegis.setOnClickListener{
            val regUrl = detailViewModel.eventDetail.value?.event?.link
            if(!regUrl.isNullOrEmpty()){
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(regUrl))
                startActivity(intent)
            }else{
                Toast.makeText(this, "Registration Link not Available", Toast.LENGTH_SHORT).show()
            }
        }

        detailViewModel.getFavoriteEventById(eventId).observe(this) { favoriteEvent ->
            var isFavorite = favoriteEvent != null
            updateFavoriteIcon(isFavorite)

            binding.favFab.setOnClickListener {
                isFavorite = !isFavorite
                if (isFavorite) {
                    val event = detailViewModel.eventDetail.value?.event
                    if (event != null) {
                        val favoriteEntity = Favorite(
                            id = event.id.toInt(),
                            name = event.name,
                            description = event.description,
                            ownerName = event.ownerName,
                            cityName = event.cityName,
                            quota = event.quota,
                            registrants = event.registrants,
                            imageLogo = event.imageLogo,
                            imgUrl = event.imgUrl,
                            beginTime = event.beginTime,
                            endTime = event.endTime,
                            link = event.link,
                            mediaCover = event.mediaCover,
                            summary = event.summary,
                            category = event.category,
                            active = event.active,
                            isBookmarked = true,
                            isFavorite = true
                        )
                        detailViewModel.addEventToFavorite(favoriteEntity)
                    }
                } else {
                    detailViewModel.removeEventFromFavorite(eventId)
                }
                updateFavoriteIcon(isFavorite)
            }
        }
    }

    private fun observeEventDetail() {
        detailViewModel.eventDetail.observe(this, Observer { detailResponse ->
            detailResponse?.let{
                val event = it.event

                binding.tvTitle.text = event.name
                binding.tvSummary.text = event.summary
                binding.tvDescription.text = HtmlCompat.fromHtml(event.description, HtmlCompat.FROM_HTML_MODE_LEGACY)
                Glide.with(this)
                    .load(event.imageLogo)
                    .into(binding.ivPicture)

                binding.tvCity.text = event.cityName
                binding.tvOwner.text = event.ownerName
                binding.tvCat.text = event.category
                binding.tvQuota.text = "Quota: ${event.quota}"
                binding.tvRegistrans.text = "Registrans: ${event.registrants}"
                binding.tvRemain.text = "Sisa Quota: ${event.quota - event.registrants}"
                binding.tvTime.text =  "${event.beginTime} - ${event.endTime}"

            }?:run{
                Log.e("DetailActivity", "Event detail is null")
                Toast.makeText(this, "Failed to load event details", Toast.LENGTH_SHORT).show()
            }
        })
        detailViewModel.isLoading.observe(this){ isLoading->
            binding?.progressBar?.visibility = if(isLoading) View.VISIBLE else View.GONE
        }
        }


    private fun updateFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            binding.favFab.setImageResource(R.drawable.baseline_favorite_24)
            binding.favFab.contentDescription = getString(R.string.remFavs)
        } else {
            binding.favFab.setImageResource(R.drawable.baseline_favorite_border_24)
            binding.favFab.contentDescription = getString(R.string.addFavs)
        }
    }
}