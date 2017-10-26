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
import asunder.toche.gerd.Utils.initRain
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

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = RainAdapter(initRain(30))

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_rain2, container, false)
        rvDate = view!!.findViewById(R.id.rv_date)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvDate.layoutManager = LinearLayoutManager(activity)
        rvDate.hasFixedSize()
        rvDate.adapter = adapter
    }




}