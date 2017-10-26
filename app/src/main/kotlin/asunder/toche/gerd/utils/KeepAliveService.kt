package utils

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder


/**
 * Created by ToCHe on 10/21/2017 AD.
 */
class KeepAliveService : Service() {

    override fun onBind(intent: Intent): IBinder {
        return sBinder
    }

    companion object {
        private val sBinder = Binder()
    }
}