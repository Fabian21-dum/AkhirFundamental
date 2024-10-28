package com.example.awalfundamental.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.awalfundamental.databinding.FragmentSettingsBinding
import com.example.awalfundamental.ui.SettingPreferences
import com.example.awalfundamental.ui.viewmodels.SettingsViewModel
import com.example.awalfundamental.ui.viewmodels.ViewModelFactory
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root

        lifecycleScope.launch {
            SettingPreferences.getInstance(requireContext()).getThemeSetting().collect { isDarkMode ->
                binding.switchDm.isChecked = isDarkMode
            }
        }

        binding.switchDm.setOnCheckedChangeListener { _, isChecked ->
            lifecycleScope.launch {
                SettingPreferences.getInstance(requireContext()).saveThemeSetting(isChecked)
            }
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        return view
    }
}
//
//_binding = FragmentSettingsBinding.inflate(inflater, container, false)
//val view = binding.root
//val pref = SettingPreferences.getInstance(application.dataStore)
//val mainViewModel = ViewModelProvider(this, ViewModelFactory(pref)).get(
//    SettingsViewModel::class.java
//)
//mainViewModel.getThemeSettings().observe(this) { isDarkModeActive: Boolean ->
//    if (isDarkModeActive) {
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//        binding.switchDm.isChecked = true
//    } else {
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        binding.switchDm.isChecked = false
//    }
//}
//
//binding.switchDm.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
//    mainViewModel.saveThemeSetting(isChecked)
//}