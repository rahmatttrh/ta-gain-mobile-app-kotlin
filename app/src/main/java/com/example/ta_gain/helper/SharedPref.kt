package com.example.ta_gain.helper

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences

class SharedPref(activity: Activity) {
    val login = "login"
    val mypref = "MAIN_PREF"
    val sp:SharedPreferences

    val id = "id"
    val nama = "nama"
    val no_hp = "no_hp"
    val email = "email"
    val foto_ktp = "foto_ktp"
    val foto_diri = "foto_diri"
    val area = "area"
    val no_rek = "no_rek"

    init {
        sp = activity.getSharedPreferences(mypref, Context.MODE_PRIVATE)
    }

//  Status Login
    fun setStatusLogin(status:Boolean){
        sp.edit().putBoolean(login,status).apply()
    }
    fun getStatusLogin():Boolean{
        return sp.getBoolean(login, false)
    }




//   Simpan user yang login
    fun setUser(key: String, value:String){
        return sp.edit().putString(key, value).apply()
    }
    fun getUser(key:String):String{
        return sp.getString(key, "")!!
    }
}