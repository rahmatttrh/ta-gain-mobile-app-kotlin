package com.example.ta_gain.api

import com.example.ta_gain.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface Api {

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email:String,
        @Field("password") password:String
    ):Call<LoginResponse>

    @FormUrlEncoded
    @PATCH("update-loc/{email}")
    fun setLoc(
        @Path("email")email:String,
        @Field("latitude")latitude:String,
        @Field("longitude")longitude:String
    ):Call<LocationResponse>

    @GET("orders/{email}")
    fun getOrders(@Path("email")email:String): Call<OrderResponse>

    @GET("report/{email}")
    fun getOrdersReport(@Path("email")email:String): Call<OrderResponse>

    @GET("report/detail/{order}")
    fun getReportList(@Path("order")order:String): Call<ReportResponse>

    @GET("report-rev/{job}")
    fun getReportPhoto(@Path("job")job:Int): Call<ReportDetailResponse>

    @GET("order/{id}")
    fun showOrder(@Path("id")id:Int):Call<ShowOrderResponse>

    @Multipart
    @POST("upload")
    fun uploadFotoReport(
            @Part("order_id") order_id:RequestBody,
            @Part("pelanggan_id") pelanggan_id:RequestBody,
            @Part("teknisi_id") teknisi_id:RequestBody,
            @Part foto_pekerjaan: MultipartBody.Part,
            @Part("judul_foto") judul_foto:RequestBody,
            @Part("deskripsi_foto") deskripsi:RequestBody

    ):Call<UploadResponse>


    @Multipart
    @POST("report-update/{job}")
    fun updateFotoReport(
        @Path("job")job:Int,
        @Part("order_id") order_id:RequestBody,
        @Part("pelanggan_id") pelanggan_id:RequestBody,
        @Part("teknisi_id") teknisi_id:RequestBody,
        @Part foto_pekerjaan: MultipartBody.Part,
        @Part("judul_foto") judul_foto:RequestBody,
        @Part("deskripsi_foto") deskripsi:RequestBody

    ):Call<UploadResponse>


    @GET("order-finish/{email}")
    fun getFinishOrder(@Path("email")email: String): Call<OrderResponse>

    @GET("order-payment/{email}")
    fun getPaymentOrder(@Path("email")email: String):Call<OrderResponse>

    @GET("order-complete/{email}")
    fun getCompleteOrder(@Path("email")email: String):Call<OrderResponse>

//   Download Report
    @GET("download-order/{order}")
    fun downloadReport(@Path("order")order :String):Call<DownloadResponse>


}