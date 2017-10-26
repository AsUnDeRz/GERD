package asunder.toche.gerd

import adapter.RainAdapter
import android.content.Context
import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import asunder.toche.gerd.ActivityRain.Companion.viewPager
import asunder.toche.gerd.Data.EastRain
import asunder.toche.gerd.Data.NorthEastRain
import asunder.toche.gerd.Data.NorthRain
import asunder.toche.gerd.Data.SouthRain
import asunder.toche.gerd.Data.WestRain
import asunder.toche.gerd.Data.locations
import asunder.toche.gerd.Data.totalRain
import asunder.toche.gerd.Utils.initRain
import com.afollestad.materialdialogs.MaterialDialog
import com.github.ajalt.timberkt.Timber.d
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.EntryXComparator
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.activity_rain.*
import kotlinx.android.synthetic.main.fragment_rain1.*
import kotlinx.android.synthetic.main.fragment_rain2.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by ToCHe on 10/23/2017 AD.
 */
class RainFragment1:Fragment(){

    companion object {
        fun newInstance():RainFragment1 {
            return RainFragment1()
        }
    }

    private var localID: Int =0
    lateinit var mChart : LineChart
    var dataR : MutableList<Model.valRain> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_rain1, container, false)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        edt_date_back.setText("30")
        spinner.setText("3")
        edt_location.setText(locations[localID])
        edt_date.setText(Utils.getDateSlash(Date()))
        viewRoot.inflate()
        initGraph(view!!)


        PushDown.setOnTouchPushDown(btn_update_grap)
        PushDown.setOnTouchPushDown(btn_update_list)
        btn_update_list.setOnClickListener {
            RainFragment2.adapter = RainAdapter(initRain(edt_date_back.text.toString().toInt()))
            RainFragment2.rvDate.adapter = RainFragment2.adapter
            viewPager.setCurrentItem(1,true)

        }

        btn_update_grap.setOnClickListener {
            val data = RainFragment2.adapter.data
            /*
            val data =initRain(10)
            for(d in data){
                val r = (Math.random() * (50f - 10f)) + 10f
                d.currentRain = r.toInt().toFloat()
            }
            */
            Utils.CalRainFall(dataR,data)


            data.forEach {
                d{it.toString()}
            }
            setData(localID,data)


        }

        spinner.setOnClickListener { view ->
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            showRain()
        }
        spinner.isFocusableInTouchMode = false
        spinner.isFocusable =false
        edt_date.setOnClickListener { view ->
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            showSpinner()
        }
        edt_date.isFocusableInTouchMode = false
        edt_date.isFocusable =false
        edt_location.setOnClickListener { view ->
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            showLocation()
        }
        edt_location.isFocusableInTouchMode = false
        edt_location.isFocusable = false

    }

    fun showRain(){
        MaterialDialog.Builder(activity)
                .title("ปริมาณน้ำฝนสะสมราย/วัน")
                .items(totalRain)
                .itemsCallbackSingleChoice(-1) { dialog, itemView, which, text ->
                    spinner.setText(text)
                    true
                }
                /*
                .itemsCallback({ dialog, view, which, text ->
                    spinner.setText(text)
                })
                */
                .positiveText("ยืนยัน")
                .show()
    }


    fun showLocation(){
        MaterialDialog.Builder(activity)
                .title("ภูมิภาค")
                .items(locations)
                .itemsCallbackSingleChoice(-1) { dialog, itemView, which, text ->
                    localID = which
                    edt_location.setText(locations[which])
                    setData(localID,ArrayList())

                    true
                }
                /*
                .itemsCallback({ dialog, view, which, text ->
                    spinner.setText(text)
                })
                */
                .positiveText("ยืนยัน")
                .show()
    }

    fun showSpinner(){
        val c = Calendar.getInstance()
        val mount = c.get(Calendar.MONTH)
        val dOfm = c.get(Calendar.DAY_OF_MONTH)
        var year = c.get(Calendar.YEAR)
        SpinnerDatePickerDialogBuilder()
                .context(activity)
                .callback { view, year, monthOfYear, dayOfMonth ->
                    edt_date.setText("$dayOfMonth/"+(monthOfYear+1)+"/$year")
                }
                .spinnerTheme(R.style.DatePickerSpinner)
                .year(year+543)
                .monthOfYear(mount)
                .dayOfMonth(dOfm)
                .build()
                .show()
    }





    ///cal grap
    fun initGraph(view :View){

        mChart = view!!.findViewById(R.id.mChart)

        //mChart.setOnChartGestureListener(this)
        //mChart.setOnChartValueSelectedListener(this)
        mChart.setDrawGridBackground(false)

        // no description text
        mChart.description.isEnabled = false

        // enable touch gestures
        mChart.setTouchEnabled(true)


        // enable scaling and dragging
        mChart.isDragEnabled = true
        mChart.setScaleEnabled(true)
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true)

        // set an alternative background color
        // mChart.setBackgroundColor(Color.GRAY);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        //val mv = MyMarkerView(this, R.layout.custom_marker_view)
        //mv.setChartView(mChart) // For bounds control
        //mChart.setMarker(mv) // Set the marker to the chart

        // x-axis limit line
        val llXAxis = LimitLine(10f, "Index 10")
        llXAxis.lineWidth = 4f
        //llXAxis.enableDashedLine(10f, 10f, 0f)
        llXAxis.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
        llXAxis.textSize = 10f

        val xAxis = mChart.xAxis
        //xAxis.axisMaximum = 4f
        //xAxis.setLabelCount(5,true)
        xAxis.textColor = Color.BLACK
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawAxisLine(true)
        xAxis.axisMaximum =300f
        xAxis.axisMinimum =0f
        xAxis.setLabelCount(13,true)
        /*
        xAxis.valueFormatter = object  : IAxisValueFormatter {
            val mFormat = SimpleDateFormat("dd MMM")
            override fun getFormattedValue(value: Float, axis: AxisBase?): String {
                //val millis = TimeUnit.HOURS.toMillis(value.toLong())
                return mFormat.format(Date(value.toLong()))
            }
        }
        */


        xAxis.enableGridDashedLine(10f, 10f, 0f)
        //xAxis.setValueFormatter(new MyCustomXAxisValueFormatter());
        //xAxis.addLimitLine(llXAxis) // add_blue x-axis limit line


        val leftAxis = mChart.axisLeft
        leftAxis.removeAllLimitLines() // reset all limit lines to avoid overlapping lines
        leftAxis.textColor = Color.BLACK
        leftAxis.axisMaximum = 300f
        leftAxis.axisMinimum =0f
        leftAxis.setLabelCount(13,true)
        //leftAxis.yOffset = 25f
        leftAxis.enableGridDashedLine(10f, 10f, 0f)
        leftAxis.setDrawZeroLine(true)
        //leftAxis.setDrawGridLines(false)

        // limit lines are drawn behind data (and not on top)
        //leftAxis.setDrawLimitLinesBehindData(true)

        mChart.axisRight.isEnabled = false
        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);

        // add_blue data
        setData(localID,ArrayList())

