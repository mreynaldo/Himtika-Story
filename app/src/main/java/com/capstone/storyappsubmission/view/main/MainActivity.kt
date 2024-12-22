package com.capstone.storyappsubmission.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
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
import com.capstone.storyappsubmission.view.addstory.AddStoryActivity
import com.capstone.storyappsubmission.view.maps.MapsActivity


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val tokenKey = stringPreferencesKey("user_token")

    private val dataStore by lazy {
        (applicationContext as MyApplication).dataStore
    }

    private val storyViewModel: StoryViewModel by viewModels()

    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupRecyclerView()
        checkLoginStatus()

        binding.btnAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                logout()
                true
            }
            R.id.maps -> {
                intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
            binding.root.setPadding(0, 0, 0, 0)
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter() { selectedEvent ->
            if (selectedEvent.id !=  null) {
                val intent = Intent(this, DetailActivity::class.java).apply {
                    putExtra("STORY_ID", selectedEvent.id)
                }

                val optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this,
                    Pair(binding.rvStory.findViewHolderForAdapterPosition(storyAdapter.currentList.indexOf(selectedEvent))?.itemView?.findViewById(R.id.imgItemPhoto), "profile"),
                    Pair(binding.rvStory.findViewHolderForAdapterPosition(storyAdapter.currentList.indexOf(selectedEvent))?.itemView?.findViewById(R.id.tvItemName), "name"),
                    Pair(binding.rvStory.findViewHolderForAdapterPosition(storyAdapter.currentList.indexOf(selectedEvent))?.itemView?.findViewById(R.id.tvItemDescription), "description")
                )

                startActivity(intent, optionsCompat.toBundle())
            } else {
                Log.d("StoryAdapter", "Invalid Story ID: ${selectedEvent.id}")
            }
        }
        binding.rvStory.layoutManager = LinearLayoutManager(this)
        binding.rvStory.adapter = storyAdapter
    }


    private fun checkLoginStatus() {
        lifecycleScope.launch {
            val token = getToken()

            if (token.isNullOrEmpty()) {
                val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            } else {
                fetchStories(token)
            }
        }
    }

    private suspend fun getToken(): String? {
        val preferences = dataStore.data.first()
        return preferences[tokenKey]
    }

    private fun logout() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Apakah Anda yakin ingin keluar?")
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                lifecycleScope.launch {
                    dataStore.edit { preferences ->
                        preferences.remove(tokenKey)
                    }
                }

                val intent = Intent(this, WelcomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }

        val alert = builder.create()
        alert.show()
    }

    private fun fetchStories(token: String) {
        binding.progressBar.visibility = View.VISIBLE

        storyViewModel.fetchStories(token)

        storyViewModel.stories.observe(this, { stories ->

            binding.progressBar.visibility = View.GONE

            if (stories.isNullOrEmpty()) {
                binding.tvNoStories.visibility = View.VISIBLE

            } else {
                binding.tvNoStories.visibility = View.GONE
                storyAdapter.submitList(stories)
            }
        })
    }

}
