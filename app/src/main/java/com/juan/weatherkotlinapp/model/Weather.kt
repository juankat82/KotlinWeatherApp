package com.juan.weatherkotlinapp.model

data class Weather (var mLocation: String="", var mLatitude: Float=0.0f, var mLongitude: Float=0.0f, var mTimeStamp: Long=0, var mWeatherCondition: String="",
                    var temperature: Float=0.0f, var windSpeed: Float=0.0f, var windDirection: String="", var iconName:String="" )