package com.juan.weatherkotlinapp

import androidx.appcompat.app.AlertDialog
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteDatabase
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.juan.weatherkotlinapp.controller.DataFlowController
import com.juan.weatherkotlinapp.controller.MyWeatherAsyncTask
import com.juan.weatherkotlinapp.controller.SingletonClass
import com.juan.weatherkotlinapp.database.WeatherOpenHelper
import com.juan.weatherkotlinapp.model.Weather


private const val OPEN_WEATHERMAP_API_KEY = "WRITE YOUR OWN KEY HERE"
private const val GPS_REQUEST_CODE = 33
private const val PERMISSIONS_REQUEST_CODE = 55


class MainActivity : AppCompatActivity() {

    private lateinit var mFloatingActionButton: FloatingActionButton
    private lateinit var mConditionTextView: TextView
    private lateinit var mTemperatureTextView: TextView
    private lateinit var mWindSpeedTextView: TextView
    private lateinit var mWindDirectionTextView: TextView
    private lateinit var mConditionImageView: ImageView
    private lateinit var mProgressCircle: ProgressBar
    private lateinit var mLoadingTextView: TextView
    private lateinit var mMessageTextView: TextView
    private lateinit var mWeatherDatabase: SQLiteDatabase
    private lateinit var mWeather: Weather
    private lateinit var mSharedPreferences: SharedPreferences
    private lateinit var mLocationManager: LocationManager
    private lateinit var gpsIntent: Intent
    private var isGpsEnabled = false
    private var choseOffMode = false
    private val permissions = arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var locationCallback: LocationCallback? = null
    private var locationRequest: LocationRequest? = null
    private lateinit var mSingletonClass: SingletonClass
    private lateinit var mDataFlowController: DataFlowController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        mWeatherDatabase = WeatherOpenHelper(applicationContext).writableDatabase
        mSharedPreferences = applicationContext.getSharedPreferences("com.juan.weatherapp", Context.MODE_PRIVATE)
        mFloatingActionButton = findViewById(R.id.floatingActionButton)
        mConditionTextView = findViewById(R.id.condition_textview)
        mTemperatureTextView = findViewById(R.id.temperature_textview)
        mWindSpeedTextView = findViewById(R.id.wind_speed_textview)
        mWindDirectionTextView = findViewById(R.id.wind_direction_textview)
        mConditionImageView = findViewById(R.id.condition_image_view)
        mProgressCircle = findViewById(R.id.progress_circle)
        mLoadingTextView = findViewById(R.id.loading_text_view)
        mMessageTextView = findViewById(R.id.message_text_view)
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mSingletonClass = SingletonClass.getSingleton(this)
        mDataFlowController = DataFlowController(mWeatherDatabase, mSharedPreferences)

        if (!mSingletonClass.isGooglePlayAvailable(this)) {
            mMessageTextView.visibility = View.VISIBLE//turn visibility OFF when hitting the RELOAD button
            mMessageTextView.setText(R.string.play_services_unavailable)
        }

