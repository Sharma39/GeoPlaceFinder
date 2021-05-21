package com.example.geoloacationfinder

import android.util.Log
import com.example.geoloacationfinder.model.PlacesResponse
import com.example.geoloacationfinder.util.Constants.Companion.API_KEY
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class GooglePlaceRetrofit {

    //Search for google places API
    //https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=33.194000,-85.396348
// &radius=1500&key=AIzaSyA5rJh9FeteViW7n9M3iORS7d8L7W75wVI
    private val placeEndPoint = createRetrofit().create(PlaceEndPoint::class.java)

    private fun createRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl("https://maps.googleapis.com/")
        .addConverterFactory(GsonConverterFactory.create())
        // this line below only needed when using coroutine adapter
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    //Without coroutine only fun makeApiCall with no suspend and no Asynch at the end of name and no deferred
    suspend fun makeApiCallAsync(type: String, keyWord: String, location: String, radius: Int): Deferred<PlacesResponse> {
        Log.d("TAG_X", "$type, $location")
        return placeEndPoint.getPlaces(
            type,
            API_KEY,
            keyWord,
            location,
            radius
        )
    }

    interface PlaceEndPoint {
        @GET("/maps/api/place/nearbysearch/json")
        fun getPlaces(
            @Query("type") placeType: String,
            @Query("key") apiKey: String,
            @Query("keyword") keyWord: String,
            @Query("location") location: String,
            @Query("radius") radius: Int
        ): Deferred<PlacesResponse>
    }


    }