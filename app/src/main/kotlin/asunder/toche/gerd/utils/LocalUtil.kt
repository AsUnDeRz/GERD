package utils

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.os.LocaleList
import android.preference.PreferenceManager
import java.util.*


/**
 * Created by ToCHe on 10/23/2017 AD.
 */
object LocalUtil {

    @Suppress("DEPRECATION")
    fun onAttach(context: Context?): Context? {
        var locale :String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context?.resources?.configuration?.locales?.get(0)?.language!!
        } else {
            context?.resources?.configuration?.locale?.language!!
        }
        val lang = getPersistedData(context, locale)
        return setLocale(context, lang)
    }

    fun onAttach(context: Context?, defaultLanguage: String): Context? =
            setLocale(context, defaultLanguage)

    @Suppress("DEPRECATION")
    fun getLanguage(context: Context): String? {
        var locale :String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context?.resources?.configuration?.locales?.get(0)?.language!!
        } else {
            context?.resources?.configuration?.locale?.language!!
        }
        return  locale

    }

    fun setLocale(context: Context?, language: String?): Context? {
        persist(context, language)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, language)
        } else updateResourcesLegacy(context, language)

    }

    private fun getPersistedData(context: Context?, defaultLanguage: String): String? {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(KEYPREFER.language, defaultLanguage)
    }

    private fun persist(context: Context?, language: String?) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()

        editor.putString(KEYPREFER.language, language)
        editor.apply()
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context?, language: String?): Context? {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val localeList = LocaleList(locale)
        LocaleList.setDefault(localeList)

        val configuration = context?.resources?.configuration
        configuration?.setLocale(locale)

        return context?.createConfigurationContext(configuration)
    }

    @Suppress("DEPRECATION")
    private fun updateResourcesLegacy(context: Context?, language: String?): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)

        val resources = context?.resources

        val configuration = resources?.configuration
        configuration?.locale = locale
        resources?.updateConfiguration(configuration, resources.displayMetrics)
        return context!!
    }
}