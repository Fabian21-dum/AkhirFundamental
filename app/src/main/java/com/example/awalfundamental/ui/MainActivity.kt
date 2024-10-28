package com.example.awalfundamental.ui

import android.os.Bundle
import android.os.StrictMode
import android.widget.CompoundButton
import androidx.activity.viewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.awalfundamental.R
import com.example.awalfundamental.data.FavsRepository
import com.example.awalfundamental.databinding.ActivityMainBinding
import com.example.awalfundamental.ui.fragments.FinishedFragment
import com.example.awalfundamental.ui.fragments.HomeFragment
import com.example.awalfundamental.ui.tools.DataStoreViewModel
import com.example.awalfundamental.ui.viewmodels.MainViewModel
import com.example.awalfundamental.ui.viewmodels.ViewModelFactory
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var dataStoreViewModel: DataStoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val settingPreferences = SettingPreferences.getInstance(this)
        val dataStoreViewModelFactory = DataStoreViewModel.Factory(settingPreferences)
        dataStoreViewModel = ViewModelProvider(this, dataStoreViewModelFactory)[DataStoreViewModel::class.java]


        val navView: BottomNavigationView = binding.navView
        lifecycleScope.launch {
            dataStoreViewModel.darkMode.observe(this@MainActivity) { isDarkTheme ->
                AppCompatDelegate.setDefaultNightMode(
                    if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES
                    else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_upcoming,
                R.id.navigation_finished,
                R.id.navigation_favorite,
                R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }
}