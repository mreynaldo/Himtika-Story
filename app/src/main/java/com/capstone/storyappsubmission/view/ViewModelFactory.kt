package com.capstone.storyappsubmission.view

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.storyappsubmission.data.Injection
import com.capstone.storyappsubmission.data.UserPreference
import com.capstone.storyappsubmission.data.repository.StoryRepository
import com.capstone.storyappsubmission.view.maps.MapsViewModel

val Context.dataStore by preferencesDataStore(name = "user_preferences")

@Suppress("UNUSED_PARAMETER")
class ViewModelFactory(private val repository: StoryRepository, preferences: UserPreference) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
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
                instance ?: ViewModelFactory(Injection.provideRepository(context), preferences)
            }.also { instance = it }
    }
}