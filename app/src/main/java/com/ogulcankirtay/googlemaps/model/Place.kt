package com.ogulcankirtay.googlemaps.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Place(
    @ColumnInfo(name="name")
    var name: String,
    @ColumnInfo(name="lat")
    var lat : Double,
    @ColumnInfo(name="lng")
    var lng: Double) : Serializable
{
    @PrimaryKey(autoGenerate = true)
    var id=0
}