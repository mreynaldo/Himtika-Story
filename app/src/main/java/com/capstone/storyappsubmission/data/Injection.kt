package com.capstone.storyappsubmission.data

import android.content.Context
import com.capstone.storyappsubmission.data.remote.retrofit.ApiConfig
import com.capstone.storyappsubmission.data.repository.StoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import com.capstone.storyappsubmission.data.datastore.MyApplication


object Injection {

    fun provideRepository(context: Context): StoryRepository {
        val dataStore = (context.applicationContext as MyApplication).dataStore
        val pref = UserPreference.getInstance(dataStore)
        val userToken = runBlocking {
            pref.getUserToken().first()
        }
        val apiService = ApiConfig.getApiServiceWithAuth(userToken ?: "")
        return StoryRepository.getInstance(apiService, pref)
    }
}
