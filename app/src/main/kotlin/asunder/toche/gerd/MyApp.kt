package asunder.toche.gerd

import android.app.Application
import com.github.ajalt.timberkt.Timber.d
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import timber.log.Timber
import utils.LocalUtil
import utils.Prefer

/**
 * Created by ToCHe on 10/23/2017 AD.
 */


class MyApp:Application() {


    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        LocalUtil.onAttach(applicationContext, "th")

        val mRootRef = FirebaseDatabase.getInstance().reference.child("android_version")
        mRootRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                d { databaseError.message }
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val version = dataSnapshot.value
                Prefer.setVersionCode(this@MyApp, version.toString())
                d { version.toString() }
            }
        })

    }
}