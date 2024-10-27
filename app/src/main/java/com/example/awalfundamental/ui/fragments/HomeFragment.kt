package com.example.awalfundamental.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.awalfundamental.databinding.FragmentHomeBinding
import com.example.awalfundamental.ui.EventAdapter
import com.example.awalfundamental.ui.SettingPreferences
import com.example.awalfundamental.ui.dataStore
import com.example.awalfundamental.ui.viewmodels.HomeViewModel
import com.example.awalfundamental.ui.viewmodels.ViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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
        val upcomingViewModel: HomeViewModel by viewModels { factory }

        setupRecyclerView(upcomingViewModel)
        observeViewModel(upcomingViewModel)

        upcomingViewModel.fetchUpcomingEvents()
    }


    private fun setupRecyclerView(upcomingViewModel: HomeViewModel) {

        eventAdapter = EventAdapter { event ->
            if (event.isBookmarked) {
                upcomingViewModel.deleteEvent(event)
            } else {
                upcomingViewModel.saveEvent(event)
            }
        }
        binding.rvHome.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHome.adapter = eventAdapter
    }

    private fun observeViewModel(upcomingViewModel: HomeViewModel) {
        upcomingViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.rvHome.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        upcomingViewModel.upcomingEvents.observe(viewLifecycleOwner) { events ->
            if (events.isNotEmpty()) {
                eventAdapter.submitList(events)
            } else {
                Toast.makeText(requireContext(), "No upcoming events available", Toast.LENGTH_SHORT).show()
            }
        }
    }
}