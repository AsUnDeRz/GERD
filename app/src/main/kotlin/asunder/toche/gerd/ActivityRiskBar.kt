package asunder.toche.gerd

import adapter.RiskBarAdapter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_riskbar.*

/**
 *Created by ToCHe on 3/1/2018 AD.
 */

class ActivityRiskBar : AppCompatActivity(){

    lateinit var data : MutableList<Model.Rain>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riskbar)

        val dataR = intent.getParcelableArrayListExtra<Model.valRain>("dr")
        data = ArrayList()
        RainFragment2.adapter.data.forEach {
            data.add(it)
        }

        data.sortByDescending { it.date }

        Utils.CalRainFall(dataR,data)


        rvRiskBar.setHasFixedSize(true)
        rvRiskBar.layoutManager = LinearLayoutManager(this)
        rvRiskBar.adapter = RiskBarAdapter(data)


    }


}