        mFloatingActionButton.setOnClickListener {

                isGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                mMessageTextView.visibility = View.GONE

                if (!isGpsEnabled) {
                    if (choseOffMode) {
                        mMessageTextView.text="${R.string.you_need_gps}"
                        mMessageTextView.visibility = View.VISIBLE
                        setOffLineSystem(mDataFlowController.getWeatherFromSharedPreferences())
                    } else
                    //we turned off gps while app was on
                    {
                        mMessageTextView.visibility = View.GONE
                        enableGps()
                        getLocation()
                    }
                } else {
                    mMessageTextView.visibility = View.GONE
                    getLocation()
                }
            }
        }



    @Override
    override fun onStart()
    {
        super.onStart()
        val permission= ActivityCompat.checkSelfPermission(applicationContext, ACCESS_FINE_LOCATION)
        isGpsEnabled=mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (permission== PackageManager.PERMISSION_GRANTED)
        {
            if (!isGpsEnabled)
            {
                Log.i("GPSWORKS","no")
                enableGps()
            }
            else
            {
                Log.i("GPSWORKS","yes")
                choseOffMode=false
                getLocation()
            }
        }
        else
        {
            ActivityCompat.requestPermissions(this,permissions,PERMISSIONS_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode:Int, @NonNull permissions:Array<String>,@NonNull grantResults:IntArray)
    {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults)
        if (requestCode==PERMISSIONS_REQUEST_CODE)
        {
            if (grantResults[0]!=PackageManager.PERMISSION_GRANTED)
            {
                mMessageTextView.visibility=View.VISIBLE
                mMessageTextView.setText(R.string.permissions_not_granted)
            }
            else
            {
                if (mMessageTextView.visibility==View.VISIBLE)
                    mMessageTextView.visibility=View.GONE

                if (isGpsEnabled)
                    getLocation()
                else
                    enableGps()
            }
        }
    }


   override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?)
    {
        if (requestCode==GPS_REQUEST_CODE)
        {
            if (resultCode== Activity.RESULT_OK)
            {
                isGpsEnabled=true
            }
        }
    }

    private fun setOffLineSystem(weather:Weather?)
    {
        val differenceOfDays:Long

        if (weather==null)
        {
            mConditionTextView.setText(R.string.no_data_text)
            mTemperatureTextView.setText(R.string.no_data_text)
            mWindSpeedTextView.setText(R.string.no_data_text)
            mWindDirectionTextView.setText(R.string.no_data_text)
            mConditionImageView.setImageResource(R.drawable.not_vailable)
        }
        else
        {
            differenceOfDays=mSingletonClass.getDifferenceOfDays(weather.mTimeStamp)
            if (differenceOfDays>=1.0)
            {
                mConditionTextView.setText(R.string.no_data_text)
                mTemperatureTextView.setText(R.string.no_data_text)
                mWindSpeedTextView.setText(R.string.no_data_text)
                mWindDirectionTextView.setText(R.string.no_data_text)
                mConditionImageView.setImageResource(R.drawable.not_vailable)
            }
            else
            {
                mMessageTextView.visibility=View.VISIBLE
                mMessageTextView.text =getText(R.string.you_need_gps)
                mConditionTextView.text=weather.mWeatherCondition
                mTemperatureTextView.text="${weather.temperature} °C"
                mWindSpeedTextView.text="${Math.round(weather.windSpeed)} Mph"
                mWindDirectionTextView.text=weather.windDirection
                mConditionImageView.setImageResource(mSingletonClass.getImage(weather.iconName))
            }
        }
    }

    private fun enableGps()
    {
        if(!isGpsEnabled)
        {
            Log.i("GPSWORKS","nope its not enabled")
            this.let {
                AlertDialog.Builder(it).setTitle(R.string.start_gps_question_title).setMessage(R.string.get_gps_enabled_string)
                    .setPositiveButton("OK")  { _, _ ->
                        gpsIntent=Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivityForResult(gpsIntent, GPS_REQUEST_CODE)
                        isGpsEnabled=true
                        choseOffMode=false
                    }
                    .setNegativeButton("Cancel") { _, _ ->
                        AlertDialog.Builder(it).setTitle(R.string.try_offline_mode_string).setMessage(R.string.are_you_sure_string)
                            .setPositiveButton("Yes") { _, _ ->
                                choseOffMode=true
                                mWeather=mDataFlowController.getWeatherFromSharedPreferences() ?: Weather()//check this out
                                setOffLineSystem(mWeather)
                                mMessageTextView.visibility=View.VISIBLE
                            }
                            .setNegativeButton("No") { _, _ ->
                                enableGps()
                                mMessageTextView.visibility=View.GONE
                                choseOffMode=false
                            }.create().show()
                    }.create().show()
            }
        }
    }

    private fun getLocation()
    {
        if (ActivityCompat.checkSelfPermission(applicationContext, ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(applicationContext,ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED)
        {
            mFusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)

            locationCallback=object: LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    super.onLocationResult(locationResult)
                    val location=locationResult!!.getLocations().get(locationResult.getLocations().size-1)//gets last location
                    val latitude=location.latitude
                    val longitude=location.longitude
                    val stringToSend="https://api.openweathermap.org/data/2.5/weather?lat=$latitude&lon=$longitude&cnt=10&APPID="+OPEN_WEATHERMAP_API_KEY

                        choseOffMode=false
                        mWeather= MyWeatherAsyncTask(applicationContext,this@MainActivity,mProgressCircle,mLoadingTextView).execute(stringToSend).get()
                        mConditionTextView.text=mWeather.mWeatherCondition
                        mTemperatureTextView.text="${Math.round(mWeather.temperature)} ℃"
                        mWindSpeedTextView.text="${Math.round(mWeather.windSpeed)} Mph"
                        mWindDirectionTextView.text=mWeather.windDirection
                        mConditionImageView.setImageResource(mSingletonClass.getImage(mWeather.iconName))
                        mDataFlowController.writeSharedPreferences(mWeather)
                }
            }
            locationRequest=LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setNumUpdates(1).setInterval(2000)
            mFusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null)
        }
        else
            requestPermissions(permissions, PERMISSIONS_REQUEST_CODE)
    }
}
