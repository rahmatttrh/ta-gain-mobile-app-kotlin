package com.example.ta_gain.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.example.ta_gain.R
import com.example.ta_gain.api.RetrofitClient
import com.example.ta_gain.helper.SharedPref
import com.example.ta_gain.model.LoginResponse
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    lateinit var handler: Handler

    var statusLogin = false
    private lateinit var s:SharedPref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        s = SharedPref(this)

        val ttbAnim = AnimationUtils.loadAnimation(this, R.anim.ttb)
        val bttAnim = AnimationUtils.loadAnimation(this, R.anim.btt)
//        bgheader.startAnimation(ttbAnim)
//        bgfooter.startAnimation(bttAnim)

        btnLogin.setOnClickListener {
            login()
        }
    }

    fun login() {
        if (etEmail.text.isEmpty()) {
            etEmail.error = "Email belum di isi"
            etEmail.requestFocus()
            return
        } else if (etPass.text.isEmpty()) {
            etPass.error = "Password belum di isi"
            etPass.requestFocus()
            return
        }

//      Loadin
        loading.visibility = View.VISIBLE

        RetrofitClient.instances.login(etEmail.text.toString(), etPass.text.toString())
            .enqueue(object : Callback<LoginResponse> {
                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    loading.visibility = View.GONE
                    Toast.makeText(this@LoginActivity, "Error :" + t.message, Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>)
                {
                    loading.visibility = View.GONE
                    val res = response.body()!!
                    if (response.body()?.success == 1) {

//                      simpan data ke shared preferences
                        s.setStatusLogin(true)
                        s.setUser(s.id, res.data.id.toString())
                        s.setUser(s.nama, res.data.nama)
                        s.setUser(s.no_hp, res.data.no_hp)
                        s.setUser(s.email, res.data.email)
                        s.setUser(s.foto_ktp, res.data.foto_ktp)
                        s.setUser(s.foto_diri, res.data.foto_diri)
                        s.setUser(s.area, res.data.area)
                        s.setUser(s.no_rek, res.data.no_rek)

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
//                        Toast.makeText(
//                            this@LoginActivity,
//                            "Success:" + response.body()?.message+"nama :"+ response.body()!!.data.name,
//                            Toast.LENGTH_SHORT
//                        ).show()
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            response.body()?.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            })

    }
}