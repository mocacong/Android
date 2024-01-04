package com.konkuk.mocacong.remote.repositories

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.konkuk.mocacong.util.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private object PreferenceKeys {
        val MEMBER_NICKNAME = stringPreferencesKey("member_nickname")
        val MEMBER_IMAGE = stringPreferencesKey("member_image")
    }

    suspend fun saveMemberNickname(nickname: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferenceKeys.MEMBER_NICKNAME] = nickname
        }
    }

    suspend fun getMemberNickname() = context.dataStore.data.map { pref->
        pref[PreferenceKeys.MEMBER_NICKNAME]
    }.first()

    suspend fun getMemberImage() = context.dataStore.data.map { pref ->
        pref[PreferenceKeys.MEMBER_IMAGE]
    }.first()


    suspend fun saveMemberImage(url: String?) {
        url?.let {
            context.dataStore.edit { prefs ->
                prefs[PreferenceKeys.MEMBER_IMAGE] = it
            }
        }
    }

}