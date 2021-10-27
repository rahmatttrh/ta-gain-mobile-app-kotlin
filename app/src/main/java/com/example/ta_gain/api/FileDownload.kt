package com.example.ta_gain.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface FileDownload {

    @GET
    fun getUrlDownload(@Url fileUrl:String): Call<ResponseBody>
}