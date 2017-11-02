package asunder.toche.gerd

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_home.*
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.media.MediaPlayer
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.view.View
import utils.CustomTabActivityHelper
import android.view.WindowManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import asunder.toche.gerd.R.id.toolbar
import com.github.ajalt.timberkt.Timber.d
import java.util.*


/**
 * Created by ToCHe on 10/22/2017 AD.
 */
class ActivityHome:AppCompatActivity(){

    private var mCustomTabActivityHelper: CustomTabActivityHelper? = null

    var permissions = arrayOf(Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        // Toolbar :: Transparent
        toolbar.setBackgroundColor(Color.TRANSPARENT)

        //setSupportActionBar(toolbar)
        //supportActionBar!!.title = "GERD"


        // Status bar :: Transparent
        val window = this.window

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.TRANSPARENT
        }
        //videoView.isDrawingCacheEnabled = true
        //videoView.setVideoURI(Uri.parse("android.resource://"+ packageName +"/"+R.raw.footage))
        //videoView.start()
        checkPermission()

        PushDown.setOnTouchPushDown(btn_link1)
        PushDown.setOnTouchPushDown(btn_link2)
        PushDown.setOnTouchPushDown(btn_link3)
        PushDown.setOnTouchPushDown(btn_link4)
        btn_link1.setOnClickListener {
            openCustomTab("http://www.landslide-engineering.com/warningls.php")
        }
        btn_link2.setOnClickListener {
            //startActivity(Intent().setClass(this@ActivityHome,ActivityMain::class.java))
            openCustomTab("http://158.108.44.242/dmrweb/")

        }
        btn_link3.setOnClickListener {
            openCustomTab("http://158.108.44.241/doichang/")
        }

        btn_link4.setOnClickListener {
            //openCustomTab("http://158.108.44.242/dmrweb/")
            startActivity(Intent().setClass(this@ActivityHome,ActivityRain::class.java))
            finish()
        }

    }

    fun checkPermission(){
        var result: Int
        val listPermissionsNeeded = ArrayList<String>()
        for (p in permissions) {
            result = ContextCompat.checkSelfPermission(this, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), 100)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 100) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // do something
                d{"All permission granted"}
            }
            return
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
       // setAnimation(intentBuilder)

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
            //Toast.makeText(this@ActivityHome, "Connected to service", Toast.LENGTH_SHORT).show()
            recreate()
        }

        override fun onCustomTabsDisconnected() {
            //Toast.makeText(this@ActivityHome, "Disconnected to service", Toast.LENGTH_SHORT).show()
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
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        val mp = MediaPlayer.create(this, R.raw.footage)

        val sv = findViewById<View>(R.id.surfaceView1) as SurfaceView
        val holder = sv.holder
        holder.addCallback(object : SurfaceHolder.Callback{
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                d{"Surface onChanged"}

            }

            override fun surfaceCreated(holder: SurfaceHolder) {
                d{"Surface onCreate"}
                mp.setDisplay(holder)
                mp.start()
                mp.isLooping = true
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                d{"Surface onDestroyed"}
            }
        })
    }
}