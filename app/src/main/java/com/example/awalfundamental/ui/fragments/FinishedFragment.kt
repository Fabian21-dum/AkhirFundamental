package com.example.awalfundamental.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.awalfundamental.databinding.FragmentFinishedBinding
import com.example.awalfundamental.ui.EventAdapter
import com.example.awalfundamental.ui.SettingPreferences
import com.example.awalfundamental.ui.dataStore
import com.example.awalfundamental.ui.viewmodels.FinishedViewModel
import com.example.awalfundamental.ui.viewmodels.ViewModelFactory

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!

    private lateinit var eventAdapter: EventAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPref = SettingPreferences.getInstance(requireContext())
        val factory: ViewModelFactory = ViewModelFactory.getInstance(requireContext(), sharedPref)
        val finishedViewModel: FinishedViewModel by viewModels { factory }

        setupRecyclerView(finishedViewModel)
        observeViewModel(finishedViewModel)

        finishedViewModel.fetchFinishedEvents()
    }

    private fun setupRecyclerView(finishedViewModel: FinishedViewModel) {

        eventAdapter = EventAdapter { event ->
            if (event.isBookmarked) {
                finishedViewModel.deleteEvent(event)
            } else {
                finishedViewModel.saveEvent(event)
            }
        }
        binding.rvFinished.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFinished.adapter = eventAdapter
    }

    private fun observeViewModel(finishedViewModel: FinishedViewModel) {
        finishedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.rvFinished.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        finishedViewModel.finishedEvents.observe(viewLifecycleOwner) { events ->
            if (events.isNotEmpty()) {
                eventAdapter.submitList(events)
            } else {
                Toast.makeText(requireContext(), "No finished events available", Toast.LENGTH_SHORT).show()
            }
        }
    }
}