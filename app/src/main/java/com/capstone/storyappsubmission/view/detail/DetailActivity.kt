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
import com.capstone.storyappsubmission.helper.DateUtils
import com.capstone.storyappsubmission.view.ViewModelFactory
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra(EXTRA_STORY_ID)
        val storyName = intent.getStringExtra(EXTRA_STORY_NAME)
        val storyDescription = intent.getStringExtra(EXTRA_STORY_DESCRIPTION)
        val storyDate = intent.getStringExtra(EXTRA_STORY_DATE)
        val storyImageUrl = intent.getStringExtra(EXTRA_STORY_IMAGE_URL)

        observeViewModel(storyId, storyName, storyDescription, storyDate, storyImageUrl)


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

    private fun observeViewModel(
        storyId: String?,
        storyName: String?,
        storyDescription: String?,
        storyDate: String?,
        storyImageUrl: String?
    ) {
        if (storyId != null) {
            viewModel.getDetailStory(storyId).observe(this) { storyDetail ->
                if (storyDetail != null) {
                    binding.tvItemName.text = storyName
                    binding.tvItemDescription.text = storyDescription
                    binding.tvDetailDate.text = storyDate?.let { DateUtils.localizeDate(it) }
                    Glide.with(this)
                        .load(storyImageUrl)
                        .error(R.drawable.ic_error)
                        .into(binding.imgItemPhoto)

                    binding.imgItemPhoto.transitionName = "sharedImage"
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.error), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
        const val EXTRA_STORY_NAME = "extra_story_name"
        const val EXTRA_STORY_DESCRIPTION = "extra_story_description"
        const val EXTRA_STORY_DATE = "extra_story_date"
        const val EXTRA_STORY_IMAGE_URL = "extra_story_image_url"
    }
}