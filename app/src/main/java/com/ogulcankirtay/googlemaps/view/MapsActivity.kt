package com.ogulcankirtay.googlemaps.view

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.ogulcankirtay.googlemaps.R
import com.ogulcankirtay.googlemaps.databinding.ActivityMapsBinding
import com.ogulcankirtay.googlemaps.model.Place
import com.ogulcankirtay.googlemaps.roomdb.PlaceDao
import com.ogulcankirtay.googlemaps.roomdb.PlaceDb
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MapsActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMapLongClickListener{

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private lateinit var permissionLauncher:ActivityResultLauncher<String>
    private lateinit var sharedPreferences: SharedPreferences
    var trackBoolen: Boolean?=null
    private lateinit var SelectedLatLng: LatLng
    private lateinit var Db:PlaceDb
    private lateinit var placeDao:PlaceDao
    private lateinit var place: Place
    val compositeDisposable= CompositeDisposable()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        regPermission()

        SelectedLatLng= LatLng(0.0,0.0)
        sharedPreferences=this.getSharedPreferences("com.ogulcankirtay.googlemaps", MODE_PRIVATE)
        trackBoolen=false

        Db=Room.databaseBuilder(applicationContext,PlaceDb::class.java,"Places").build()

        placeDao=Db.PlaceDao()

    binding.button.setOnClickListener{
        save(binding.root)
    }
        binding.button2.setOnClickListener {
            delete(binding.root)
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(this)

        val intent=intent
        val info=intent.getStringExtra("info")
        if(info=="old"){
            binding.button2.visibility=View.VISIBLE
            binding.button.visibility=View.GONE
            mMap.clear()
            place=intent.getSerializableExtra("Place") as Place
            mMap.addMarker(MarkerOptions().position(LatLng(place.lat,place.lng)).title(place.name))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(place.lat,place.lng),15f))

            binding.editTextTextPersonName.setText(place.name.toString())
        }
        else{
            binding.button.visibility=View.VISIBLE
            binding.button2.visibility=View.GONE

            locationManager=this.getSystemService(LOCATION_SERVICE) as LocationManager

        locationListener=object :LocationListener{
            override fun onLocationChanged(location: Location) {
                trackBoolen=sharedPreferences.getBoolean("trackBoolen",false)
                if(trackBoolen==false){
                    val userL=LatLng(location.latitude,location.longitude)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userL,15f))
                    sharedPreferences.edit().putBoolean("trackBoolen",true).apply()
                }

            }
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
            Snackbar.make(binding.root,"Permission Needed for Location",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission"){
                //req permission
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }.show()
        }else{
            // req permission
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        }else{
            //izin verildi
             locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
             val lastlocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
             if(lastlocation!=null){
                 val lastuserLocation=LatLng(lastlocation.latitude,lastlocation.longitude)
                 mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastuserLocation,15f))
             }
            mMap.isMyLocationEnabled=true
        }
    }}
    private fun regPermission(){
        permissionLauncher=registerForActivityResult(ActivityResultContracts.RequestPermission()){ result->
            if(result){
                //izin verildi
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
                    val lastlocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if(lastlocation!=null){
                        val lastuserLocation=LatLng(lastlocation.latitude,lastlocation.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastuserLocation,15f))
                    }
                    mMap.isMyLocationEnabled=true
                }

            }else{
                //izin Verilmedi
                Toast.makeText(this,"Permission Needed",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onMapLongClick(p0: LatLng) {
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(p0))
        SelectedLatLng=p0

        println(SelectedLatLng)
    }
    private fun save(view:View){
        println("save")
        val place=Place(binding.editTextTextPersonName.text.toString(),SelectedLatLng.latitude,SelectedLatLng.longitude)
        compositeDisposable.add(
            placeDao.insert(place)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::HandleResponse)
        )

    }

    private fun delete(view :View){
        compositeDisposable.add(
            placeDao.Delete(place).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::HandleResponse)
        )
    }
    private fun HandleResponse(){
        val intent= Intent(this,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }
    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}