package asunder.toche.gerd

import adapter.RainAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.github.ybq.android.spinkit.style.FadingCircle
import kotlinx.android.synthetic.main.fragment_rain2.*
import utils.Prefer
import java.util.*
import kotlin.properties.Delegates


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

        @SuppressLint("StaticFieldLeak")
        lateinit var btnSave :Button

        var isShowSave: Boolean by Delegates.observable(false, { property, oldValue, newValue ->
            if (newValue) {
                btnSave.visibility = View.VISIBLE
            } else {
                btnSave.visibility = View.GONE
            }

        })



    }
    lateinit var appDb:AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appDb = AppDatabase(context!!)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_rain2, container, false)
        rvDate = view!!.findViewById(R.id.rv_date)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        proLoad = view.findViewById(R.id.root_load2)
        load = view.findViewById(R.id.pro_load2)
        btnSave = view.findViewById(R.id.btn_save)
        val fad = FadingCircle()
        load.indeterminateDrawable  = fad
        rvDate.layoutManager = LinearLayoutManager(activity)
        rvDate.hasFixedSize()

        btnSave.setOnClickListener {
            adapter.DialogSave(context!!)
        }
        setSpinner()
    }

    fun initAdapter(limit :String){
        adapter = RainAdapter(Utils.synchronizeData(appDb.getRainPrevious(limit,Utils.getDateWithFormat(Date())),
                Utils.initRain(limit.toInt())), limit,Utils.getDateWithFormat(Date()))
        rvDate.adapter = adapter

    }


    fun setSpinner(){
        val data = (7 until 31).map { it.toString() }
        val adapter = ArrayAdapter<String>(activity,R.layout.custom_text_day,data)
        spinnerDay.adapter = adapter
        spinnerDay.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Prefer.setDateLimit(context!!,data[position])
                initAdapter(data[position])
            }
        }

    }




}