package com.ogulcankirtay.googlemaps.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ogulcankirtay.googlemaps.model.Place

@Database(entities = [Place::class], version = 1)
abstract class PlaceDb : RoomDatabase() {
    abstract fun PlaceDao(): PlaceDao
}