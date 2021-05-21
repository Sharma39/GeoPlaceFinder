package com.example.geoloacationfinder.model

data class PlacesResponse(
    val html_attributions: List<Any>,
    val results: List<Result>,
    val status: String
)