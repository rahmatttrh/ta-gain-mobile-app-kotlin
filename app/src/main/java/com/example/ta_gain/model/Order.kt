package com.example.ta_gain.model

import com.example.ta_gain.model.TeknisiResponse

data class Order (
    val id: Int,
    val status:String,
    val info_status: String,
    val joborder_id:Int,
    val client:String?,
    val client_id:Int,
    val kordinator:String?,
    val kordinator_id:Int,
    val teknisi: ArrayList<TeknisiResponse>?,
    val bast:String,
    val site:String,
    val provinsi:String,
    val kabupaten:String,
    val alamat:String,
    val latitude:String,
    val longitude:String,
    val nama_projek:String,
    val no_telpon:String,
    val ukuran:String,
    val system_antena:String,
    val jenis_pekerjaan:String,
    val harga_paket:Int,
    val total_reimburse:Int,
    val grand_total:Int,
    val modem:String,
    val alasan_penolakan:String?,
    val keterangan_status:String?,
    val created_at:String?,
    val updated_at:String?



)