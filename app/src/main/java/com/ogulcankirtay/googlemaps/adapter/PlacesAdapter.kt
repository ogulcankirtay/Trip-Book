package com.ogulcankirtay.googlemaps.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ogulcankirtay.googlemaps.databinding.RowBinding
import com.ogulcankirtay.googlemaps.model.Place
import com.ogulcankirtay.googlemaps.view.MapsActivity

class PlacesAdapter(val placeList: List<Place>): RecyclerView.Adapter<PlacesAdapter.PlaceHolder>() {

    class PlaceHolder(val recyclerRowBinding: RowBinding): RecyclerView.ViewHolder(recyclerRowBinding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceHolder {

        val recyclerRowBinding=RowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PlaceHolder(recyclerRowBinding)
    }

    override fun onBindViewHolder(holder: PlaceHolder, position: Int) {

        holder.recyclerRowBinding.recyclerRow.text=placeList.get(position).name.toString()
        holder.itemView.setOnClickListener {
            val intent=Intent(holder.itemView.context,MapsActivity::class.java)
            intent.putExtra("Place",placeList.get(position))
            intent.putExtra("info","old")
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return placeList.size
    }
}