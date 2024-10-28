package com.example.awalfundamental.ui.tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.awalfundamental.ui.SettingPreferences
import kotlinx.coroutines.launch

class DataStoreViewModel (private val settingPreferences: SettingPreferences) : ViewModel() {

    val darkMode = settingPreferences.getThemeSetting().asLiveData()

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            settingPreferences.saveThemeSetting(enabled)
        }
    }

    class Factory(private val settingPreferences: SettingPreferences) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DataStoreViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DataStoreViewModel(settingPreferences) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}