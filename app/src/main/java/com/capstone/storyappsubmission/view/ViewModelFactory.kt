package com.capstone.storyappsubmission.view

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.storyappsubmission.di.Injection
import com.capstone.storyappsubmission.data.preference.UserPreference
import com.capstone.storyappsubmission.data.repository.StoryRepository
import com.capstone.storyappsubmission.view.addstory.AddStoryViewModel
import com.capstone.storyappsubmission.view.detail.DetailViewModel
import com.capstone.storyappsubmission.view.login.LoginViewModel
import com.capstone.storyappsubmission.view.main.story.StoryViewModel
import com.capstone.storyappsubmission.view.maps.MapsViewModel
import com.capstone.storyappsubmission.view.register.RegisterViewModel

val Context.dataStore by preferencesDataStore(name = "user_preferences")

@Suppress("UNUSED_PARAMETER")
class ViewModelFactory(private val repository: StoryRepository, preferences: UserPreference) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(StoryViewModel::class.java) -> {
                StoryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(repository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }


    companion object {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory =
            instance ?: synchronized(this) {
                val preferences = UserPreference.getInstance(context.dataStore)
                instance ?: ViewModelFactory(Injection.provideAppRepository(context), preferences)
            }.also { instance = it }
    }
}