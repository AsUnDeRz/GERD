package asunder.toche.gerd

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import com.github.ajalt.timberkt.Timber.d
import com.synnapps.carouselview.ImageListener
import kotlinx.android.synthetic.main.activity_main.*
import utils.CustomTabActivityHelper
import utils.Prefer
import java.text.SimpleDateFormat
import java.util.*


class ActivityMain : AppCompatActivity() {

    private var mCustomTabActivityHelper: CustomTabActivityHelper? = null
    lateinit var img : ArrayList<String>
    var mockBg = arrayOf("","","","")
    var dateList = arrayListOf<String>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //openCustomTab("http://158.108.44.242/dmrweb/index.php")
        //http://158.108.44.242/dmrweb/index.php
        setUpImagesForcast()


        btn_back.setOnClickListener {
            var current = carousel.currentItem
            carousel.currentItem = current-1
        }
        btn_next.setOnClickListener {
            var current = carousel.currentItem
            carousel.currentItem = current+1
        }
    }


    fun setUpImagesForcast(){
        img = calUrlImage()

        PushDown.setOnTouchPushDown(btn_back)
        PushDown.setOnTouchPushDown(btn_next)
        loadImage(img[1])
        txtDate.text = "ปัจจุบัน 07:00 น "+dateList[1]
        carousel.pageCount = mockBg.size
        carousel.setImageListener(imageListener)
        carousel.currentItem = 1
        carousel.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                when(position){
                    0 ->{
                        txtDate.text = "เมื่อวาน 07:00 น "+dateList[position]
                        loadImage(img[position])
                        btn_back.visibility = View.INVISIBLE
                        btn_next.visibility = View.VISIBLE
                    }
                    1 ->{
                        txtDate.text = "ปัจจุบัน 07:00 น "+dateList[position]
                        loadImage(img[position])
                        btn_back.visibility = View.VISIBLE
                        btn_next.visibility = View.VISIBLE
                    }
                    2 ->{
                        txtDate.text = "คาดการณ์ล่วงหน้า 1 วัน 07:00 น "+dateList[position]
                        loadImage(img[position])
                        btn_back.visibility = View.VISIBLE
                        btn_next.visibility = View.VISIBLE
                    }
                    3 ->{
                        txtDate.text = "คาดการณ์ล่วงหน้า 2 วัน 07:00 น "+dateList[position]
                        loadImage(img[position])
                        btn_back.visibility = View.VISIBLE
                        btn_next.visibility = View.INVISIBLE
                    }
                }

            }
        })

    }


    private fun openCustomTab(URL: String) {

        mCustomTabActivityHelper = CustomTabActivityHelper()
        mCustomTabActivityHelper!!.setConnectionCallback(mConnectionCallback)
        mCustomTabActivityHelper!!.mayLaunchUrl(Uri.parse(URL), null, null)

        //ตัวแปรนี้จะให้ในการกำหนดค่าต่างๆ ที่ข้างล่างนี้
        val intentBuilder = CustomTabsIntent.Builder()

        intentBuilder.setToolbarColor(ContextCompat.getColor(this, R.color.green))

        //กำหนดให้มี Animation เมื่อ Custom tab เข้ามาและออกไป ถ้าไม่มีจะเหมือน Activity ที่เด้งเข้ามาเลย
        setAnimation(intentBuilder)

        //Launch Custome tab ให้ทำงาน
        CustomTabActivityHelper.openCustomTab(
                this, intentBuilder.build(), Uri.parse(URL), WebviewFallback())
    }

    private fun setAnimation(intentBuilder: CustomTabsIntent.Builder) {
        //intentBuilder.setStartAnimations(this, android.R.anim.slide_out_right, android.R.anim.slide_in_left)
        intentBuilder.setExitAnimations(this, android.R.anim.slide_in_left, android.R.anim.slide_out_right)
    }

    // You can use this callback to make UI changes
    private val mConnectionCallback = object : CustomTabActivityHelper.ConnectionCallback {
        override fun onCustomTabsConnected() {
            //Toast.makeText(this@PointHistriesActivity, "Connected to service", Toast.LENGTH_SHORT).show()
        }

        override fun onCustomTabsDisconnected() {
            //Toast.makeText(this@PointHistriesActivity, "Disconnected from service", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onStart() {
        super.onStart()
        mCustomTabActivityHelper?.bindCustomTabsService(this)
    }

    override fun onStop() {
        super.onStop()
        mCustomTabActivityHelper?.unbindCustomTabsService(this)
    }

    inner class WebviewFallback : CustomTabActivityHelper.CustomTabFallback {
        override fun openUri(activity: Activity, uri: Uri) {
            val intent = Intent(activity, WebViewActivity::class.java)
            intent.putExtra("KEY_URL", uri.toString())
            activity.startActivity(intent)
        }
    }

    var imageListener: ImageListener = ImageListener {
        position, imageView ->
        Glide.with(this@ActivityMain)
                .load(mockBg[position])
                .into(imageView)
    }
    fun loadImage(url: String){
        Glide.with(this@ActivityMain)
                .load(url)
                .error(R.drawable.data2)
                .listener(LoggingListener())
                .into(map)
    }

    fun calUrlImage() : ArrayList<String>{
        val data = Prefer.getForcast(this)
        var urlList = arrayListOf<String>()
        var baseUrl = "http://gerdmodel.ku.ac.th/dmrprogram_new/geopng/"
        val lastUpdate = Date(Prefer.getLastUpdate(this))
        dateList = arrayListOf()


        if (data != ""){
            val result = data.substring(1,data.length-1).split(",")
            result.forEach {
                urlList.add(baseUrl+it.substring(1,it.length-1))
                dateList.add(it.substring(1,it.length-5))
            }
        }else{
            urlList.add(baseUrl+getPreviusDate(lastUpdate)+".png")
            urlList.add(baseUrl+getDateSlash(lastUpdate)+".png")
            urlList.add(baseUrl+getFutureDate(lastUpdate,1)+".png")
            urlList.add(baseUrl+getFutureDate(lastUpdate,2)+".png")

            dateList.add(getPreviusDate(lastUpdate))
            dateList.add(getDateSlash(lastUpdate))
            dateList.add(getFutureDate(lastUpdate,1))
            dateList.add(getFutureDate(lastUpdate,2))
        }

        urlList.forEach {
            d{it}
        }

        return urlList
    }

    fun getDateSlash(date: Date):String{
        val fmtOut = SimpleDateFormat("yyyy-MM-dd", Locale("en"))
        return fmtOut.format(date)
    }

    fun getPreviusDate(date: Date):String{
        var calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DATE, -1)
        return getDateSlash(calendar.time)
    }
    fun getFutureDate(date: Date,plusDay: Int):String{
        var calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DATE, plusDay)
        return getDateSlash(calendar.time)
    }

    inner class LoggingListener<T, R> : RequestListener<T, R> {
        override fun onException(e: java.lang.Exception?, model: T, target: com.bumptech.glide.request.target.Target<R>?, isFirstResource: Boolean): Boolean {
            d{"OnException"}
            return false
        }

        override fun onResourceReady(resource: R, model: T, target: com.bumptech.glide.request.target.Target<R>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
            d{"OnResourceReady"}
            return false
        }
    }



}
