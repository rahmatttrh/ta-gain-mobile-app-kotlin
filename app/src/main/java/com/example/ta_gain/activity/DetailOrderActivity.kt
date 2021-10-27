package com.example.ta_gain.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.ta_gain.R
import com.example.ta_gain.api.RetrofitClient
import com.example.ta_gain.model.Order
import com.example.ta_gain.model.ShowOrderResponse
import com.example.ta_gain.model.TeknisiResponse
import kotlinx.android.synthetic.main.activity_detail_order.*
import kotlinx.android.synthetic.main.activity_order.*
import kotlinx.android.synthetic.main.activity_order.btnHome
import kotlinx.android.synthetic.main.item_order.*
import kotlinx.android.synthetic.main.item_order.tvSite
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailOrderActivity : AppCompatActivity() {
    private val list = ArrayList<TeknisiResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_order)

        btnHome.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        val idOrder = intent.getIntExtra("id", 0)
        loadingDetailOrder.visibility = View.VISIBLE
        RetrofitClient.instances.showOrder(idOrder).enqueue(object: Callback<ShowOrderResponse>{
            override fun onFailure(call: Call<ShowOrderResponse>, t: Throwable) {
                Log.d("Goes App", "Error: "+ t.message)
            }

            override fun onResponse(call: Call<ShowOrderResponse>, response: Response<ShowOrderResponse>) {
                loadingDetailOrder.visibility = View.GONE
                val res = response.body()!!.data
                Log.d("Goes App", "Success: "+ response.body()!!)
                tvSite.text = res.site
                tvKordinator.text = res.kordinator
                tvClientDetail.text = res.client

//                val listResponse = res.teknisi
//                list.addAll(listResponse)
                if(res.status == "7" || res.status == "8" || res.status == "9"){
                    btnOpenReport.visibility = View.VISIBLE
                } else {
                    btnOpenReport.visibility = View.GONE
                }



                    btnOpenReport.setOnClickListener {
                        val intent =
                            Intent(this@DetailOrderActivity, ListReportActivity::class.java)
                        intent.putExtra("id", res.id)
                        intent.putExtra("site", res.site)
                        startActivity(intent)
                    }


//                tvStat.text = res.status
                tvAlamat.text = res.alamat
                tvKabupaten.text = res.kabupaten
                tvProvinsi.text = res.provinsi
                tvLat.text = res.latitude
                tvLong.text = res.longitude
                tvProject.text = res.nama_projek
                tvTelpon.text = res.no_telpon
                tvPekerjaan.text = res.jenis_pekerjaan
                tvSistem.text = res.system_antena
                tvModem.text = res.modem
                tvUkuran.text = res.ukuran
                tvHarga.text = res.harga_paket.toString()
            }

        })

    }
}