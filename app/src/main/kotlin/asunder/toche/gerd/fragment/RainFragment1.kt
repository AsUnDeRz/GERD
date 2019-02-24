package asunder.toche.gerd

import adapter.RainAdapter
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
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
import com.afollestad.materialdialogs.MaterialDialog
import com.cleveroad.adaptivetablelayout.OnItemClickListener
import com.cleveroad.adaptivetablelayout.OnItemLongClickListener
import com.github.ajalt.timberkt.Timber.d
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet
import com.github.mikephil.charting.utils.EntryXComparator
import com.layernet.thaidatetimepicker.date.DatePickerDialog
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder
import kotlinx.android.synthetic.main.fragment_rain1.*
import utils.Prefer
import java.util.*


/**
 * Created by ToCHe on 10/23/2017 AD.
 */
class RainFragment1:Fragment(),OnItemClickListener,OnItemLongClickListener,
        com.layernet.thaidatetimepicker.date.DatePickerDialog.OnDateSetListener{
    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        edt_date.setText("$dayOfMonth/"+(monthOfYear+1)+"/${year+543}")
        var haveDay= false
        val max = Calendar.getInstance()
        max.set(year,monthOfYear,dayOfMonth,7,0,0)
        pickDate = max.time
        val dateLimit = Prefer.getDateLimit(context!!)
        //max.add(Calendar.DATE,0)
        val test = Utils.initPrevious(dateLimit, pickDate)
        test.forEach {
            d{"initPrevious $it"}
        }
        val testDb = appDb.getRainPrevious(dateLimit.toString(), pickDate)
        testDb.forEach {
            d{"db $it"}
        }
        val dateData = Utils.synchronizeData(testDb,test)
        dateData.forEach {
            d{"after sync $it"}
        }
        dateData.sortByDescending { it.date }
        RainFragment2.adapter = RainAdapter(dateData,dateLimit.toString(), pickDate)
        RainFragment2.rvDate.adapter = RainFragment2.adapter
        viewPager.setCurrentItem(1,true)
    }


    override fun onColumnHeaderClick(column: Int) {
            d {"onColumnHeaderClick $column"}
    }

    override fun onRowHeaderClick(row: Int) {
        d {"onRowHeaderClick $row"}
    }

    override fun onLeftTopHeaderClick() {
        d {"onLeftnHeaderClick"}
    }

    override fun onItemClick(row: Int, column: Int) {
        d {"onItemClick row $row column $column"}
    }

    override fun onItemLongClick(row: Int, column: Int) {
        d {"onItemLongClick row $row column $column"}
    }

    override fun onLeftTopHeaderLongClick() {
        d {"onLeftTopHeaderClick"}
    }

    companion object {
        fun newInstance():RainFragment1 {
            return RainFragment1()
        }

        fun loadChart(context: Context){
            val data = RainFragment2.adapter.data
            data.sortByDescending { it.date }
            Utils.CalRainFall(dataR,data)
            setData(localID,data,context)
        }

        fun setData(localID:Int,rawList:MutableList<Model.Rain>,activity:Context) {
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
            val dataSets = CombinedData()
            val setRed: LineDataSet
            val setYellow : LineDataSet
            val setRawData :ScatterDataSet
            val lastData : ScatterDataSet


            red.add(Entry(dataR[0].x,dataR[0].y))
            red.add(Entry(dataR[1].x,dataR[1].y))
            yellow.add(Entry(dataR[2].x,dataR[2].y))
            yellow.add(Entry(dataR[3].x,dataR[3].y))

            if(rawList.size > 0) {
                for (d in rawList){
                    rawData.add(Entry(d.previousRain,d.currentRain))
                }
                rawData.removeAt(0)
                lastPosition.add(Entry(rawList.first().previousRain,rawList.first().currentRain))
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
                setRawData = mChart.data.getDataSetByIndex(2) as ScatterDataSet
                setRawData.values = rawData
                lastData = mChart.data.getDataSetByIndex(3) as ScatterDataSet
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



                //init rawList
                setRawData = ScatterDataSet(rawData,"")
                setRawData.values = rawData
                setRawData.setScatterShape(ScatterChart.ScatterShape.SQUARE)
                setRawData.setDrawIcons(false)
                setRawData.color = ContextCompat.getColor(activity,R.color.bluePlot)
                setRawData.valueTextColor = Color.BLACK
                setRawData.setDrawValues(true)
                setRawData.valueTextSize = 9f
                setRawData.formSize = 15f


                //

                lastData = ScatterDataSet(lastPosition,"ตำแหน่งล่าสุด")
                lastData.values = lastPosition
                lastData.setScatterShape(ScatterChart.ScatterShape.CIRCLE)
                lastData.setDrawIcons(false)
                lastData.color = ContextCompat.getColor(activity,R.color.green)
                lastData.valueTextColor = Color.BLACK
                lastData.setDrawValues(true)
                lastData.valueTextSize = 9f
                lastData.formSize = 15f


                // create a data object with the datasets
                val lineSet = ArrayList<ILineDataSet>()
                lineSet.add(setRed)
                lineSet.add(setYellow)
                val lineData = LineData(lineSet)
                val scatSet = ArrayList<IScatterDataSet>()
                scatSet.add(setRawData)
                scatSet.add(lastData)
                val scatData = ScatterData(scatSet)

                // set data
                dataSets.setData(lineData)
                dataSets.setData(scatData)
                mChart.data = dataSets
                mChart.notifyDataSetChanged() // let the chart know it's data changed
                mChart.invalidate() // refresh
            }
        }

        lateinit var mChart : CombinedChart
        lateinit var appDb:AppDatabase
        var dataR : MutableList<Model.valRain> = ArrayList()
        var localID: Int =0
        var pickDate=Date()
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appDb = AppDatabase(context!!)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_rain1, container, false)


        return view
    }

    val DATA_TO_SHOW = arrayOf(arrayOf("This", "is", "a", "test"), arrayOf("and", "a", "second", "test"))

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val calendar = Calendar.getInstance()
        calendar.time = Date()
        calendar.set(calendar.get(Calendar.YEAR)+543,calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),7,0,0)
        pickDate = Utils.getDateWithFormat(Date())
        edt_location.setText(locations[localID])
        edt_date.setText(Utils.getDateSlash(calendar.time))
        viewRoot.inflate()
        initGraph(view!!)


        //tableView.headerAdapter = SimpleTableHeaderAdapter(activity,"วันที่", "ปริมาณน้ำฝน", "น้ำสะสม3วัน", "ระดับความเสี่ยงต่อการเกิดดินถล่ม")
        //tableView.dataAdapter = SimpleTableDataAdapter(activity,DATA_TO_SHOW)

        PushDown.setOnTouchPushDown(btn_update_grap)

        btn_update_grap.setOnClickListener {
           loadChart(context!!)
            /*
            scrollView.fullScroll(View.FOCUS_DOWN)
            val lastChild = scrollView.getChildAt(scrollView.childCount - 1)
            val bottom = lastChild.bottom + scrollView.paddingBottom
            val sy = scrollView.scrollY
            val sh = scrollView.height
            val delta = bottom - (sy + sh)
            scrollView.postDelayed({
                scrollView.smoothScrollBy(0, delta)
            },2000)
            */
        }

        btn_table.setOnClickListener {
            val dR = Intent()
            var rawData = ArrayList<Model.valRain>()
            dataR.forEach {
                rawData.add(it)
            }
            dR.putParcelableArrayListExtra("dr",rawData)
            startActivity(dR.setClass(activity,ActivityRiskBar::class.java))
        }

        edt_date.setOnClickListener { view ->
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            //showSpinner()
            showDateThai()
        }
        edt_date.isFocusableInTouchMode = false
        edt_date.isFocusable =false
        edt_location.setOnClickListener { view ->
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
            showLocation()
        }
        edt_location.isFocusableInTouchMode = false
        edt_location.isFocusable = false

    }


    fun showLocation(){
        MaterialDialog.Builder(context!!)
                .title("ภูมิภาค")
                .items(locations)
                .itemsCallbackSingleChoice(localID) { dialog, itemView, which, text ->
                    localID = which
                    edt_location.setText(locations[which])
                    //setData(localID,ArrayList())
                    loadChart(context!!)
                    true
                }
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

    fun showDateThai(){
        val max = Calendar.getInstance()
        val min = Calendar.getInstance()
        max.set(max.get(Calendar.YEAR),max.get(Calendar.MONTH),max.get(Calendar.DAY_OF_MONTH),7,0,0)
        min.set(max.get(Calendar.YEAR),max.get(Calendar.MONTH),max.get(Calendar.DAY_OF_MONTH),7,0,0)
        val mount = max.get(Calendar.MONTH)
        val dOfm = max.get(Calendar.DAY_OF_MONTH)
        val year = max.get(Calendar.YEAR)
        val datePopup = com.layernet.thaidatetimepicker.date.DatePickerDialog.newInstance(this, year, mount, dOfm)
        max.add(Calendar.DATE,1)
        datePopup.maxDate = max
        min.add(Calendar.DATE,-30)
        datePopup.minDate = min
        datePopup?.show(datePopup.childFragmentManager,"datepicker")
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
        //xAxis.axisMaximum =365f
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
        //leftAxis.axisMaximum = 365f
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
        setData(localID,ArrayList(),context!!)

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







}