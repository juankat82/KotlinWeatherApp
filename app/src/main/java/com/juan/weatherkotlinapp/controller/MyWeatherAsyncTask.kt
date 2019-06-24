package com.juan.weatherkotlinapp.controller

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.juan.weatherkotlinapp.MainActivity
import com.juan.weatherkotlinapp.model.Weather
import java.util.*
import kotlin.concurrent.timerTask
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject


class MyWeatherAsyncTask(_context: Context, _activity: MainActivity, _progressCircle:ProgressBar, _textView:TextView ) : AsyncTask<String,Void, Weather>(){

    private var progressCircle:ProgressBar
    private var loadingTextView:TextView
    private var mActivity: MainActivity
    private var mContext:Context
    private val client=OkHttpClient()
    private var mSingletonClass:SingletonClass


    init {
        progressCircle=_progressCircle
        loadingTextView=_textView
        mActivity=_activity
        mContext=_context
        mSingletonClass=SingletonClass.getSingleton(mContext)
    }
    override fun onPreExecute() {
        progressCircle.visibility=View.VISIBLE
        loadingTextView.visibility=View.VISIBLE
    }
    override fun doInBackground(vararg params:String): Weather {

        return params[0].getJson().getWeatherFromJson()
    }

    private fun String.getWeatherFromJson() : Weather
    {
        val weather=Weather()
        val base = JSONObject(this)
        val coordObject = base.getJSONObject("coord")

        if (this.isNotBlank()) {
            weather.mLongitude = coordObject.getDouble("lon").toFloat()
            weather.mLatitude = coordObject.getDouble("lat").toFloat()

            val weatherArray = base.getJSONArray("weather")
            val weatherObject = weatherArray.getJSONObject(0)
            val condition = weatherObject.getString("description")
            val capitalizedCondition = condition.substring(0, 1).toUpperCase() + condition.substring(1)
            weather.mWeatherCondition = capitalizedCondition
            weather.iconName = "i${weatherObject.getString("icon")}"
            val mainObject = base.getJSONObject("main")
            weather.temperature = mSingletonClass.kelvinToCelsius(mainObject.getDouble("temp")).toFloat()
            val wind = base.getJSONObject("wind")
            weather.windSpeed = (wind.getDouble("speed") * 2.23694).toFloat()
            weather.windDirection = mSingletonClass.getWindDirection(wind.getDouble("deg").toInt())
            weather.mLocation = base.getString("name")
            weather.mTimeStamp = Date().time
        }
        else {
            mActivity.runOnUiThread {
                Runnable {
                    Toast.makeText(mContext, "No data received from server. Try again later", Toast.LENGTH_SHORT).show()
                }
            }
        }
        return weather
    }

    private fun String.getJson() : String {

        val request=Request.Builder().url(this).build()
        val jsonResponse=client.newCall(request).execute()
        return jsonResponse.body()?.string() ?: ""
    }

    override fun onPostExecute(result: Weather?) {
        val timer= Timer()
        val task=timerTask {
            mActivity.runOnUiThread{
                progressCircle.visibility=View.GONE
                loadingTextView.visibility=View.GONE
            }
        }

        timer.scheduleAtFixedRate(task,500,500)
    }
}