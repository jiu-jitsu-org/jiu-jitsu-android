package com.kyu.jiu_jitsu.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kyu.jiu_jitsu.data.utils.decryptFromBlob
import com.kyu.jiu_jitsu.data.utils.encryptToBlob
import dagger.hilt.android.qualifiers.ApplicationContext
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Base64

private val Context.appPrefs by preferencesDataStore(name = "jiu_jitsu_app_prefs")

object PrefKeys {
    val DARK_MODE = booleanPreferencesKey("dark_mode")
    val USERNAME  = stringPreferencesKey("username")
    val LAUNCH_COUNT = intPreferencesKey("launch_count")
    val TEST: Preferences.Key<String> = stringPreferencesKey("test")

    val USER_TOKEN: Preferences.Key<String> = stringPreferencesKey("jjp_user_token")
    val USER_NICK_NAME: Preferences.Key<String> = stringPreferencesKey("jjp_user_nick_name")
    val USER_PROFILE_IMG: Preferences.Key<String> = stringPreferencesKey("jjp_user_profile_img")
}

class SecurePreferences @Inject constructor (
    @ApplicationContext private val context: Context
) {

    suspend fun setValueToEncrypt(
        key: Preferences.Key<String>,
        plainStr: String?,
    ) {
        context.appPrefs.edit { prefs ->
            if (plainStr == null) prefs.remove(key)
            else {
                val blob = encryptToBlob(plainStr.encodeToByteArray())
                prefs[key] = Base64.getEncoder().encodeToString(blob)
            }
        }
    }

    fun getValueToDecrypt(
        key: Preferences.Key<String>,
    ): Flow<String?> = context.appPrefs.data.map { prefs ->
        prefs[key]?.let { b64 ->
            val blob = Base64.getDecoder().decode(b64)
            decryptFromBlob(blob).decodeToString()
        }
    }


}
