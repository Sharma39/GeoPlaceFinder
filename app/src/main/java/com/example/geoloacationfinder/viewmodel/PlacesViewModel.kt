package com.example.geoloacationfinder.viewmodel



import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geoloacationfinder.GooglePlaceRetrofit
import com.example.geoloacationfinder.model.Result
import com.example.geoloacationfinder.util.FunctionUtility.Companion.toFormattedString
import com.example.geoloacationfinder.util.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PlacesViewModel: ViewModel() {


    private var netJob: Job? = null

    private val retrofit = GooglePlaceRetrofit()

    val liveData: MutableLiveData<List<Result>> = MutableLiveData()

    val statusData = MutableLiveData<State>()

    fun getPlacesNearMe(location: Location, pname: String){
        statusData.value = State.LOADING

        netJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                Log.d("TAG_R","${pname}, ${location.toFormattedString()}")
                val result = retrofit.makeApiCallAsync(
                    pname,
                    "",
                    location.toFormattedString(),
                    10000
                ).await()

                liveData.postValue(result.results)
                statusData.postValue(State.SUCCESS)

            } catch (e: Exception) {
                //At this point an error occurred
                Log.d("TAG_X", e.toString())
                statusData.postValue(State.ERROR)
            }

        }

    }

    override fun onCleared() {
        netJob?.cancel()
        super.onCleared()
    }
}