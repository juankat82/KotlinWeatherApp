package com.juan.weatherkotlinapp.controller

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.juan.weatherkotlinapp.R
import java.util.*


class SingletonClass (context: Context){

    companion object SingletonClass{

        fun getSingleton(context: Context) = SingletonClass(context)

    }

    //private fun  getSingletonClass(context:Context) = this@SingletonClass

    fun isGooglePlayAvailable(context:Context) : Boolean
    {
        val availability=GoogleApiAvailability.getInstance()
        val result=availability.isGooglePlayServicesAvailable(context)
        if (result!= ConnectionResult.SUCCESS)
        {
            if(!availability.isUserResolvableError(result))
                return false
            return false
        }
        else {
            return true
        }
    }

    fun getDifferenceOfDays(oldTimeStamp: Long) = ((Date().getTime()/(24*60*60*1000))-(oldTimeStamp/(24*60*60*1000)))

    fun  getImage(icon: String) : Int
    {
        when (icon)
        {
            "i01d" -> return R.drawable.i01d
            "i01n" -> return R.drawable.i01n
            "i02d" -> return R.drawable.i02d
            "i02n" -> return R.drawable.i02n
            "i03d" -> return R.drawable.i03d
            "i03n" -> return R.drawable.i03n
            "i04d" -> return R.drawable.i04d
            "i04n" -> return R.drawable.i04n
            "i09d" -> return R.drawable.i09d
            "i09n" -> return R.drawable.i09n
            "i10d" -> return R.drawable.i10d
            "i10n" -> return R.drawable.i10n
            "i11d" -> return R.drawable.i11d
            "i11n" -> return R.drawable.i11n
            "i13d" -> return R.drawable.i13d
            "i13n" -> return R.drawable.i13n
            "i50d" -> return R.drawable.i50d
            "i50n" -> return R.drawable.i50n
            else ->  return R.drawable.not_vailable;
        }
    }

    fun kelvinToCelsius(kelvin:Double) = kelvin-273.15

    fun getWindDirection(degrees:Int) =  when(degrees) {
            in 338..360 ->"North"
            in 0..22 -> "North"
            in 23..67 ->"North East"
            in 68..112 ->"East"
            in 113..157 ->"South East"
            in 158..202 ->"South"
            in 203..247 ->"South West"
            in 248..292 ->"West"
            in 293..337 ->"North West"
            else -> "Invalid wind direction"
        }
}