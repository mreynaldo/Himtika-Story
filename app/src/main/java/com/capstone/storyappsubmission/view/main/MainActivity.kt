package com.capstone.storyappsubmission.view.main

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.storyappsubmission.R
import com.capstone.storyappsubmission.data.datastore.MyApplication
import com.capstone.storyappsubmission.databinding.ActivityMainBinding
import com.capstone.storyappsubmission.view.detail.DetailActivity
import com.capstone.storyappsubmission.view.main.story.StoryAdapter
import com.capstone.storyappsubmission.view.main.story.StoryViewModel
import com.capstone.storyappsubmission.view.welcome.WelcomeActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.core.util.Pair
import androidx.paging.LoadState
import com.capstone.storyappsubmission.data.preference.UserPreference
import com.capstone.storyappsubmission.view.ViewModelFactory
import com.capstone.storyappsubmission.view.addstory.AddStoryActivity
import com.capstone.storyappsubmission.view.dataStore
import com.capstone.storyappsubmission.view.maps.MapsActivity
import com.capstone.storyappsubmission.view.widget.StoryAppWidget
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userPref: UserPreference

    private val viewModel by viewModels<StoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.maps -> {
                intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.logout -> {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Apakah Anda yakin ingin keluar?")
                    .setCancelable(false)
                    .setPositiveButton("Ya") { _, _ ->
                        lifecycleScope.launch {
                            val userPref = UserPreference.getInstance(dataStore)
                            userPref.clearToken()

                            Snackbar.make(binding.root, R.string.logout_success, Snackbar.LENGTH_SHORT)
                                .show()

                            updateWidget()

                            delay(1000)

                            val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                    }
                    .setNegativeButton("Batal") { dialog, _ ->
                        dialog.dismiss()
                    }

                val alert = builder.create()
                alert.show()

                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkLoginStatus()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storyAdapter = StoryAdapter()

        observeViewModel()
        updateWidget()

        binding.btnAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }

        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = storyAdapter
        }
    }

    private fun checkLoginStatus() {
        userPref = UserPreference.getInstance(dataStore)
        lifecycleScope.launch {
            userPref.getUserToken().collect { token ->
                if (token.isNullOrEmpty()) {
                    val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.getAllStories.observe(this) {
            storyAdapter.submitData(lifecycle, it)
        }

        storyAdapter.addLoadStateListener { loadState ->
            binding.progressBar.visibility =
                if (loadState.refresh is LoadState.Loading && storyAdapter.itemCount == 0) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

            if (loadState.refresh is LoadState.Error) {

                val error = (loadState.refresh as LoadState.Error).error
                Snackbar.make(
                    binding.root,
                    error.localizedMessage ?: getString(R.string.error_loading_data),
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction(getString(R.string.retry)) {
                        storyAdapter.retry()
                    }
                    .show()
            } else {
                binding.tvNoStories.visibility = View.GONE
            }
        }
    }

    private fun updateWidget() {
        val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(
            ComponentName(application, StoryAppWidget::class.java)
        )
        AppWidgetManager.getInstance(application)
            .notifyAppWidgetViewDataChanged(ids, R.id.stack_view)
        Log.d("Loginctivity", "Widget IDs: ${ids.joinToString()}")
        val intent = Intent(this, StoryAppWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            Log.d("Loginctivity", "Sending broadcast for widget update")
        }
        sendBroadcast(intent)
        Log.d("Loginctivity", "Broadcast sent for widget update")
    }

    override fun onResume() {
        super.onResume()
        observeViewModel()
    }

}
