package asunder.toche.gerd

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteCursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.github.ajalt.timberkt.Timber
import com.github.ajalt.timberkt.Timber.d
import java.util.*


/**
 * Created by ToCHe on 10/28/2017 AD.
 */
class AppDatabase(internal var myCon: Context) : SQLiteOpenHelper(myCon, DATABASE_NAME, null, DATABASE_VERSION) {


    internal var cursor: SQLiteCursor? = null
    internal lateinit var sqlDb: SQLiteDatabase
    fun date2Db(date:Long):String = date.toString().substring(0,10)+"000"


    fun open(): AppDatabase {

        sqlDb = this.writableDatabase
        return this
    }

    override fun close() {
        this.close()
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_RAIN_TABLE = "CREATE TABLE " + TABLE_RAIN +
                "(" +
                KEY_RAIN_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_RAIN_DATE + " TEXT," + // Define a foreign key
                KEY_RAIN_CURRENT + " REAL" +
                ")"
        db.execSQL(CREATE_RAIN_TABLE)
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " +TABLE_RAIN)
            onCreate(db)
        }
    }

    fun addRain(currentRain:Float,date:Long) {
        // Create and/or open the database for writing
        val db = writableDatabase
        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        d{date2Db(date)}
        db.beginTransaction()
        try {
            val values = ContentValues()
            values.put(KEY_RAIN_DATE, date2Db(date))
            values.put(KEY_RAIN_CURRENT, currentRain)

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_RAIN, null, values)
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Timber.d { "Error while trying to database" }
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun getRainWithDate(date: Long):Model.Rain{
        val rain = Model.Rain()
        rain.date = Date((date2Db(date)).toLong())
        // Select All Query
        val selectQuery = "SELECT  * FROM $TABLE_RAIN WHERE $KEY_RAIN_DATE = ${date2Db(date)}"

        val db = writableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        // looping through all rows and adding to list
        try{
            if (cursor.moveToFirst()) {
                    rain.id = cursor.getInt(0)
                    rain.date = Date(cursor.getLong(1))
                    rain.currentRain = cursor.getFloat(2)
                    rain.previousRain = 0.0f
                    rain.status =Model.StatusRain.LOW
            }

        } catch (e: Exception) {
            Timber.d { "Error while trying to get posts from database" }
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            db.close()
        }
        // return contact list
        d{"return rain $rain"}
        return rain
    }

    fun updateRain(rain:Model.Rain){
        val db = writableDatabase
        val values = ContentValues()
        values.put(KEY_RAIN_DATE, date2Db(rain.date.time))
        values.put(KEY_RAIN_CURRENT, rain.currentRain)

        d { "update rain with id [" + rain.id + "] current ["+rain.currentRain+"]" }
        // updating row
        db.update(TABLE_RAIN, values, KEY_RAIN_ID + " = ?", arrayOf(rain.id.toString()))
        db.close()

    }
    fun deleteRain(id:String){
        val db = writableDatabase
        Timber.d { "Delete Rain with id[$id]" }
        db.delete(TABLE_RAIN, KEY_RAIN_ID + " = ?", arrayOf(id))
        db.close()
    }

    fun getRainPrevious(limit: String,pickDate:Date):MutableList<Model.Rain>{
        val rainList = ArrayList<Model.Rain>()
        // Select All Query
        val conditionDate =date2Db(pickDate.time)
        val selectQuery = "SELECT  * FROM $TABLE_RAIN WHERE $KEY_RAIN_DATE <= $conditionDate ORDER BY $KEY_RAIN_DATE DESC LIMIT $limit"

        d{conditionDate}
        val db = writableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        // looping through all rows and adding to list
        try{
            if (cursor.moveToFirst()) {
                do {
                    rainList.apply {
                        d{cursor.getLong(1).toString()+" Date = "+Date(cursor.getLong(1))}
                        add(Model.Rain(
                                cursor.getInt(0),
                                Date(cursor.getLong(1)),
                                cursor.getFloat(2),
                                0.0f,Model.StatusRain.LOW))
                    }
                } while (cursor.moveToNext())
            }

        } catch (e: Exception) {
            Timber.d { "Error while trying to get posts from database" }
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            db.close()
        }
        // return contact list
        return rainList
    }
    fun getRainList(limit:String) : MutableList<Model.Rain>{
        val rainList = ArrayList<Model.Rain>()
        // Select All Query
        val selectQuery = "SELECT  * FROM " + TABLE_RAIN +" ORDER BY "+ KEY_RAIN_DATE+" DESC LIMIT "+limit

        val db = writableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        // looping through all rows and adding to list
        try{
            if (cursor.moveToFirst()) {
                do {
                    rainList.apply {
                        add(Model.Rain(
                                cursor.getInt(0),
                                Date(cursor.getLong(1)),
                                cursor.getFloat(2),
                                0.0f,Model.StatusRain.LOW))
                    }
                } while (cursor.moveToNext())
            }

        } catch (e: Exception) {
            Timber.d { "Error while trying to get posts from database" }
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            db.close()
        }
        // return contact list
        return rainList
    }

    fun deleteAllRain(){
        val db = writableDatabase
        Timber.d { "Delete all record rain" }
        db.delete(Companion.TABLE_RAIN,null,null)
        db.close()
    }
    companion object {
        // Database Info
        private val DATABASE_NAME = "gerd"
        private val DATABASE_VERSION = 1

        // Table Names
        private val TABLE_RAIN = "rain"

        // rain Table Columns
        private val KEY_RAIN_ID = "id"
        private val KEY_RAIN_DATE = "date"
        private val KEY_RAIN_CURRENT = "current_rain"


    }
}