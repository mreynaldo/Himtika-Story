package com.capstone.storyappsubmission.view.main.story

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.capstone.storyappsubmission.data.Injection
import com.capstone.storyappsubmission.data.remote.response.ListStoryItem
import com.capstone.storyappsubmission.data.repository.StoryRepository
import kotlinx.coroutines.launch

class StoryViewModel(application: Application) : AndroidViewModel(application) {

    private val storyRepository: StoryRepository = Injection.provideRepository(application)

    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> = _stories

    fun fetchStories(token: String) {
        viewModelScope.launch {
            try {
                Log.d("StoryViewModel", "Fetching stories with token: $token")
                val response = storyRepository.getStories(token)
                if (response.isSuccessful) {
                    _stories.postValue(response.body()?.listStory)
                } else {
                    Log.e("Error", "Failed to fetch stories: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Error fetching stories: ${e.message}", e)
                _stories.postValue(emptyList())
            }
        }
    }
}

