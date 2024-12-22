package com.capstone.storyappsubmission.view.detail

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import android.view.Window
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.capstone.storyappsubmission.R
import com.capstone.storyappsubmission.data.remote.response.ListStoryItem
import com.capstone.storyappsubmission.data.datastore.MyApplication
import com.capstone.storyappsubmission.databinding.ActivityDetailBinding
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel: DetailViewModel by viewModels()

    private val tokenKey = stringPreferencesKey("user_token")

    private val dataStore by lazy {
        (applicationContext as MyApplication).dataStore
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            fetchStoryDetails()
        }

        window.enterTransition = TransitionInflater.from(this).inflateTransition(android.R.transition.fade)

        val imgItemPhoto = findViewById<ShapeableImageView>(R.id.imgItemPhoto)
        val tvItemName = findViewById<TextView>(R.id.tvItemName)
        val tvItemDescription = findViewById<TextView>(R.id.tvItemDescription)

        val transitionName = ViewCompat.getTransitionName(imgItemPhoto)

        if (transitionName != null) {
            imgItemPhoto.transitionName = transitionName
            tvItemName.transitionName = ViewCompat.getTransitionName(tvItemName)
            tvItemDescription.transitionName = ViewCompat.getTransitionName(tvItemDescription)
        }

        supportPostponeEnterTransition()

        supportStartPostponedEnterTransition()

    }

    private suspend fun getToken(): String? {
        val preferences = dataStore.data.first()
        return preferences[tokenKey]
    }

    private suspend fun fetchStoryDetails() {
        val storyId = intent.getStringExtra("STORY_ID")
        val token = getToken()

        showLoading(true)

        if (storyId != null) {
            if (token != null) {
                detailViewModel.fetchStoryDetail(storyId, token)
            }

            // Mengamati LiveData dari ViewModel
            detailViewModel.storyDetail.observe(this@DetailActivity, { story ->
                if (story != null) {
                    displayStoryDetail(story)
                } else {
                    Toast.makeText(
                        this@DetailActivity,
                        "Failed to fetch story details",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                showLoading(false)
            })
        } else {
            Toast.makeText(this@DetailActivity, "Invalid Story ID", Toast.LENGTH_SHORT).show()
            showLoading(false)
        }
    }

    private fun displayStoryDetail(story: ListStoryItem) {
        binding.apply {
            tvItemName.text = story.name
            tvItemDescription.text = story.description
            Glide.with(this@DetailActivity)
                .load(story.photoUrl)
                .into(imgItemPhoto)
        }

    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}