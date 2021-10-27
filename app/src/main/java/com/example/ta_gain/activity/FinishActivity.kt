package com.example.ta_gain.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ta_gain.R
import com.example.ta_gain.adapter.OrderAdapter
import com.example.ta_gain.api.RetrofitClient
import com.example.ta_gain.helper.SharedPref
import com.example.ta_gain.model.Order
import com.example.ta_gain.model.OrderResponse
import kotlinx.android.synthetic.main.activity_finish.*
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.activity_order.btnHome
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FinishActivity : AppCompatActivity() {
    private val list = ArrayList<Order>()
    private lateinit var s: SharedPref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finish)

        btnHome.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        s = SharedPref(this)


        loadingFinish.visibility = View.VISIBLE
        rvFinish.setHasFixedSize(true)
        rvFinish.layoutManager = LinearLayoutManager(this)
        RetrofitClient.instances.getFinishOrder(s.getUser(s.email)).enqueue(object : Callback<OrderResponse>{
            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                Log.d("Goes App", "Error : "+t.message)
            }

            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                loadingFinish.visibility = View.GONE
                val listResponse = response.body()?.orders
                listResponse?.let { list.addAll(it) }
                val adapter = OrderAdapter(list)
                rvFinish.adapter = adapter

                adapter.setOnItemClickCallback(object: OrderAdapter.OnItemClickCallback{
                    override fun onItemClicked(data: Order) {

                        val intent = Intent(this@FinishActivity, DetailOrderActivity::class.java)
                        intent.putExtra("id", data.id)
                        startActivity(intent)
                    }

                })
            }
        })
    }
}