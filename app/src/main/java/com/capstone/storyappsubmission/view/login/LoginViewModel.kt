package com.capstone.storyappsubmission.view.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.viewModelScope
import com.capstone.storyappsubmission.data.Results
import com.capstone.storyappsubmission.data.remote.response.LoginResponse
import com.capstone.storyappsubmission.data.datastore.MyApplication
import com.capstone.storyappsubmission.data.remote.response.LoginResult
import com.capstone.storyappsubmission.data.repository.StoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: StoryRepository) : ViewModel() {

    fun login(email: String, password: String, onSuccess: (LoginResult) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            when (val results = repository.login(email, password)) {
                is Results.Loading -> {
                    // Do nothing
                }
                is Results.Success -> {
                    val loginResult = results.data.loginResult
                    if (loginResult != null) {
                        repository.saveToken(loginResult.token ?: "")
                        onSuccess(loginResult)
                    } else {
                        onError("Login failed: no results")
                    }
                }
                is Results.Error -> {
                    onError(results.error)
                }
            }
        }
    }
}