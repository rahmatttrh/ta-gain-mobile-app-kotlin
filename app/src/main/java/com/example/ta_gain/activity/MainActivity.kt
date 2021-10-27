package com.example.ta_gain.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.ta_gain.R
import com.example.ta_gain.adapter.SliderAdapter
import com.example.ta_gain.api.RetrofitClient
import com.example.ta_gain.helper.SharedPref
import com.example.ta_gain.model.LocationResponse
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_report_list.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val PERMISSION_REQUEST = 10

class MainActivity : AppCompatActivity() {

    private var permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    private lateinit var s:SharedPref

    lateinit var locManager:LocationManager
    private var onGps = false
    private var onNetwork = false
    private var locGps : Location? = null
    private var locNetwork : Location? = null



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        s = SharedPref(this)

//      Animation
      vpSlider
        val arraySlider = ArrayList<Int>()
        arraySlider.add(R.drawable.item3)
        arraySlider.add(R.drawable.item2)
        arraySlider.add(R.drawable.item4)
        arraySlider.add(R.drawable.item8)

        val sliderAdapter= SliderAdapter(arraySlider, this)
        vpSlider.adapter = sliderAdapter

        requestPermissions(permission, PERMISSION_REQUEST)
        getLocation()

        btnFinish.setOnClickListener{
            getLocation()
        }

        tvNama.text = s.getUser(s.nama)
        tvEmail.text = s.getUser(s.email)
        val urlImage = "http://gan.codevintek.com/storage/"+s.getUser(s.foto_diri)
        Picasso.get().load(urlImage)
                .placeholder(R.drawable.user_filled_500px)
                .error(R.drawable.user_filled_500px)
                .into(circleImageView)

//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Yakin?")
//        builder.setMessage("Anda akan logout dari aplikasi ini")
//        builder.setPositiveButton("Logout"){
//            ialog, id ->
//            // User clicked Update Now button
//            Toast.makeText(this, "Updating your device",Toast.LENGTH_SHORT).show()
//        }
//        builder.setNegativeButton("Cancel"){
//            dialog, id ->
//        }
//        builder.show()

        btnLogout.setOnClickListener{
            showDialogLogout()
//            s.setStatusLogin(false)
//            val intent = Intent(this, LoginActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
//            startActivity(intent)
//            finish()
        }

        btnOrder.setOnClickListener{
            Intent(this@MainActivity, OrderActivity::class.java).also {
                startActivity(it)
            }
        }

        btnReport.setOnClickListener{
            Intent(this@MainActivity, ReportActivity::class.java).also{
                startActivity(it)
            }
        }

        btnFinish.setOnClickListener{
            Intent(this, FinishActivity::class.java).also {
                startActivity(it)
            }
        }

        btnPayment.setOnClickListener{
            Intent(this, PaymentActivity::class.java).also {
                startActivity(it)
            }
        }

        btnComplete.setOnClickListener{
            Intent(this, CompleteActivity::class.java).also {
                startActivity(it)
            }
        }

        btnWallet.setOnClickListener {
            Intent(this, WalletActivity::class.java).also {
                startActivity(it)
            }
        }
    }



    private fun getLocation(){
        locManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        onGps = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        onNetwork = locManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if(onGps || onNetwork){
            if(onGps){
                Log.d("Goes App", "GPS Location")

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }

                locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000,0F,object : LocationListener{
                    override fun onLocationChanged(location: Location) {
                        if(location!=null){
                            locGps = location
                        }
                    }
                })

                val localGps =  locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                if(localGps!=null){
                    locGps = localGps
                    Log.d("Goes App", "GPS Latitude: "+locGps!!.latitude )
                    Log.d("Goes App", "GPS Longitude: "+locGps!!.longitude )
//                    Toast.makeText(this, "GPS Location has updated", Toast.LENGTH_LONG).show()
                    setLocation()
                }
            }

            else if(onNetwork){
                Log.d("Goes App", "Network Location")
                locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000,0F,object : LocationListener{
                    override fun onLocationChanged(location: Location) {
                        if(location!=null){
                            locNetwork = location
                        }
                    }
                })
                val localNetwork =  locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if(localNetwork!=null){
                    locNetwork = localNetwork
                    Log.d("Goes App", "Network Latitude: "+locNetwork!!.latitude )
                    Log.d("Goes App", "Network Longitude: "+locNetwork!!.longitude )
//                    Toast.makeText(this, "Network Location has updated", Toast.LENGTH_LONG).show()
                    setLocationNetwork()
                }
            }

            else if(locGps!=null && locNetwork!=null){
                if (locGps!!.accuracy > locNetwork!!.accuracy){
                    Log.d("Goes App", "Network Latitude : "+ locNetwork!!.latitude)
                    Log.d("Goes App", "Network Longitude : "+ locNetwork!!.longitude)
                    setLocationNetwork()
                } else {
                    Log.d("Goes App", "GPS Latitude : "+ locGps!!.latitude)
                    Log.d("Goes App", "GPS Longitude : "+ locGps!!.longitude)
                    setLocation()
                }
            }
        } else {
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    fun setLocation(){
        RetrofitClient.instances.setLoc(s.getUser(s.email), locGps!!.latitude.toString(), locGps!!.longitude.toString()).enqueue(object: Callback<LocationResponse>{
            override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
                Log.d("Goes App", "Error : "+ t.message)
            }

            override fun onResponse(call: Call<LocationResponse>, response: Response<LocationResponse>) {
                Log.d("Goes App", "Success : GPS Location has updated")
                Log.d("Goes App", "GPS Latitude : "+ locGps!!.latitude)
                Log.d("Goes App", "GPS Longitude : "+ locGps!!.longitude)
                Toast.makeText(this@MainActivity, "GPS Location has updated", Toast.LENGTH_LONG).show()
            }

        })
    }
    fun setLocationNetwork(){
        RetrofitClient.instances.setLoc(s.getUser(s.email), locNetwork!!.latitude.toString(), locNetwork!!.longitude.toString()).enqueue(object: Callback<LocationResponse>{
            override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
                Log.d("Goes App", "Error : "+ t.message)
            }

            override fun onResponse(call: Call<LocationResponse>, response: Response<LocationResponse>) {
                Log.d("Goes App", "Success : Network Location has updated")
                Log.d("Goes App", "Network Latitude : "+ locNetwork!!.latitude)
                Log.d("Goes App", "Network Longitude : "+ locNetwork!!.longitude)
                Toast.makeText(this@MainActivity, "Network Location has updated", Toast.LENGTH_LONG).show()
            }

        })
    }

    fun showDialogLogout(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Are you sure?")
        builder.setPositiveButton("Logout"){
            ialog, id ->
            // User clicked Update Now button
//            Toast.makeText(this, "Updating your device",Toast.LENGTH_SHORT).show()
            s.setStatusLogin(false)
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
        builder.setNegativeButton("Cancel"){
            dialog, id ->
        }
        builder.show()
    }
}