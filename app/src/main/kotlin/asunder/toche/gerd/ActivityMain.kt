package asunder.toche.gerd

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.View
import com.bumptech.glide.Glide
import com.synnapps.carouselview.ImageListener
import kotlinx.android.synthetic.main.activity_main.*
import utils.CustomTabActivityHelper

class ActivityMain : AppCompatActivity() {

    private var mCustomTabActivityHelper: CustomTabActivityHelper? = null
    val img = arrayOf("","","","")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //openCustomTab("http://158.108.44.242/dmrweb/index.php")
        //http://158.108.44.242/dmrweb/index.php


        PushDown.setOnTouchPushDown(btn_back)
        PushDown.setOnTouchPushDown(btn_next)
        loadImage(R.drawable.data0)
        title = "ปัจจุบัน 07:00 น"
        carousel.pageCount = img.size
        carousel.setImageListener(imageListener)
        carousel.currentItem = 1
        carousel.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                when(position){
                    0 ->{
                        title = "เมื่อวาน 07:00 น"
                        loadImage(R.drawable.data)
                        btn_back.visibility = View.INVISIBLE
                        btn_next.visibility = View.VISIBLE
                    }
                    1 ->{
                        title = "ปัจจุบัน 07:00 น"
                        loadImage(R.drawable.data0)
                        btn_back.visibility = View.VISIBLE
                        btn_next.visibility = View.VISIBLE
                    }
                    2 ->{
                        title = "คาดการณ์ล่วงหน้า 1 วัน 07:00 น"
                        loadImage(R.drawable.data1)
                        btn_back.visibility = View.VISIBLE
                        btn_next.visibility = View.VISIBLE
                    }
                    3 ->{
                        title = "คาดการณ์ล่วงหน้า 2 วัน 07:00 น"
                        loadImage(R.drawable.data2)
                        btn_back.visibility = View.VISIBLE
                        btn_next.visibility = View.INVISIBLE
                    }
                }

            }
        })

        btn_back.setOnClickListener {
            var current = carousel.currentItem
            carousel.currentItem = current-1
        }
        btn_next.setOnClickListener {
            var current = carousel.currentItem
            carousel.currentItem = current+1
        }
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
                .load(img[position])
                .into(imageView)
    }
    fun loadImage(img:Int){
        Glide.with(this@ActivityMain)
                .load(img)
                .into(map)
    }
}
