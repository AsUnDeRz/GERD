package asunder.toche.gerd

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.github.ajalt.timberkt.Timber.d
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_home.*
import utils.CustomTabActivityHelper
import utils.Prefer
import java.util.*


/**
 * Created by ToCHe on 10/22/2017 AD.
 */
class ActivityHome:AppCompatActivity(),SurfaceHolder.Callback{

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        d{"Surface onChanged"}
    }
    override fun surfaceCreated(holder: SurfaceHolder) {
        d{"Surface onCreate"}
        mp?.setDisplay(holder)
        mp?.start()
        mp?.isLooping = true
    }
    override fun surfaceDestroyed(holder: SurfaceHolder) {
        d{"Surface onDestroyed"}
    }

    private var mCustomTabActivityHelper: CustomTabActivityHelper? = null

    var permissions = arrayOf(Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

    var holder:SurfaceHolder? =null
    var mp :MediaPlayer?=null
    var sv :SurfaceView?=null
    lateinit var snackBar:Snackbar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        toolbar.setBackgroundColor(Color.TRANSPARENT)
        initSnackBar()
        getVersionCode()
        callLastUpdate()
        callforcast()
        val window = this.window
        //sv = findViewById<SurfaceView>(R.id.surfaceView1)
        //mp = MediaPlayer.create(this, R.raw.footage)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = Color.TRANSPARENT
        }
        checkPermission()

        PushDown.setOnTouchPushDown(btn_link1)
        PushDown.setOnTouchPushDown(btn_link2)
        PushDown.setOnTouchPushDown(btn_link3)
        PushDown.setOnTouchPushDown(btn_link4)
        btn_link1.setOnClickListener {
            if(!snackBar.isShown) {
                openCustomTab("http://www.landslide-engineering.com/warningls.php")
            }else{
                openStore()
            }
        }
        btn_link2.setOnClickListener {
            if(!snackBar.isShown) {
                //openCustomTab("http://158.108.44.242/dmrweb/")
                startActivity(Intent().setClass(this@ActivityHome,ActivityMain::class.java))
            }else{
                openStore()
            }
        }
        btn_link3.setOnClickListener {
            if(!snackBar.isShown) {
                openCustomTab("http://158.108.44.241/doichang/")
            }else{
                openStore()
            }
        }

        btn_link4.setOnClickListener {
            if(!snackBar.isShown) {
                startActivity(Intent().setClass(this@ActivityHome, ActivityRain::class.java))
                finish()
            }else{
                openStore()
            }
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
            //recreate()
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

    /*
    override fun onResume() {
        super.onResume()
        holder = sv?.holder
        holder?.addCallback(this@ActivityHome)
        d{"OnResume"}
    }

    override fun onPause() {
        super.onPause()
        if(mp!!.isPlaying){
            mp?.pause()
        }
        holder?.removeCallback(this@ActivityHome)
        d{"OnPause"}

    }
    */

    fun getVersionCode(){
        val versionCode = Prefer.getVersionCode(this@ActivityHome)
        if(versionCode == "") {
            val mRootRef = FirebaseDatabase.getInstance().reference.child("android_version")
            mRootRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(databaseError: DatabaseError) {
                    d { databaseError.message }
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val version = dataSnapshot.value
                    Prefer.setVersionCode(this@ActivityHome,version.toString())
                    checkVersion(version.toString())
                    d { version.toString() }
                }
            })
        }else{
            checkVersion(versionCode)
        }




    }

    fun checkVersion(version :String){
        Log.d("BuildConfig",BuildConfig.VERSION_NAME)
        Log.d("version",version)

        if(!BuildConfig.VERSION_NAME.equals(version,true)){
           snackBar.show()
        }else{
            Log.d("version",version)
        }

    }

    fun openStore(){
        val appPackageName = packageName // getPackageName() from Context or Activity object
        try {
            startActivityForResult(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)),154)
        } catch (anfe: android.content.ActivityNotFoundException) {
            startActivityForResult(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)),154)
        }
    }

    fun initSnackBar(){
        snackBar = Snackbar.make(rootHome,"กรุณาอัพเดทเวอร์ชั่นใหม่",Snackbar.LENGTH_INDEFINITE).setAction("ตกลง",{
            openStore()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 154){
            getVersionCode()
        }
    }

    fun callLastUpdate(){
        val lastUpdate = FirebaseDatabase.getInstance().reference.child("last_update")
        lastUpdate.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                d { databaseError.message }
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val result = dataSnapshot.value
                Prefer.setLastUpdate(this@ActivityHome, result as Long)
                d { result.toString() }
            }
        })
    }

    fun callforcast(){
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "http://gerdmodel.ku.ac.th/dmrweb/get_current_result.php"

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener<String> { response ->
                    // Display the first 500 characters of the response string.
                    println(response)
                    Prefer.setForcast(this,response)
                },
                Response.ErrorListener {
                    println(it.message)
                })

        queue.add(stringRequest)
    }
}