package com.ogulcankirtay.googlemaps.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.ogulcankirtay.googlemaps.R
import com.ogulcankirtay.googlemaps.adapter.PlacesAdapter
import com.ogulcankirtay.googlemaps.databinding.ActivityMainBinding
import com.ogulcankirtay.googlemaps.model.Place
import com.ogulcankirtay.googlemaps.roomdb.PlaceDb
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val compositeDisposable=CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        val view=binding.root
        setContentView(view)

        val db= Room.databaseBuilder(applicationContext,PlaceDb::class.java,"Places").build()
        val Dao=db.PlaceDao()

        compositeDisposable.add(
            Dao.getAll().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(this::HandleResponse)
        )

    }
    private fun HandleResponse(placeList: List<Place>) {
        println(placeList)
        binding.recyclerView.layoutManager=LinearLayoutManager(this)
        val adapter=PlacesAdapter(placeList)
        binding.recyclerView.adapter=adapter
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater=menuInflater
        menuInflater.inflate(R.menu.place_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId== R.id.addPlace){
            val intent=Intent(this, MapsActivity::class.java)
            intent.putExtra("info","new")
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}