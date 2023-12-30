package com.konkuk.mocacong.util


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

class Extensions {
    val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

}