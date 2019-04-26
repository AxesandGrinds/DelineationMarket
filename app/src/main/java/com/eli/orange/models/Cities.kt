package com.eli.orange.models

import com.google.gson.annotations.SerializedName

class Cities(
        @field:SerializedName("country") var country: String?,
        @field:SerializedName("name") var countryName: String?,
        @field:SerializedName("lat") var latitude: Double,
        @field:SerializedName("lng") var longitude: Double)

