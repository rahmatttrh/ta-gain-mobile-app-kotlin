package com.example.ta_gain.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ta_gain.R
import com.example.ta_gain.model.OrderResponse
import com.example.ta_gain.model.ReportResponse
import com.example.ta_gain.model.Order
import kotlinx.android.synthetic.main.item_order.view.*

class OrderAdapter(private val list: ArrayList<Order>): RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private var onItemClickCallback: OrderAdapter.OnItemClickCallback? = null

    fun setOnItemClickCallback(onItemClickCallback: OrderAdapter.OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

     inner class OrderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(order: Order){
            with(itemView){
                val site = "${order.site}"
                val provinsi = "${order.provinsi}"

                tvSite.text = site
                tvClient.text = provinsi

                itemView.setOnClickListener{onItemClickCallback?.onItemClicked(order)}
            }
        }
     }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(list[position])
    }

    interface OnItemClickCallback{
        fun onItemClicked(data: Order)
    }
}