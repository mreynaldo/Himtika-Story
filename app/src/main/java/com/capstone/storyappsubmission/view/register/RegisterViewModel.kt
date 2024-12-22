package com.capstone.storyappsubmission.view.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.capstone.storyappsubmission.data.remote.response.RegisterResponse
import kotlinx.coroutines.Dispatchers

class RegisterViewModel (private val registerRepository: RegisterRepository) : ViewModel() {

    fun register(name: String, email: String, password: String) = liveData(Dispatchers.IO) {
        try {
            val response = registerRepository.register(name, email, password)
            emit(response)
        } catch (e: Exception) {
            emit(RegisterResponse(error = true, message = e.message ?: "Unknown Error"))
        }
    }
}