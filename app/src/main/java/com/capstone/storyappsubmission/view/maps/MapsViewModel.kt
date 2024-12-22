package com.capstone.storyappsubmission.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.capstone.storyappsubmission.data.Results
import com.capstone.storyappsubmission.data.remote.response.ListStoryItem
import com.capstone.storyappsubmission.data.repository.StoryRepository

class MapsViewModel(repository: StoryRepository) : ViewModel() {
    val getAllStoriesWithLocation: LiveData<Results<List<ListStoryItem>>> = repository.getAllStoriesWithLoc()
}