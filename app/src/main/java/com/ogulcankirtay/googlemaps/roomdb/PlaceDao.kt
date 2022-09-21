package com.ogulcankirtay.googlemaps.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ogulcankirtay.googlemaps.model.Place
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface PlaceDao {

    @Query("Select * From Place")
    fun getAll(): Flowable<List<Place>>

    @Insert
    fun insert(place: Place): Completable
    @Delete
    fun Delete(place: Place) :Completable
}