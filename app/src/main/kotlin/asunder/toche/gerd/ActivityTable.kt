package asunder.toche.gerd

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import asunder.toche.gerd.Utils.initRain
import kotlinx.android.synthetic.main.table_view.*

/**
 * Created by ToCHe on 10/27/2017 AD.
 */
class ActivityTable:AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.table_view)

        val data2 = arrayListOf(
                arrayOf("Header","Cloum"),arrayOf("num","1"),arrayOf("num","2"),
                arrayOf("num","3"), arrayOf("num","4"), arrayOf("num","5"),
                arrayOf("num","6"), arrayOf("num","7"), arrayOf("num","8"),
                arrayOf("num","9"), arrayOf("num","10"), arrayOf("num","11"))

        val data =initRain(20)
        for(d in data){
            val r = (Math.random() * (50f - 10f)) + 10f
            when {
                r >= 40 -> {
                    d.status = Model.StatusRain.HIGH
                }
                r >= 25 -> {
                    d.status = Model.StatusRain.NORMAL
                }
                else -> {
                    d.status = Model.StatusRain.LOW
                }
            }
            d.currentRain = r.toInt().toFloat()
            d.previousRain = (r/2).toInt().toFloat()

        }


        val mTableAdapter = SampleLinkedTableAdapter(this@ActivityTable, RainDataSource(data))
        tableLayout.setAdapter(mTableAdapter)

    }
}