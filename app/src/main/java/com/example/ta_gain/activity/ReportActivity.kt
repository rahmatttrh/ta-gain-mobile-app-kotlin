package com.example.ta_gain.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ta_gain.R
import com.example.ta_gain.adapter.OrderAdapter
//import com.example.goesapp.adapter.ReportAdapter
import com.example.ta_gain.api.RetrofitClient
import com.example.ta_gain.helper.SharedPref
import com.example.ta_gain.model.Order
import com.example.ta_gain.model.OrderResponse
import com.example.ta_gain.model.ReportResponse
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.android.synthetic.main.activity_report.btnHome
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ReportActivity : AppCompatActivity() {

    private val list = ArrayList<Order>()
    private lateinit var s: SharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        s = SharedPref(this)

        btnHome.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }


        loadingReport.visibility = View.VISIBLE
        rvReport.setHasFixedSize(true)
        rvReport.layoutManager = LinearLayoutManager(this)
        RetrofitClient.instances.getOrdersReport(s.getUser(s.email)).enqueue(object : Callback<OrderResponse>{
            override fun onFailure(call: Call<OrderResponse>, t: Throwable) {
                Log.d("Goes App", "Error : "+t.message)
            }

            override fun onResponse(call: Call<OrderResponse>, response: Response<OrderResponse>) {
                loadingReport.visibility = View.GONE
                Log.d("Goes App", "Success : "+response.body())
                val listResponse = response.body()?.orders
                listResponse?.let { list.addAll(it) }
                val adapter = OrderAdapter(list)
                rvReport.adapter = adapter

                adapter.setOnItemClickCallback(object: OrderAdapter.OnItemClickCallback{
                    override fun onItemClicked(data: Order) {
                        Log.d("Goes App", "data : "+data.site)
                        Log.d("Goes App", "data : "+data.client_id)
                        val intent = Intent(this@ReportActivity, ListReportActivity::class.java)
                        intent.putExtra("id", data.id)
                        intent.putExtra("site", data.site)
                        intent.putExtra("id client", data.client_id)
                        startActivity(intent)
                    }

                })
            }

        })
    }
}