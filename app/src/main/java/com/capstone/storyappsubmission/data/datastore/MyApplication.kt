package com.capstone.storyappsubmission.data.datastore

import android.app.Application
import androidx.datastore.preferences.preferencesDataStore

class MyApplication : Application() {
    val dataStore by preferencesDataStore(name = "user_preferences")
}