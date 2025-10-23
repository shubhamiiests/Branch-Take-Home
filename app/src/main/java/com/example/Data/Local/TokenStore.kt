package com.example.Data.Local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "auth")

@Singleton
class TokenStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val KEY = stringPreferencesKey("token")

    val token: Flow<String?> = context.dataStore.data.map { it[KEY] }

    suspend fun set(token: String?) {
        context.dataStore.edit { prefs ->
            if (token == null) prefs.remove(KEY) else prefs[KEY] = token
        }
    }
}
