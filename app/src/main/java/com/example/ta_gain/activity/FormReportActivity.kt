package com.example.ta_gain.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Path
import android.net.Uri
import android.os.Bundle
import android.os.FileUtils
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.example.ta_gain.R
import com.example.ta_gain.api.RetrofitClient
import com.example.ta_gain.helper.SharedPref
import com.example.ta_gain.helper.URIPath
import com.example.ta_gain.model.Photo
import com.example.ta_gain.model.ReportDetailResponse
import com.example.ta_gain.model.UploadResponse
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_detail_order.*
import kotlinx.android.synthetic.main.activity_form_report.*
import kotlinx.android.synthetic.main.activity_form_report.btnHome
import kotlinx.android.synthetic.main.activity_form_report.loading
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_order.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.net.URLDecoder

private const val MY_PERMISSION_REQUEST = 100
private const val PICK_IMAGE_FROM_GALLERY_REQUEST = 1
//private lateinit var byteAOS: ByteArrayOutputStream()
class FormReportActivity : AppCompatActivity() {
    lateinit var bitmap: Bitmap
    var idOrder:Int = 0
    var idJob:Int = 0
    var idClient:Int = 0
    lateinit var imgPath:String
    private lateinit var s: SharedPref
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_report)
        btnHome.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        s = SharedPref(this)
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), MY_PERMISSION_REQUEST)
        } else {

            ActivityCompat
                .requestPermissions(this,arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),MY_PERMISSION_REQUEST)

        }

        idJob = intent.getIntExtra("id job", 0)
        idOrder = intent.getIntExtra("id order", 0)
        idClient = intent.getIntExtra("id client", 0)
        val status = intent.getStringExtra("status")

        Log.d("Goes App Job", "id job: "+idJob)
        Log.d("Goes App Job", "id order: "+idOrder)
        Log.d("Goes App Job", "id client: "+idClient)
        Log.d("Goes App Job", "Status: "+status)

        if (status == "2"){
            btnUploadReport.visibility = View.GONE
            tvStatusFoto.text = "Approved"
            btnPick.setOnClickListener(){
                Toast.makeText(this,"Report has been approved!", Toast.LENGTH_SHORT).show()
            }
        } else {
            btnUploadReport.visibility = View.VISIBLE
            btnPick.setOnClickListener(){

                val intentPick = Intent(Intent.ACTION_PICK)
                intentPick.type = "image/*"

                startActivityForResult(
                        intentPick, PICK_IMAGE_FROM_GALLERY_REQUEST
                )
            }
        }

        if(idJob == 0){
            cvReportOwner.visibility= View.GONE
        } else {
            cvReportOwner.visibility= View.VISIBLE
        }


//        btnTake.setOnClickListener(){
//            val intentTake = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            startActivityForResult(
//                    intentTake, 123
//            )
//        }

