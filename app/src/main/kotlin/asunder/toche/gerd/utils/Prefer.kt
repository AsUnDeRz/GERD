package utils

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import java.util.*

/**
 *Created by ToCHe on 14/12/2017 AD.
 */
object Prefer{


    fun getPrefer(context: Context) : SharedPreferences{
        return PreferenceManager.getDefaultSharedPreferences(context)
    }
    fun setVersionCode(context: Context,version:String) {
        val editor = getPrefer(context).edit()
        editor.putString(KEYPREFER.version_code,version)
        editor.apply()
    }

    fun setLastUpdate(context: Context,lastUpdate:Long){
        val editor = getPrefer(context).edit()
        editor.putLong(KEYPREFER.last_update,lastUpdate)
        editor.apply()
    }

    fun getLastUpdate(context: Context):Long{
        return getPrefer(context).getLong(KEYPREFER.last_update,Date().time)
    }

    fun getVersionCode(context: Context) :  String{
        return getPrefer(context).getString(KEYPREFER.version_code,"")
    }

    fun setDateLimit(context: Context, datelimit: String) {
        val editor = getPrefer(context).edit()
        editor.putInt(KEYPREFER.date_limit,datelimit.toInt())
        editor.apply()
    }
    fun getDateLimit(context: Context) :Int {
        return getPrefer(context).getInt(KEYPREFER.date_limit,7)
    }

    fun setForcast(context: Context,data:String){
        val editor = getPrefer(context).edit()
        editor.putString(KEYPREFER.FORCAST,data)
        editor.apply()
    }

    fun getForcast(context: Context):String{
        return getPrefer(context).getString(KEYPREFER.FORCAST,"")
    }


}