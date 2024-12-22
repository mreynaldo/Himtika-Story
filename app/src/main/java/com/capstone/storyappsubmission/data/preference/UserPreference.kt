package com.capstone.storyappsubmission.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences

class UserPreference(private val dataStore: DataStore<Preferences>) {

    suspend fun saveUserToken(token: String) {
        dataStore.edit { preferences ->
            preferences[USER_TOKEN_KEY] = token
        }
    }

    fun getUserToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[USER_TOKEN_KEY]
        }
    }

    suspend fun clearToken() {
        dataStore.edit { preferences ->
            preferences.remove(USER_TOKEN_KEY)
        }
    }

    companion object {
        private val USER_TOKEN_KEY = stringPreferencesKey("user_token")
        @Volatile
        private var INSTANCE: UserPreference? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
