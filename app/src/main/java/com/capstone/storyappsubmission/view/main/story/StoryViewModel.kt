package com.capstone.storyappsubmission.view.main.story

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.capstone.storyappsubmission.data.local.StoryEntity
import com.capstone.storyappsubmission.di.Injection
import com.capstone.storyappsubmission.data.remote.response.ListStoryItem
import com.capstone.storyappsubmission.data.repository.StoryRepository
import kotlinx.coroutines.launch

class StoryViewModel(repository: StoryRepository) : ViewModel() {
    val getAllStories: LiveData<PagingData<StoryEntity>> =
        repository.getPagingStories().cachedIn(viewModelScope)
}

