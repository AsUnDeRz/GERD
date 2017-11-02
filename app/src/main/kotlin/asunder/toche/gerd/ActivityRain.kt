package asunder.toche.gerd

import adapter.RainViewAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.github.ajalt.timberkt.d
import kotlinx.android.synthetic.main.activity_rain.*
import java.util.*


/**
 * Created by ToCHe on 10/22/2017 AD.
 */

class ActivityRain : AppCompatActivity(){


    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var viewPager:ViewPager
    }

    lateinit var appDb :AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rain)

        setSupportActionBar(toolbar)

        viewPager = findViewById(R.id.viewpager)
        viewPager.adapter = RainViewAdapter(supportFragmentManager,this@ActivityRain)
        tabView.setupWithViewPager(viewpager)



        title = "คำนวณปริมาณน้ำฝนสะสม"


        //test

        /*
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.add(Calendar.DATE,1)
        val dataNew = Utils.initRain(8)
        dataNew.add(Model.Rain(0,calendar.time,0.0f,0.0f,Model.StatusRain.LOW))
        calendar.add(Calendar.DATE,1)
        dataNew.add(Model.Rain(0,calendar.time,0.0f,0.0f,Model.StatusRain.LOW))
        dataNew.forEach {
            it.currentRain = ((Math.random() * (50f - 10f)) + 10f).toFloat()
            d{"Addd $it"}
            //appDb.addRain(it.currentRain,it.date.time)
        }
        Utils.compareData(appDb,dataNew)
        */




    }


    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent().setClass(this@ActivityRain,ActivityHome::class.java))
        finish()
    }



}
