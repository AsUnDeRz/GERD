package asunder.toche.gerd

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import asunder.toche.gerd.Utils.initRain
import com.cleveroad.adaptivetablelayout.AdaptiveTableLayout
import com.github.ajalt.timberkt.Timber.d

/**
 * Created by ToCHe on 10/27/2017 AD.
 */
class ActivityTable:AppCompatActivity(){

    lateinit var data : MutableList<Model.Rain>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(!resources.getBoolean(R.bool.isTablet)){
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        }else{
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        }
        setContentView(R.layout.table_view)
        val tableView = findViewById<AdaptiveTableLayout>(R.id.tableLayout)

        val dataR = intent.getParcelableArrayListExtra<Model.valRain>("dr")
        data = ArrayList()
        RainFragment2.adapter.data.forEach {
            data.add(it)
        }

        data.sortByDescending { it.date }

        Utils.CalRainFall(dataR,data)
        //data.add(0,Model.Rain())
        val mTableAdapter = SampleLinkedTableAdapter(this@ActivityTable, RainDataSource(data))
        tableView.setAdapter(mTableAdapter)

    }
}