//        mChart.setVisibleXRange(20);
//        mChart.setVisibleYRange(20f, AxisDependency.LEFT);
//        mChart.centerViewTo(20, 50, AxisDependency.LEFT);

        mChart.animateX(2500)
        //mChart.invalidate()

        // get the legend (only possible after setting_layout data)
        val l = mChart.legend

        // modify the legend ...
        l.form = Legend.LegendForm.LINE
        l.isEnabled = false
        //l.textColor = Color.BLACK
        //l.typeface = utils.medium

        // // dont forget to refresh the drawing
        mChart.invalidate()

    }


    private fun setData(localID:Int,rawList:MutableList<Model.Rain>) {
        dataR  = ArrayList()
        when(localID){
            0 -> dataR = NorthRain
            1 -> dataR = SouthRain
            2 -> dataR = EastRain
            3 -> dataR = WestRain
            4 -> dataR = NorthEastRain

        }

        val red = ArrayList<Entry>()
        val yellow = ArrayList<Entry>()
        val rawData = ArrayList<Entry>()
        val lastPosition = ArrayList<Entry>()
        val dataSets = ArrayList<ILineDataSet>()
        val setRed: LineDataSet
        val setYellow : LineDataSet
        val setRawData :LineDataSet
        val lastData :LineDataSet


        red.add(Entry(dataR[0].x,dataR[0].y))
        red.add(Entry(dataR[1].x,dataR[1].y))
        yellow.add(Entry(dataR[2].x,dataR[2].y))
        yellow.add(Entry(dataR[3].x,dataR[3].y))

        if(rawList.size > 0) {
            for (d in rawList){
                rawData.add(Entry(d.previousRain,d.currentRain))
            }
            lastPosition.add(Entry(rawList.last().previousRain,rawList.last().currentRain))
        }
        else{
            rawData.add(Entry(0.0f,0.0f))
            lastPosition.add(Entry(0.0f,0.0f))

        }

        Collections.sort(rawData, EntryXComparator())





        if (mChart.data != null && mChart.data.dataSetCount > 0) {
            setRed = mChart.data.getDataSetByIndex(0) as LineDataSet
            setRed.values = red
            setYellow = mChart.data.getDataSetByIndex(1) as LineDataSet
            setYellow.values = yellow
            setRawData = mChart.data.getDataSetByIndex(2) as LineDataSet
            setRawData.values = rawData
            lastData = mChart.data.getDataSetByIndex(3) as LineDataSet
            lastData.values = lastPosition
            mChart.data.notifyDataChanged()
            mChart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            setRed = LineDataSet(red,"ระดับสูง")
            setRed.values = red

            setRed.setDrawIcons(false)
            // set the line to be drawn like this "- - - - - -"
            //setRed.enableDashedLine(10f, 5f, 0f)
            //setRed.enableDashedHighlightLine(10f, 5f, 0f)
            setRed.color = ContextCompat.getColor(activity,R.color.red)
            setRed.valueTextColor = Color.BLACK
            setRed.circleRadius = 3f
            setRed.setCircleColor(ContextCompat.getColor(activity,R.color.red))
            setRed.lineWidth = 3f
            //setRed.circleRadius = 3f
            setRed.setDrawCircleHole(false)
            setRed.setDrawValues(true)
            setRed.valueTextSize = 9f
            setRed.setDrawFilled(false)
            setRed.formLineWidth = 1f
            setRed.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            setRed.formSize = 15f

            //init yellow
            setYellow = LineDataSet(yellow,"ระดับกลาง")
            setYellow.values = yellow

            setYellow.setDrawIcons(false)
            // set the line to be drawn like this "- - - - - -"
            // setYellow.enableDashedLine(10f, 5f, 0f)
            // setYellow.enableDashedHighlightLine(10f, 5f, 0f)
            setYellow.color = ContextCompat.getColor(activity,R.color.yellow)
            setYellow.valueTextColor = Color.BLACK
            setYellow.circleRadius = 3f
            setYellow.setCircleColor(ContextCompat.getColor(activity,R.color.yellow))
            setYellow.lineWidth = 3f
            // setYellow.circleRadius = 3f
            setYellow.setDrawCircleHole(false)
            setYellow.setDrawValues(false)
            setYellow.valueTextSize = 9f
            setYellow.setDrawFilled(false)
            setYellow.formLineWidth = 1f
            setYellow.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
            setYellow.formSize = 15f

            /*
            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
                val drawable = ContextCompat.getDrawable(this, R.drawable.fade_red)
                setRed.fillDrawable = drawable
            } else {
                setRed.fillColor = Color.BLACK
            }
            */
                //init rawList

                setRawData = LineDataSet(rawData,"")
                setRawData.values = rawData

                setRawData.setDrawIcons(false)
                // set the line to be drawn like this "- - - - - -"
                setRawData.enableDashedLine(10f, 5f, 0f)
                setRawData.enableDashedHighlightLine(10f, 5f, 0f)
                setRawData.color = Color.BLACK
                setRawData.valueTextColor = Color.BLACK
                setRawData.circleRadius = 5f
                setRawData.setCircleColor(Color.YELLOW)
                setRawData.lineWidth = 1f
                // setYellow.circleRadius = 3f
                setRawData.setDrawCircleHole(false)
                setRawData.setDrawValues(true)
                setRawData.valueTextSize = 9f
                setRawData.setDrawFilled(false)
                setRawData.formLineWidth = 1f
                setRawData.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
                setRawData.formSize = 15f


                //

                lastData = LineDataSet(lastPosition,"ตำแหน่งล่าสุด")
                lastData.values = lastPosition

                lastData.setDrawIcons(false)
                // set the line to be drawn like this "- - - - - -"
                lastData.enableDashedLine(10f, 5f, 0f)
                lastData.enableDashedHighlightLine(10f, 5f, 0f)
                lastData.color = Color.BLACK
                lastData.valueTextColor = Color.BLACK
                lastData.circleRadius = 5f
                lastData.setCircleColor(ContextCompat.getColor(activity,R.color.green))
                lastData.lineWidth = 1f
                //setRed.circleRadius = 3f
                lastData.setDrawCircleHole(false)
                lastData.setDrawValues(true)
                lastData.valueTextSize = 9f
                lastData.setDrawFilled(false)
                lastData.formLineWidth = 1f
                lastData.formLineDashEffect = DashPathEffect(floatArrayOf(10f, 5f), 0f)
                lastData.formSize = 15f


                //set dataset

            dataSets.add(setRed) // add_blue the datasets
            dataSets.add(setYellow)
            dataSets.add(setRawData)
            dataSets.add(lastData)


            // create a data object with the datasets
            val data = LineData(dataSets)
            //data.setValueTextSize(9f)
            //data.setDrawValues(true)
            // set data
            mChart.data = data
        }
    }


}