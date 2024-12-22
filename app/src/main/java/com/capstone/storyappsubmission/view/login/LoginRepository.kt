package com.capstone.storyappsubmission.view.login

import com.capstone.storyappsubmission.data.remote.retrofit.ApiConfig
import com.capstone.storyappsubmission.data.remote.response.ErrorResponse
import com.capstone.storyappsubmission.data.remote.response.LoginResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class LoginRepository {

    suspend fun login(email: String, password: String): LoginResponse {
        return try {
            withContext(Dispatchers.IO) {
                ApiConfig.apiService.login(email, password)
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            throw Exception(errorBody.message)
        }
    }
}