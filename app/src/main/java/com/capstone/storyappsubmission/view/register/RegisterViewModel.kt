package com.capstone.storyappsubmission.view.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.capstone.storyappsubmission.data.Results
import com.capstone.storyappsubmission.data.remote.response.RegisterResponse
import com.capstone.storyappsubmission.data.repository.StoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _registerResult = MutableStateFlow<Results<RegisterResponse>>(Results.Loading)
    val registerResult: StateFlow<Results<RegisterResponse>> = _registerResult

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _registerResult.value = repository.register(name, email, password)
        }
    }
}