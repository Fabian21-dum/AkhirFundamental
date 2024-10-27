package com.example.awalfundamental.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.Fragment
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.awalfundamental.R
import com.example.awalfundamental.data.FavsRepository
import com.example.awalfundamental.data.favs.AppExecutors
import com.example.awalfundamental.data.favs.FavsDao
import com.example.awalfundamental.data.response.ListEventsItem
import com.example.awalfundamental.data.retrofit.ApiService
import com.example.awalfundamental.databinding.FragmentFavoriteBinding
import com.example.awalfundamental.ui.EventAdapter
import com.example.awalfundamental.ui.SettingPreferences
import com.example.awalfundamental.ui.dataStore
import com.example.awalfundamental.ui.viewmodels.FavoriteViewModel
import com.example.awalfundamental.ui.viewmodels.ViewModelFactory


class FavoriteFragment : Fragment(R.layout.fragment_favorite) {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FavoriteViewModel
    private lateinit var adapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = SettingPreferences.getInstance(requireContext().dataStore)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext(), sharedPref)
        viewModel = ViewModelProvider(this, factory)[FavoriteViewModel::class.java]

        setupRecyclerView()
        observeViewModel()

        viewModel.fetchFavoriteEvents()
    }

    private fun setupRecyclerView() {
        adapter = EventAdapter { eventItem ->
            if (eventItem.isBookmarked) {
                viewModel.deleteFavoriteEvent(eventItem)
            } else {
                viewModel.addFavoriteEvent(eventItem)
            }
        }

        binding.rvFavorite.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFavorite.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.rvFavorite.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.favoriteEvents.observe(viewLifecycleOwner) { events ->
            if (events.isNotEmpty()) {
                val eventItems = events.map { eventEntity ->
                    ListEventsItem(
                        id = eventEntity.id,
                        name = eventEntity.name,
                        imageLogo = eventEntity.mediaCover,
                        active = eventEntity.active,
                        beginTime = eventEntity.beginTime,
                        cityName = eventEntity.cityName,
                        description = eventEntity.description,
                        endTime = eventEntity.endTime,
                        imgUrl = eventEntity.imgUrl,
                        isBookmarked = eventEntity.isBookmarked,
                        link = eventEntity.link,
                        mediaCover = eventEntity.mediaCover,
                        ownerName = eventEntity.ownerName,
                        quota = eventEntity.quota,
                        registrants = eventEntity.registrants,
                        summary = eventEntity.summary,
                        category = eventEntity.category
                    )
                }
                adapter.submitList(eventItems)
            } else {
                Toast.makeText(requireContext(), "No favorite events available", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }
}