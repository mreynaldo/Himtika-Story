package com.capstone.storyappsubmission.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.capstone.storyappsubmission.data.Results
import com.capstone.storyappsubmission.data.UserPreference
import com.capstone.storyappsubmission.data.remote.retrofit.ApiService
import com.capstone.storyappsubmission.data.remote.response.ListStoryItem
import com.capstone.storyappsubmission.data.remote.response.StoryDetailResponse
import com.capstone.storyappsubmission.data.remote.response.StoryResponse
import kotlinx.coroutines.flow.first
import retrofit2.Response

class StoryRepository private constructor(private val apiService: ApiService, private val pref: UserPreference) {

    suspend fun getStories(token: String): Response<StoryResponse> {
        return try {
            val response = apiService.getStories("Bearer $token")
            Log.d("StoryRepository", "API Response: $response")

            if (!response.isSuccessful) {
                Log.e("StoryRepository", "Error: ${response.code()} - ${response.message()}")
                Log.e("StoryRepository", "Error body: ${response.errorBody()?.string()}")
            }

            response

        } catch (e: Exception) {
            Log.e("StoryRepository", "Error fetching stories: ${e.message}", e)
            throw Exception("Failed to fetch stories: ${e.message}")
        }
    }

    suspend fun getDetailStories(id: String,token: String): Response<StoryDetailResponse> {
        return try {
            val response = apiService.getDetailStories(id,"Bearer $token")
            Log.d("StoryRepository", "API Response: $response")

            if (!response.isSuccessful) {
                Log.e("StoryRepository", "Error: ${response.code()} - ${response.message()}")
                Log.e("StoryRepository", "Error body: ${response.errorBody()?.string()}")
            }

            response

        } catch (e: Exception) {
            Log.e("StoryRepository", "Error fetching stories: ${e.message}", e)
            throw Exception("Failed to fetch stories: ${e.message}")
        }
    }

    fun getAllStoriesWithLoc(): LiveData<Results<List<ListStoryItem>>> = liveData {
        emit(Results.Loading)
        try {
            val token = pref.getUserToken().first()
            val response = apiService.getStoriesWithLocation(
                token = "Bearer $token",
                location = 1
            )
            emit(Results.Success(response.listStory))
        } catch (e: Exception) {
            emit(Results.Error(e.message.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(apiService: ApiService, pref: UserPreference): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, pref).also { instance = it }
            }
    }
}
