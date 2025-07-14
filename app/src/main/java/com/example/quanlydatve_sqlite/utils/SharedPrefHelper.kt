package com.example.quanlydatve_sqlite.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.quanlydatve_sqlite.networks.DatabaseHelper
import com.example.quanlydatve_sqlite.models.NguoiDung

object SharedPrefHelper {

    private const val PREF_NAME = "user_session"
    private const val KEY_USER_ID = "key_user_id"
    private const val KEY_IS_LOGGED_IN = "key_is_logged_in"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Gọi sau khi đăng nhập thành công, để ghi nhận phiên đăng nhập
     */
    fun saveLoginSession(context: Context, userId: Int) {
        getPrefs(context).edit().apply {
            putInt(KEY_USER_ID, userId)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun clearLoginSession(context: Context) {
        getPrefs(context).edit().clear().apply()
    }

    fun isLoggedIn(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUserId(context: Context): Int? {
        val id = getPrefs(context).getInt(KEY_USER_ID, -1)
        return if (id != -1) id else null
    }


    fun getCurrentUser(context: Context): NguoiDung? {
        val userId = getUserId(context)
        return if (userId != null) {
            val db = DatabaseHelper(context)
            db.getUserById(userId)
        } else {
            null
        }
    }

    fun getRole(context: Context): String? {
        val user = getCurrentUser(context)
        return user?.role
    }

    fun getUsername(context: Context): String? {
        val user = getCurrentUser(context)
        return user?.tenDangNhap
    }
}
