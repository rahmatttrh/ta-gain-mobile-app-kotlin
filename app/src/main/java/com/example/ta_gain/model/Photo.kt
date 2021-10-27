package com.example.ta_gain.model

data class Photo (
        val id:Int?,
        val order_id:Int?,
        val site:String,
        val pelanggan_id:Int?,
        val client_name:String?,
        val teknisi_id:Int?,
        val teknisi_nama:String?,
        val foto_pekerjaan:String,
        val judul_foto:String?,
        val deskripsi_foto:String?,
        val keterangan:String?,
        val status:String?,
        val info_status:String,
        val date_created_at:String?,
        val time_created_at:String?,
        val date_updated_at:String?,
        val time_updated_at:String?
)