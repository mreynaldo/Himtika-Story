package com.capstone.storyappsubmission.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.capstone.storyappsubmission.data.Results
import com.capstone.storyappsubmission.data.local.StoryDatabase
import com.capstone.storyappsubmission.data.local.StoryEntity
import com.capstone.storyappsubmission.data.preference.UserPreference
import com.capstone.storyappsubmission.data.remote.response.FileUploadResponse
import com.capstone.storyappsubmission.data.remote.retrofit.ApiService
import com.capstone.storyappsubmission.data.remote.response.ListStoryItem
import com.capstone.storyappsubmission.data.remote.response.LoginResponse
import com.capstone.storyappsubmission.data.remote.response.RegisterResponse
import com.capstone.storyappsubmission.data.remote.response.StoryDetailResponse
import com.capstone.storyappsubmission.data.remote.response.StoryResponse
import com.capstone.storyappsubmission.data.remote.retrofit.ApiConfig
import com.capstone.storyappsubmission.data.remote.retrofit.StoriesRemoteMediator
import com.capstone.storyappsubmission.helper.wrapAppIdlingResource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class StoryRepository private constructor(
    private val apiService: ApiService,
    private val pref: UserPreference,
    private val database: StoryDatabase,
) {

    suspend fun login(email: String, password: String): Results<LoginResponse> {
        return wrapAppIdlingResource {
            try {
                val response = apiService.login(email, password)
                Results.Success(response)
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, LoginResponse::class.java)
                Results.Error(errorBody.message.toString())
            } catch (e: Exception) {
                Results.Error(e.message ?: "An error occurred")
            }
        }
    }

    suspend fun saveToken(token: String) {
        pref.saveUserToken(token)
    }

    suspend fun register(name: String, email: String, password: String): Results<RegisterResponse> {
        return try {
            val response = apiService.register(name, email, password)
            Results.Success(response)
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, RegisterResponse::class.java)
            Results.Error(errorBody.message.toString())
        } catch (e: Exception) {
            Results.Error(e.message ?: "An error occurred")
        }
    }

    fun getPagingStories(): LiveData<PagingData<StoryEntity>> = liveData {
        val token = pref.getUserToken().first()
        @OptIn(ExperimentalPagingApi::class)
        emitSource(
            Pager(
                config = PagingConfig(pageSize = 5),
                remoteMediator = token?.let { StoriesRemoteMediator(database, apiService, it) },
                pagingSourceFactory = { database.storyDao().getAllStory() }
            ).liveData
        )
    }

    fun getDetailStories(storyId: String): LiveData<Results<ListStoryItem>> = liveData {
        emit(Results.Loading)
        try {
            val token = pref.getUserToken().first()
            Log.d("AppRepository", "Bearer token: $token")
            val response = apiService.getDetailStories(
                token = "Bearer $token",
                storyId = storyId
            )
            Log.d("DetailStory", response.toString())
            val story = response.story
            if (story != null) {
                emit(Results.Success(story))
            } else {
                emit(Results.Error("Story not found"))
            }
        } catch (e: Exception) {
            Log.d("DetailStory", e.toString())
            emit(Results.Error(e.message.toString()))
        }
    }

    suspend fun uploadStory(
        description: RequestBody,
        photo: MultipartBody.Part,
        lat: RequestBody?,
        lon: RequestBody?
    ): Results<FileUploadResponse> {
        return try {
            val token = pref.getUserToken().first()
            Log.d("AppRepository", "Bearer token: $token")
            val response = apiService.uploadStory("Bearer $token", photo, description, lat, lon)

            if (!response.error!!) {
                Results.Success(response)
            } else {
                Results.Error(response.message ?: "Unknown error occurred")
            }
        } catch (e: IOException) {
            Results.Error("Network error: ${e.message}")
        } catch (e: HttpException) {
            Results.Error("HTTP error: ${e.message}")
        } catch (e: Exception) {
            Results.Error("An unexpected error occurred: ${e.message}")
        }
    }

    suspend fun getAllStoriesWidget(): Results<List<ListStoryItem>> {
        return try {
            val token = pref.getUserToken().first()
            val response = apiService.getStories(
                token = "Bearer $token",
                page = null,
                size = null,
                location = 0
            )
            Results.Success(response.listStory)
        } catch (e: Exception) {
            Results.Error(e.message.toString())
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

        fun getInstance(apiService: ApiService, pref: UserPreference, database: StoryDatabase): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, pref, database).also { instance = it }
            }
    }
}
