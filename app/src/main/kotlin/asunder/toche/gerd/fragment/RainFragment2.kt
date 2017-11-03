package asunder.toche.gerd

import adapter.RainAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import asunder.toche.gerd.Utils.initRain
import com.github.ajalt.timberkt.Timber.d
import com.github.ybq.android.spinkit.style.FadingCircle
import kotlinx.android.synthetic.main.activity_rain.*
import kotlinx.android.synthetic.main.fragment_rain2.*
import java.util.*


/**
 * Created by ToCHe on 10/23/2017 AD.
 */
class RainFragment2:Fragment(){


    companion object {
        fun newInstance():RainFragment2 {
            return RainFragment2()
        }

        @SuppressLint("StaticFieldLeak")
        lateinit var rvDate :RecyclerView
        lateinit var adapter:RainAdapter

        @SuppressLint("StaticFieldLeak")
        lateinit var proLoad:RelativeLayout
        @SuppressLint("StaticFieldLeak")
        lateinit var load:ProgressBar

    }
    lateinit var appDb:AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appDb = AppDatabase(context)
        adapter = RainAdapter(Utils.synchronizeData(appDb,"30"), "30")


    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_rain2, container, false)
        rvDate = view!!.findViewById(R.id.rv_date)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        proLoad = view!!.findViewById(R.id.root_load2)
        load = view.findViewById(R.id.pro_load2)
        val fad = FadingCircle()
        load.indeterminateDrawable =fad
        rvDate.layoutManager = LinearLayoutManager(activity)
        rvDate.hasFixedSize()
        rvDate.adapter = adapter

        btn_save.setOnClickListener {
            adapter.DialogSave(context)
        }
    }




}