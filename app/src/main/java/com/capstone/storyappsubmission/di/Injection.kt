package com.capstone.storyappsubmission.di

import android.content.Context
import com.capstone.storyappsubmission.data.remote.retrofit.ApiConfig
import com.capstone.storyappsubmission.data.repository.StoryRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import com.capstone.storyappsubmission.data.datastore.MyApplication
import com.capstone.storyappsubmission.data.local.StoryDatabase
import com.capstone.storyappsubmission.data.preference.UserPreference
import com.capstone.storyappsubmission.view.dataStore


object Injection {
    fun provideAppRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val pref = UserPreference.getInstance(context.dataStore)
        val database = StoryDatabase.getDatabase(context)
        return StoryRepository.getInstance(apiService, pref, database)
    }
}
