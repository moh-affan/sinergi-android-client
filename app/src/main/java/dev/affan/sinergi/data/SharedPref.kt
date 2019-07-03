package dev.affan.sinergi.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import dev.affan.sinergi.User

class SharedPref(ctx: Context) {
    private val sharedPreferences: SharedPreferences = ctx.getSharedPreferences("p@3r_.._f*", Context.MODE_PRIVATE)
    var isLogIn: Boolean
        get() = this.sharedPreferences.getBoolean(LOGIN, false)
        set(v) = this.sharedPreferences.edit().putBoolean(LOGIN, v).apply()
    var user: User?
        get() {
            val strUsr = this.sharedPreferences.getString(USER, null)
            strUsr?.let {
                return Gson().fromJson(it, User::class.java) as User
            }
            return User()
        }
        set(usr) {
            usr?.let {
                val strUsr = Gson().toJson(usr)
                this.sharedPreferences.edit().putString(USER, strUsr).apply()
                return@let
            }
            if (usr == null)
                this.sharedPreferences.edit().putString(USER, null).apply()
        }

    companion object {
        private const val USER = "_.USER"
        private const val LOGIN = "_.LOGIN"
    }
}