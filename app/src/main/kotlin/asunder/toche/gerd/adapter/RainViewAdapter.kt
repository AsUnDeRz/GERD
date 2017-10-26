package adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import asunder.toche.gerd.RainFragment1
import asunder.toche.gerd.RainFragment2


/**
 * Created by ToCHe on 10/23/2017 AD.
 */
class RainViewAdapter(fragmentManager: FragmentManager, var context: Context) : FragmentPagerAdapter(fragmentManager) {


    val title= arrayOf("ระบุข้อมูลปริมาณน้ำฝน","กรอกข้อมูลปริมาณน้ำฝน")
    override fun getCount(): Int {
        return NUM_ITEMS
    }

    // Returns the fragment to display for that page
    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> return RainFragment1.newInstance()
            1 -> return RainFragment2.newInstance()
        //6 -> return ClinicInfoFragment.newInstance()
            else -> return null
        }
    }

    // Returns the page title for the top indicator
    override fun getPageTitle(position: Int): CharSequence {
        return title[position]
    }

    companion object {
        private val NUM_ITEMS = 2
    }
}