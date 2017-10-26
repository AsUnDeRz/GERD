package asunder.toche.gerd

import adapter.RainViewAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.github.ajalt.timberkt.d
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.activity_rain.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by ToCHe on 10/22/2017 AD.
 */

class ActivityRain : AppCompatActivity(){


    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var viewPager:ViewPager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rain)

        setSupportActionBar(toolbar)

        viewPager = findViewById(R.id.viewpager)
        viewPager.adapter = RainViewAdapter(supportFragmentManager,this@ActivityRain)
        tabView.setupWithViewPager(viewpager)



        title = "คำนวณปริมาณน้ำฝนสะสม"


    }









}
