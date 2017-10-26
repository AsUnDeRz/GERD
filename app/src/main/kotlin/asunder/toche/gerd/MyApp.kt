package asunder.toche.gerd

import android.app.Application
import timber.log.Timber
import utils.LocalUtil

/**
 * Created by ToCHe on 10/23/2017 AD.
 */


class MyApp:Application(){


    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

        LocalUtil.onAttach(applicationContext,"th")
    }
}