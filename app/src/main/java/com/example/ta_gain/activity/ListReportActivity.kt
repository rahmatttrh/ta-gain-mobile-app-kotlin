package com.example.ta_gain.activity

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ta_gain.R
import com.example.ta_gain.adapter.ReportAdapter
import com.example.ta_gain.api.FileDownload
import com.example.ta_gain.api.RetrofitClient
import com.example.ta_gain.model.DownloadResponse
import com.example.ta_gain.model.Photo
import com.example.ta_gain.model.ReportResponse
import kotlinx.android.synthetic.main.activity_list_report.*
import kotlinx.android.synthetic.main.activity_order.btnHome
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.*
import java.net.URL


private const val MY_PERMISSION_REQUEST = 100
private const val PICK_IMAGE_FROM_GALLERY_REQUEST = 1
class ListReportActivity : AppCompatActivity() {
    private val list = ArrayList<Photo>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_report)

        if (Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        val idOrder = intent.getIntExtra("id", 0)
        val idClient = intent.getIntExtra("id client", 0)
        val siteOrder = intent.getStringExtra("site")
        val success = intent.getBooleanExtra("success", false)
        tvNamaSiteReport.text = siteOrder
//        Log.d("Goes App", "order : "+idOrder)
//        Log.d("Goes App", "client : "+idClient)

        if(success){
            Toast.makeText(this, "Upload report berhasil", Toast.LENGTH_SHORT).show()
        }

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), MY_PERMISSION_REQUEST)
        } else {

            ActivityCompat
                    .requestPermissions(this,arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),MY_PERMISSION_REQUEST)

        }

        btnHome.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        btnTambahReport.setOnClickListener{
            val intent = Intent(this@ListReportActivity, FormReportActivity::class.java)
            intent.putExtra("id order", idOrder)
            intent.putExtra("id client", idClient)
            startActivity(intent)
        }

        btnDownload.setOnClickListener(){
            downloadReport(idOrder.toString())
        }

        loadingListReport.visibility = View.VISIBLE
        rvReportList.setHasFixedSize(true)
        rvReportList.layoutManager = LinearLayoutManager(this)
        RetrofitClient.instances.getReportList(idOrder.toString()).enqueue(object : Callback<ReportResponse> {
            override fun onFailure(call: Call<ReportResponse>, t: Throwable) {
                Log.d("Goes App", "Error : "+t.message)
            }

            override fun onResponse(call: Call<ReportResponse>, response: Response<ReportResponse>) {
                loadingListReport.visibility = View.GONE
                Log.d("Goes App", "Success : "+response.body()?.jobReport)
                val listResponse = response.body()?.jobReport
                listResponse?.let { list.addAll(it) }
                val adapter = ReportAdapter(list)
                rvReportList.adapter = adapter

                val order = response.body()?.order
                if(order?.status == "7" || order?.status == "8" || order?.status == "9" ){
                    btnTambahReport.visibility = View.GONE
                } else {
                    btnTambahReport.visibility = View.VISIBLE
                }

                Log.d("Goes App", "Order : "+response.body()?.order?.site)

                adapter.setOnItemClickCallback(object : ReportAdapter.OnItemClickCallback{
                    override fun onItemClicked(data: Photo) {
                        val intent = Intent(this@ListReportActivity, FormReportActivity::class.java)
                        intent.putExtra("id job", data.id)
                        intent.putExtra("id client", data.pelanggan_id)
                        intent.putExtra("status", data.status)
                        Log.d("Goes App", "Client id : "+data)
                        startActivity(intent)
                    }

                })
            }

        })
    }

    fun downloadReport(order: String){
        RetrofitClient.instances.downloadReport(order).enqueue(object :Callback<DownloadResponse>{
            override fun onFailure(call: Call<DownloadResponse>, t: Throwable) {
               Log.d("Goes App", "Error download : "+t.message)
            }

            override fun onResponse(call: Call<DownloadResponse>, response: Response<DownloadResponse>) {
                Log.d("Goes App", "berhasil get url : "+response.body())
                val link = "http://ta-gain.codevintek.com/"+response.body()?.url
                val path = getExternalFilesDir(DIRECTORY_DOWNLOADS)?.path
//                URL(link).openStream().use { input ->
//                    FileOutputStream(File(path)).use { output ->
//                        input.copyTo(output)
//                    }
//                }
//                val path = "/storage/emulated/0/Download"
//                download(link, path)
//                getFile(response.body()?.url)
                downloadMan(link)
            }
        })
    }


    fun downloadMan(link: String){
        val downloadmanager =
            this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri: Uri = Uri.parse(link)
        Log.d("Goes App", "File "+uri)
        val fileName = uri.lastPathSegment
        val request = DownloadManager.Request(uri)
        request.setTitle("Report "+fileName)
        request.setDescription("Downloading")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setVisibleInDownloadsUi(false)
        request.allowScanningByMediaScanner()
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName+".zip")

        downloadmanager.enqueue(request)
    }

    fun download(link: String, path: String?) {
        Log.d("Goes App", "Saving")
        URL(link).openStream().use { input ->

            FileOutputStream(File(path+"halo")).use { output ->
                input.copyTo(output)
            }
        }
    }

    fun getFile(url:String?){
        val retrofit = Retrofit.Builder()
                .baseUrl("http://ta-gain.codevintek.com/")
                .build()
        val downloadClient = retrofit.create(FileDownload::class.java)
        downloadClient.getUrlDownload(url!!).enqueue(object :Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d("Goes App", "Error : "+t.message)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("Goes App", "berhasil get file : "+response.body())
//                val downloadFolder = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                val externalFilesDir = getExternalFilesDir(DIRECTORY_DOWNLOADS)
                val reportFile = File(externalFilesDir,"haloo")

                saveFile(response.body(), reportFile.path)
                val save = writeResponseBodyToDisk(response.body()!!)

            }
        })
    }



    fun saveFile(body: ResponseBody?, pathWhereYouWantToSaveFile: String):String{
        Log.d("Goes App", "Saving File"+body)
        if (body==null)
            return "null"
        var input: InputStream? = null
        try {
            Log.d("Goes App", "trying")
            input = body.byteStream()
            //val file = File(getCacheDir(), "cacheFileAppeal.srl")
            val fos = FileOutputStream(pathWhereYouWantToSaveFile)
            fos.use { output ->
                val buffer = ByteArray(4 * 1024) // or other buffer size
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
            return pathWhereYouWantToSaveFile
        }catch (e:Exception){
            Log.e("saveFile",e.toString())
        }
        finally {
            input?.close()
        }
        return ""
    }


    private fun writeResponseBodyToDisk(body: ResponseBody): Boolean {
        return try {
            Log.d("Goes App", "writefile : "+body)
            // todo change the file location/name according to your needs
            val externalFilesDir = getExternalFilesDir(DIRECTORY_DOWNLOADS)
            val reportFile = File(externalFilesDir,"haloo")
            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0
                inputStream = body.byteStream()
                outputStream = FileOutputStream(reportFile)
                while (true) {
                    val read = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()

                }
                outputStream.flush()
                true
            } catch (e: IOException) {
                false
            } finally {
                inputStream?.close()
                outputStream?.close()
            }
        } catch (e: IOException) {
            false
        }
    }





}