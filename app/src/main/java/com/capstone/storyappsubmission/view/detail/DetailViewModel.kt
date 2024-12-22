package com.capstone.storyappsubmission.view.detail

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.storyappsubmission.data.Results
import com.capstone.storyappsubmission.di.Injection
import com.capstone.storyappsubmission.data.remote.response.ListStoryItem
import com.capstone.storyappsubmission.data.remote.response.StoryDetailResponse
import com.capstone.storyappsubmission.data.repository.StoryRepository
import kotlinx.coroutines.launch

class DetailViewModel (private val repository: StoryRepository) : ViewModel() {

    private val _story = MutableLiveData<StoryDetailResponse>()
    val story: LiveData<StoryDetailResponse> = _story

    fun getDetailStory(storyId: String): LiveData<Results<ListStoryItem>> {
        return repository.getDetailStories(storyId)
    }
}