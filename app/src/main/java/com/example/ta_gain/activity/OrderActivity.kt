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
import kotlinx.android.synthetic.main.activity_order.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderActivity : AppCompatActivity() {

    private val list = ArrayList<Order>()
    private lateinit var s: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        btnHome.setOnClickListener{
            val intent = Intent(this@OrderActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        s = SharedPref(this)


        loadingOrder.visibility = View.VISIBLE
//      panggil retrofit
        rvOrder.setHasFixedSize(true)
        rvOrder.layoutManager = LinearLayoutManager(this)
        RetrofitClient.instances.getOrders(s.getUser(s.email)).enqueue(object: Callback<OrderResponse>{
            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                Log.d("Goes App", "Error : "+t.message)
            }

            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                loadingOrder.visibility = View.GONE

                val listResponse = response.body()?.orders
                listResponse?.let { list.addAll(it) }
                val adapter = OrderAdapter(list)
                rvOrder.adapter = adapter

                Log.d("Goes App", " "+listResponse)

//                val resCode = response.code().toString()
//                tvCode.text = resCode
//                masukan data response ke list yang dibuat di atas
//                response.body()?.let { list.addAll(it) }
//                val adapter = OrderAdapter(list)
//                rvOrder.adapter = adapter
//
                adapter.setOnItemClickCallback(object: OrderAdapter.OnItemClickCallback{
                    override fun onItemClicked(data: Order) {

                        val intent = Intent(this@OrderActivity, DetailOrderActivity::class.java)
                        intent.putExtra("id", data.id)
                        startActivity(intent)
                    }

                })

            }
        })




    }
}