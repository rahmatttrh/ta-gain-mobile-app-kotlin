package com.example.ta_gain.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ta_gain.R
import com.example.ta_gain.helper.SharedPref
import com.example.ta_gain.model.Order
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_order.*

class WalletActivity : AppCompatActivity() {
    private val list = ArrayList<Order>()
    private lateinit var s: SharedPref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)
        btnHome.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        s = SharedPref(this)
        tvNama.text = s.getUser(s.nama)
        tvEmail.text = s.getUser(s.email)

    }
}