//      Get report detail
        loadingFormReport.visibility = View.VISIBLE
        RetrofitClient.instances.getReportPhoto(idJob).enqueue(object : Callback<ReportDetailResponse>{
            override fun onFailure(call: Call<ReportDetailResponse>, t: Throwable) {
                Log.d("Goes App", "Error : "+t.message)
            }

            override fun onResponse(call: Call<ReportDetailResponse>, response: Response<ReportDetailResponse>) {
                Log.d("Goes App", "Success")
                loadingFormReport.visibility = View.GONE
                val res = response.body()?.foto

                if (res!=null) {
                    if (res?.status == "201"){
                        cvKeterangan.visibility = View.VISIBLE
                        tvKeterangan.text = res?.keterangan
                    } else{
                        cvKeterangan.visibility = View.GONE
                    }
                    tvNamaJob.text = "Detail Report"
                    etDetailJudulFoto.text = Editable.Factory.getInstance().newEditable(res?.judul_foto)
                    etDetailDeskripsiFoto.text = Editable.Factory.getInstance().newEditable(res?.deskripsi_foto)
                    tvReportTeknisi.text = res?.teknisi_nama
                    tvReportDate.text = res?.date_updated_at
                    tvReportTime.text = res?.time_updated_at

                    val urlImage = "http://ta-gain.codevintek.com/storage/app/" + res?.foto_pekerjaan
                    Picasso.get()
                            .load(urlImage)
                            .placeholder(R.drawable.signal)
                            .error(R.drawable.signal)
                            .into(ivFotoJob)
                }

            }

        })

        


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_FROM_GALLERY_REQUEST) {
            // I'M GETTING THE URI OF THE IMAGE AS DATA AND SETTING IT TO THE IMAGEVIEW
            Log.d("Goes App", "Pick Gallery")
            val path:Uri? = data?.getData()

            val filePatchColumn:InputStream? = path?.let { contentResolver.openInputStream(it) }
            bitmap = BitmapFactory.decodeStream(filePatchColumn)
            filePatchColumn?.close()
            ivFotoJob.setImageBitmap(bitmap)
            Log.d("Goes App", "img success "+bitmap)

            if (idOrder!=0){
                Log.d("Goes App", "tambah foto"+path)
                btnUploadReport.setOnClickListener{
                    uploadFotoReport(path!!)
                }
            } else if (idJob!=0){
                Log.d("Goes App", "edit job")
                btnUploadReport.setOnClickListener{
                    updateFotoReport(path!!)
                }
            }
        } else if (resultCode == Activity.RESULT_OK && requestCode == 123){
            Log.d("Goes App", "Take Camera")
            var path = data?.extras?.get("data") as Uri

//            val bytes = ByteArrayOutputStream()
//            bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//            val path = MediaStore.Images.Media.insertImage(applicationContext.getContentResolver(), bmp, "Title", null).toUri();


//            var file = data?.extras?.get("data") as File

//            val path:Uri? = Uri.fromFile(file)

            val filePatchColumn:InputStream? = path?.let { contentResolver.openInputStream(
                    it) }
            bitmap = BitmapFactory.decodeStream(filePatchColumn)
            filePatchColumn?.close()
            ivFotoJob.setImageBitmap(bitmap)
            Log.d("Goes App", "take img success "+bitmap)

            if (idOrder!=0){
                Log.d("Goes App", "tambah foto")
                btnUploadReport.setOnClickListener{
                    uploadFotoReport(path!!)
                }
            } else if (idJob!=0){
                Log.d("Goes App", "edit job")
                btnUploadReport.setOnClickListener{
                    updateFotoReport(path!!)
                }
            }
        }
    }

    fun getImageUri( inContext:Context,  inImage:Bitmap) {

    }


    fun uploadFotoReport(fileUri:Uri){

        Log.d("Goes App", "upload foto "+fileUri)
        val uriPathHelper = URIPath()
        val filePath = uriPathHelper.getPath(this, fileUri)
        Log.d("Goes App", "fullpath "+filePath)

        val order_id:RequestBody = RequestBody.create(MultipartBody.FORM, idOrder.toString())
        val pelanggan_id:RequestBody = RequestBody.create(MultipartBody.FORM, idClient.toString())
        val teknisi_id:RequestBody = RequestBody.create(MultipartBody.FORM, s.getUser(s.id))
        val judul_foto:RequestBody = RequestBody.create(MultipartBody.FORM, etDetailJudulFoto.text.toString())
        val deskripsi_foto:RequestBody = RequestBody.create(MultipartBody.FORM, etDetailDeskripsiFoto.text.toString())
        val keterangan:RequestBody = RequestBody.create(MultipartBody.FORM, "")
        val status:RequestBody = RequestBody.create(MultipartBody.FORM, "")

//        val path:String? = fileUri.
        val file:File = File(filePath)
//        file.mkdirs()
        Log.d("Goes App", "File : "+file)


        val foto_pekerjaan:RequestBody = RequestBody.create(MediaType.parse(
                "image/*"), file)
        val fileImg:MultipartBody.Part = MultipartBody.Part.createFormData("foto_pekerjaan", file.name, foto_pekerjaan)

        Log.d("Goes App", "id pelanggan : "+pelanggan_id)
        loadingFormReport.visibility = View.VISIBLE
        RetrofitClient.instances.uploadFotoReport(order_id,pelanggan_id,teknisi_id,fileImg,judul_foto, deskripsi_foto).enqueue(object : Callback<UploadResponse>{
            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                loading.visibility = View.GONE
                Log.d("Goes App", "Error : "+t.message)
                Toast.makeText(this@FormReportActivity, "Failed to upload.", Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                loadingFormReport.visibility = View.GONE
                Log.d("Goes App", "Berhasil uplaod : "+response.body()?.job)
//                Toast.makeText(this@FormReportActivity, "Berhasil Upload!", Toast.LENGTH_LONG).show()
                val intent = Intent(this@FormReportActivity, ListReportActivity::class.java)
                intent.putExtra("id", response.body()?.job?.order_id)
                intent.putExtra("success", true)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

        })


    }


    fun updateFotoReport(fileUri:Uri){
        Log.d("Goes App", "edit foto "+fileUri)
        val uriPathHelper = URIPath()
        val filePath = uriPathHelper.getPath(this, fileUri)

        val order_id:RequestBody = RequestBody.create(MultipartBody.FORM, idOrder.toString())
        val pelanggan_id:RequestBody = RequestBody.create(MultipartBody.FORM, idClient.toString())
        val teknisi_id:RequestBody = RequestBody.create(MultipartBody.FORM, s.getUser(s.id))
        val judul_foto:RequestBody = RequestBody.create(MultipartBody.FORM, etDetailJudulFoto.text.toString())
        val deskripsi_foto:RequestBody = RequestBody.create(MultipartBody.FORM, etDetailDeskripsiFoto.text.toString())

        val path:String? = fileUri.lastPathSegment
        val file:File = File(filePath)
        Log.d("Goes App", "File : "+file)


        val foto_pekerjaan:RequestBody = RequestBody.create(MediaType.parse(
            "image/*"), file)
        val fileImg:MultipartBody.Part = MultipartBody.Part.createFormData("foto_pekerjaan", file.name, foto_pekerjaan)


        loadingFormReport.visibility = View.VISIBLE
        RetrofitClient.instances.updateFotoReport(idJob,order_id,pelanggan_id,teknisi_id,fileImg,judul_foto, deskripsi_foto).enqueue(object : Callback<UploadResponse>{
            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                loading.visibility = View.GONE
                Log.d("Goes App", "Error Edit: "+t.message)
                Toast.makeText(this@FormReportActivity, "Gagal!"+t.message, Toast.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                loadingFormReport.visibility = View.GONE
                Log.d("Goes App", "Berhasil diupdate: "+response.body()?.job)
//                Toast.makeText(this@FormReportActivity, "Berhasil Di Ubah.", Toast.LENGTH_LONG).show()
                val intent = Intent(this@FormReportActivity, ListReportActivity::class.java)
                intent.putExtra("id", response.body()?.job?.order_id)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }

        })


    }
}