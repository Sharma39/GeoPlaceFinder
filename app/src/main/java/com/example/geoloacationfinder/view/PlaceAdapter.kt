package com.example.geoloacationfinder.view

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.geoloacationfinder.R
import com.example.geoloacationfinder.model.Result
import kotlinx.android.synthetic.main.place_item_layout.view.*

class PlaceAdapter(private var plList: List<Result>):
    RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>() {

    inner class PlaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun updatePlaces(placeList: List<Result>) {
        this.plList = placeList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.place_item_layout, parent, false)
        return PlaceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        plList[position].let {
            holder.itemView.apply {
              Log.d("TAG_NS", "${it.name}, ${it.vicinity}")
                name_view_layout.text = it.name
                area_view_layout.text = it.vicinity
                openhrs_layout.text = "OpenHrs:  ${it.opening_hours}"
                rating_view.text = it.rating.toString()
            }
        }
    }
    override fun getItemCount(): Int = plList.size

}