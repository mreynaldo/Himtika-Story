package com.capstone.storyappsubmission.view.detail

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

class DetailViewModel (application: Application) : AndroidViewModel(application){

    private val storyRepository: StoryRepository = Injection.provideRepository(application)

    private val _storyDetail = MutableLiveData<ListStoryItem?>()
    val storyDetail: LiveData<ListStoryItem?> = _storyDetail

    fun fetchStoryDetail(storyId: String, token: String) {
        viewModelScope.launch {
            try {
                val response = storyRepository.getDetailStories(storyId, token)

                if (response.isSuccessful) {
                    _storyDetail.value = response.body()?.story
                } else {
                    Log.e("StoryViewModel", "Failed to fetch story detail: ${response.code()}")
                    _storyDetail.value = null
                }
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Error: ${e.message}")
                _storyDetail.value = null
            }
        }
    }
}