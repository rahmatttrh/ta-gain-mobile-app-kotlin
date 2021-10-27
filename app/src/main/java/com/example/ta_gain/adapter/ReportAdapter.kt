package com.example.ta_gain.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ta_gain.R
import com.example.ta_gain.model.Photo
import com.example.ta_gain.model.ReportResponse
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_order.view.*
import kotlinx.android.synthetic.main.item_report_list.view.*

class ReportAdapter(private val list: ArrayList<Photo>): RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    private var onItemClickCallback: OnItemClickCallback? = null
   lateinit var status:String
//  fungsi agar bisa dipanggil di main activity

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback){
        this.onItemClickCallback = onItemClickCallback
    }

    inner class ReportViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(photo: Photo){
            with(itemView){
                val judul = "${photo.judul_foto}"
                val status = "${photo.info_status}"

                if (photo.status == "1"){
                    cvStatusWaiting.visibility = View.VISIBLE
                    cvStatusApprove.visibility = View.GONE
                    cvStatusReject.visibility = View.GONE
//                    tvStatusWaiting.text = status
                } else if(photo.status == "2"){
                    cvStatusApprove.visibility = View.VISIBLE
                    cvStatusWaiting.visibility = View.GONE
                    cvStatusReject.visibility = View.GONE
//                    tvStatusApprove.text = status
                }else if(photo.status == "201"){
                    cvStatusReject.visibility = View.VISIBLE
                    cvStatusWaiting.visibility = View.GONE
                    cvStatusApprove.visibility = View.GONE
//                    tvStatusReject.text = status
                }

                val urlImage = "http://ta-gain.codevintek.com/storage/app/"+photo.foto_pekerjaan
                Picasso.get().load(urlImage)
                    .placeholder(R.drawable.signal)
                    .error(R.drawable.signal)
                    .resize(200, 200).centerInside()
                    .into(ivReportFoto)

                tvJudulFoto.text = judul

//                tvDeskripsiFoto.text = deskripsi

                itemView.setOnClickListener{onItemClickCallback?.onItemClicked(photo)}
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_report_list, parent, false)
        return ReportViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.bind(list[position])

    }
    interface OnItemClickCallback{
        fun onItemClicked(data: Photo)
    }

}