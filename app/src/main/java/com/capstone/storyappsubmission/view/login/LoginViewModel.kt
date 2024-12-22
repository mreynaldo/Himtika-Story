package com.capstone.storyappsubmission.view.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.capstone.storyappsubmission.data.remote.response.LoginResponse
import com.capstone.storyappsubmission.data.datastore.MyApplication
import kotlinx.coroutines.Dispatchers

class LoginViewModel(private val loginRepository: LoginRepository, private val context: Context) : ViewModel() {

    private val dataStore = (context.applicationContext as MyApplication).dataStore
    private val tokenKey = stringPreferencesKey("user_token")

    fun login(email: String, password: String) = liveData(Dispatchers.IO) {
        try {
            val response = loginRepository.login(email, password)
            if (!response.error && response.loginResult != null) {
                saveToken(response.loginResult.token)
                emit(response)
            } else {
                emit(LoginResponse(error = true, message = response.message, loginResult = null))
            }
        } catch (e: Exception) {
            emit(LoginResponse(error = true, message = e.message ?: "Unknown Error", loginResult = null))
        }
    }

    private suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[tokenKey] = token
        }
    }
}