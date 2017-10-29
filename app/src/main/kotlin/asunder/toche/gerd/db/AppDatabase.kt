package asunder.toche.gerd

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteCursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.github.ajalt.timberkt.Timber
import java.util.*


/**
 * Created by ToCHe on 10/28/2017 AD.
 */
class AppDatabase(internal var myCon: Context) : SQLiteOpenHelper(myCon, DATABASE_NAME, null, DATABASE_VERSION) {


    internal var cursor: SQLiteCursor? = null
    internal lateinit var sqlDb: SQLiteDatabase

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
        db.beginTransaction()
        try {
            val values = ContentValues()
            values.put(KEY_RAIN_DATE, date.toString())
            values.put(KEY_RAIN_CURRENT, currentRain)

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_RAIN, null, values)
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Timber.d { "Error while trying to add login to database" }
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun updateUser(){

    }

    fun haveUser(): String {
        var mobile = "empty"
        // SELECT * FROM POSTS
        // LEFT OUTER JOIN USERS
        // ON POSTS.KEY_POST_USER_ID_FK = USERS.KEY_USER_ID
        val LOGIN_SELECT_QUERY = String.format("SELECT * FROM " + TABLE_USER)

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        val db = readableDatabase
        val cursor = db.rawQuery(LOGIN_SELECT_QUERY, null)
        try {
            Timber.d { "SQLLITE Count record in table login" + cursor!!.count }
            if (cursor!!.count === 1) {
                if (cursor!!.moveToFirst()) {
                    do {
                        mobile = cursor.getString(cursor.getColumnIndexOrThrow(KEY_LOGIN_USERID))
                        Timber.d { "SQLLITE My Email = " + mobile }
                    } while (cursor.moveToNext())
                }
            }

        } catch (e: Exception) {
            Timber.d { "Error while trying to get posts from database" }
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            db.close()
        }
        return mobile
    }



    fun updateRain(rain:Model.Rain){
        val db = writableDatabase
        val values = ContentValues()
        values.put(KEY_RAIN_DATE, rain.date.time.toString())
        values.put(KEY_RAIN_CURRENT, rain.currentRain)

        Timber.d { "update rain with id [" + rain.id + "] current ["+rain.currentRain+"]" }
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
    fun getNotificationList() : ArrayList<Model.Rain>{
        val rainList = ArrayList<Model.Rain>()
        // Select All Query
        val selectQuery = "SELECT  * FROM " + TABLE_RAIN

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


    fun getCountNoti(): Model.NotiPercent{
        val tracked = getNotiTracked().size.toString()
        val missing = getNotiMissing().size.toString()
        val waiting = getNotiWaiting().size.toString()
        val totalNoti = getNotificationList().size.toString()

        Timber.d { "tracked [$tracked] Missing [$missing] Waiting [$waiting] Total [$totalNoti]" }
        return Model.NotiPercent(tracked,missing,waiting,totalNoti)
    }


    fun getNotiWaiting():ArrayList<Model.Notification>{
        Timber.d { "Select with Status Waiting" }
        return getNotisWithStatus(KEYPREFER.WAITING)
    }
    fun getNotiMissing():ArrayList<Model.Notification>{
        Timber.d { "Select with Status Missing" }
        return getNotisWithStatus(KEYPREFER.MISSING)
    }
    fun getNotiTracked():ArrayList<Model.Notification>{
        Timber.d { "Select with Status Tracked" }
        return getNotisWithStatus(KEYPREFER.TRACKED)

    }
    fun getNotisWithStatus(status:Int) : ArrayList<Model.Notification> {
        val notiList = ArrayList<Model.Notification>()
        // Select All Query
        val selectQuery = "SELECT  * FROM $TABLE_NOTIFICATION WHERE $KEY_NOTI_STATUS =?"

        val db = writableDatabase
        val cursor = db.rawQuery(selectQuery, arrayOf(status.toString()))

        // looping through all rows and adding to list
        try {
            if (cursor.moveToFirst()) {
                do {
                    notiList.apply {
                        add(Model.Notification(
                                cursor.getString(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                Date(cursor.getLong(3))
                        ))
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
        return notiList
    }

    fun getNotiWithState(id:String) : Model.Notification?{
        var notification :Model.Notification? = null
        val db = readableDatabase
        val cursor = db.query(TABLE_NOTIFICATION, arrayOf(KEY_NOTI_ID, KEY_NOTI_TITLE, KEY_NOTI_DESC, KEY_NOTI_TIME),
                KEY_NOTI_ID +"=?", arrayOf(id),null,null,null,null)
        try{
            cursor?.moveToFirst()
            notification = Model.Notification(cursor.getString(0),cursor.getString(1),cursor.getString(2),
                    Date(cursor.getLong(3)))
        }catch (e: Exception) {
            Timber.d { "Error while trying to get posts from database" }
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            db.close()
        }

        return notification
    }

    //function knowledge group
    fun addKnowledgeGroup(data:ArrayList<Model.KnowledgeGroup>){
        // Create and/or open the database for writing
        val db = writableDatabase
        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction()
        try {
            for(group in data){
                val values = ContentValues()
                values.put(KEY_KG_ID, group.group_id)
                values.put(KEY_KG_NAME_TH, group.group_name_th)
                values.put(KEY_KG_NAME_EN,group.group_name_eng)
                values.put(KEY_KG_VERSION,group.version)
                values.put(KEY_KG_POINT,group.sumpoint)
                db.insertOrThrow(TABLE_KNOWLEGDE_GROUP, null, values)
                Timber.d { "Insert [" + group.group_id + "] [" + group.group_name_eng + "] [" + group.sumpoint + "]" }
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Timber.d { "Error while trying to add login to database" }
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun getKnowledgeGroup() :ObservableArrayList<Model.KnowledgeGroup>{
        val knowledgeGroupList = ObservableArrayList<Model.KnowledgeGroup>()
        // Select All Query
        val selectQuery = "SELECT  * FROM $TABLE_KNOWLEGDE_GROUP"

        val db = writableDatabase
        val cursor = db.rawQuery(selectQuery,null)
        try {
            if (cursor.moveToFirst()) {
                do {
                    knowledgeGroupList.apply {
                        add(Model.KnowledgeGroup(
                                cursor.getString(0),
                                cursor.getString(2),
                                cursor.getString(1),
                                cursor.getString(3),
                                cursor.getString(4)))
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
        // return  list
        return knowledgeGroupList
    }

    fun deleteAllKnowledgeGroup(){
        val db = writableDatabase
        Timber.d { "Delete all record knowledge group" }
        db.delete(TABLE_KNOWLEGDE_GROUP,null,null)
        db.close()
    }


    //function knowledge content
    fun addKnowledgeContent(data:ObservableArrayList<Model.RepositoryKnowledge>){
        // Create and/or open the database for writing
        val db = writableDatabase
        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction()
        try {
            for(content in data){
                val values = ContentValues()
                var contentTHlong=""
                var contentENlong=""
                var link=""
                if(content.content_th_long != null){
                    contentTHlong = content.content_th_long
                }
                if(content.content_eng_long != null){
                    contentENlong = content.content_eng_long
                }
                if(content.link != null){
                    link = content.link
                }
                values.put(KEY_KC_ID, content.id)
                values.put(KEY_KC_GROUP_ID,content.group_id)
                values.put(KEY_KC_TITLE_TH,content.title_th)
                values.put(KEY_KC_TITLE_EN,content.title_eng)
                values.put(KEY_KC_CONTENT_TH,content.content_th)
                values.put(KEY_KC_CONTENT_EN,content.content_eng)
                values.put(KEY_KC_CONTENT_LONG_TH,contentTHlong)
                values.put(KEY_KC_CONTENT_LONG_EN,contentENlong)
                values.put(KEY_KC_IMAGE,"") // test content must null only
                values.put(KEY_KC_VERSION,content.version)
                values.put(KEY_KC_POINT,content.point)
                values.put(KEY_KC_LINK,link)
                db.insertOrThrow(TABLE_KNOWLEGDE_CONTENT, null, values)
                Timber.d { "Insert [" + content.id + "] [" + content.title_eng + "] [" + content.point + "]" }
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Timber.d { "Error while trying to add login to database" }
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun getKnowledgeContent() : ObservableArrayList<Model.RepositoryKnowledge> {
        var contentList = ObservableArrayList<Model.RepositoryKnowledge>()
        Timber.d { "getKnowledgeContent" }
        // Select All Query
        val selectQuery = "SELECT * FROM $TABLE_KNOWLEGDE_CONTENT"

        val db = readableDatabase
        val cursor = db.rawQuery(selectQuery,null)
        try {
            if (cursor.moveToFirst()) {
                do {
                    contentList.apply {
                        add(Model.RepositoryKnowledge(
                                cursor.getString(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getString(3),
                                cursor.getString(4),
                                cursor.getString(5),
                                cursor.getString(9),
                                cursor.getString(10),
                                arrayListOf("1","2","3","4","5"),
                                cursor.getString(9),
                                cursor.getString(6),
                                cursor.getString(7),
                                cursor.getString(11)
                        )
                        )
                    }
                } while (cursor.moveToNext())
            }
            if(contentList.size > 0) {
                val master = ObservableArrayList<Model.RepositoryKnowledge>().apply {
                    for (i in 0..2) {
                        add(contentList[i])
                    }
                }
                contentList = master
            }

        } catch (e: Exception) {
            Timber.d { "Error while trying to get posts from database [" + e.message + "]" }
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            db.close()
        }
        // return  list

        return contentList
    }

    fun getKnowledgeContent(id:String) :ObservableArrayList<Model.RepositoryKnowledge>{
        val contentList = ObservableArrayList<Model.RepositoryKnowledge>()
        Timber.d { "getKnowledgeContent by $id" }
        // Select All Query
        val selectQuery = "SELECT  * FROM $TABLE_KNOWLEGDE_CONTENT WHERE $KEY_KC_GROUP_ID =? "

        val db = writableDatabase
        val cursor = db.rawQuery(selectQuery,arrayOf(id))
        try {
            if (cursor.moveToFirst()) {
                do {
                    contentList.apply {
                        add(Model.RepositoryKnowledge(
                                cursor.getString(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getString(3),
                                cursor.getString(4),
                                cursor.getString(5),
                                cursor.getString(9),
                                cursor.getString(10),
                                arrayListOf("1","2","3","4","5"),
                                cursor.getString(9),
                                cursor.getString(6),
                                cursor.getString(7),
                                cursor.getString(11)
                        )
                        )
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
        // return  list
        return contentList
    }

    fun deleteAllKnowledgeContent(){
        val db = writableDatabase
        Timber.d { "Delete all record knowledge content" }
        db.delete(TABLE_KNOWLEGDE_CONTENT,null,null)
        db.close()
    }

    fun addProvince(data: ObservableArrayList<Model.Province>){
        // Create and/or open the database for writing
        val db = writableDatabase
        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction()
        try {
            for(content in data){
                val values = ContentValues()
                values.put(KEY_PROV_ID, content.province_id)
                values.put(KEY_PROV_TH,content.province_th)
                values.put(KEY_PROV_ENG,content.province_eng)
                values.put(KEY_PROV_LOCX,content.locx.toString())
                values.put(KEY_PROV_LOCY,content.locy.toString())
                db.insertOrThrow(TABLE_PROVINCE, null, values)
                Timber.d { "Insert [" + content.province_id + "] [" + content.province_th + "] [" + content.province_eng + "]" }
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Timber.d { "Error while trying to add login to database" }
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun getProvince() : ObservableArrayList<Model.Province> {
        var contentList = ObservableArrayList<Model.Province>()
        Timber.d { "getKnowledgeContent" }
        // Select All Query
        val selectQuery = "SELECT * FROM $TABLE_PROVINCE"
        val db = readableDatabase
        val cursor = db.rawQuery(selectQuery,null)
        try {
            if (cursor.moveToFirst()) {
                do {
                    contentList.apply {
                        add(Model.Province(
                                cursor.getString(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getString(3).toDouble(),
                                cursor.getString(4).toDouble())
                        )
                    }
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Timber.d { "Error while trying to get posts from database [" + e.message + "]" }
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            db.close()
        }
        // return  list
        return contentList

    }
    fun deleteAllProvince(){
        val db = writableDatabase
        Timber.d { "Delete all record province" }
        db.delete(TABLE_PROVINCE,null,null)
        db.close()
    }
    fun addRiskQustion(data:ObservableArrayList<Model.RiskQuestion>){
        // Create and/or open the database for writing
        val db = writableDatabase
        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction()
        try {
            for(content in data){
                val values = ContentValues()
                values.put(KEY_CHOICE_ID, content.choice_id)
                values.put(KEY_TITLE_TH,content.title_th)
                values.put(KEY_TITLE_ENG,content.title_eng)
                values.put(KEY_QUES1_TH,content.question1_th)
                values.put(KEY_QUES1_ENG,content.question1_eng)
                values.put(KEY_QUES2_TH,content.question2_th)
                values.put(KEY_QUES2_ENG,content.question2_eng)
                values.put(KEY_QUES3_TH,content.question3_th)
                values.put(KEY_QUES3_ENG,content.question3_eng)
                values.put(KEY_QUES4_TH,content.question4_th)
                values.put(KEY_QUES4_ENG,content.question4_eng)
                values.put(KEY_QUES5_TH,content.question5_th)
                values.put(KEY_QUES5_ENG,content.question5_eng)
                values.put(KEY_QUES6_TH,content.question6_th)
                values.put(KEY_QUES6_ENG,content.question6_eng)
                values.put(KEY_QUES7_TH,content.question7_th)
                values.put(KEY_QUES7_ENG,content.question7_eng)
                db.insertOrThrow(TABLE_RISK_QUESTION, null, values)
                Timber.d { "Insert [" + content.choice_id + "] [" + content.title_eng + "] [" + content.title_th + "]" }
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Timber.d { "Error while trying to add login to database" }
        } finally {
            db.endTransaction()
            db.close()
        }

    }
    fun getRiskQuestion()  : ObservableArrayList<Model.RiskQuestion> {
        var contentList = ObservableArrayList<Model.RiskQuestion>()
        Timber.d { "getRiskQuestion" }
        // Select All Query
        val selectQuery = "SELECT * FROM $TABLE_RISK_QUESTION"
        val db = readableDatabase
        val cursor = db.rawQuery(selectQuery,null)
        try {
            if (cursor.moveToFirst()) {
                do {
                    contentList.apply {
                        add(Model.RiskQuestion(
                                cursor.getString(0),
                                cursor.getString(1),
                                cursor.getString(2),
                                cursor.getString(3),
                                cursor.getString(4),
                                cursor.getString(5),
                                cursor.getString(6),
                                cursor.getString(7),
                                cursor.getString(8),
                                cursor.getString(9),
                                cursor.getString(10),
                                cursor.getString(11),
                                cursor.getString(12),
                                cursor.getString(13),
                                cursor.getString(14),
                                cursor.getString(15),
                                cursor.getString(16))
                        )
                    }
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Timber.d { "Error while trying to get posts from database [" + e.message + "]" }
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            db.close()
        }
        // return  list

        return contentList

    }
    fun deleteAllRiskQuestion(){
        val db = writableDatabase
        Timber.d { "Delete all record risk question" }
        db.delete(TABLE_RISK_QUESTION,null,null)
        db.close()
    }

    //db job
    fun addJobs(data:ObservableArrayList<Model.RepositoryJob>){
        // Create and/or open the database for writing
        val db = writableDatabase
        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction()
        try {
            for(content in data){
                val values = ContentValues()
                values.put(KEY_JOB_ID, content.occupation_id)
                values.put(KEY_JOB_TH,content.occupation_th)
                values.put(KEY_JOB_ENG,content.occupation_eng)
                db.insertOrThrow(TABLE_JOB, null, values)
                Timber.d { "Insert [" + content.occupation_id + "] [" + content.occupation_eng + "] [" + content.occupation_th + "]" }
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Timber.d { "Error while trying to add login to database" }
        } finally {
            db.endTransaction()
            db.close()
        }

    }
    fun getJobs()  : ObservableArrayList<Model.RepositoryJob> {
        var contentList = ObservableArrayList<Model.RepositoryJob>()
        Timber.d { "getJobs" }
        // Select All Query
        val selectQuery = "SELECT * FROM $TABLE_JOB"
        val db = readableDatabase
        val cursor = db.rawQuery(selectQuery,null)
        try {
            if (cursor.moveToFirst()) {
                do {
                    contentList.apply {
                        add(Model.RepositoryJob(
                                cursor.getString(0),
                                cursor.getString(1),
                                cursor.getString(2))
                        )
                    }
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Timber.d { "Error while trying to get posts from database [" + e.message + "]" }
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            db.close()
        }
        // return  list

        return contentList

    }

    //db national
    fun addNationals(data:ObservableArrayList<Model.RepositoryNational>){
        // Create and/or open the database for writing
        val db = writableDatabase
        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction()
        try {
            for(content in data){
                val values = ContentValues()
                values.put(KEY_NATION_ID, content.national_id)
                values.put(KEY_NATION_TH,content.nationality_th)
                values.put(KEY_NATION_ENG,content.nationality_eng)
                db.insertOrThrow(TABLE_NATIONAL, null, values)
                Timber.d { "Insert [" + content.national_id + "] [" + content.nationality_eng + "] [" + content.nationality_th + "]" }
            }
            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Timber.d { "Error while trying to add login to database" }
        } finally {
            db.endTransaction()
            db.close()
        }

    }
    fun getNations()  : ObservableArrayList<Model.RepositoryNational> {
        var contentList = ObservableArrayList<Model.RepositoryNational>()
        Timber.d { "getJobs" }
        // Select All Query
        val selectQuery = "SELECT * FROM $TABLE_NATIONAL"
        val db = readableDatabase
        val cursor = db.rawQuery(selectQuery,null)
        try {
            if (cursor.moveToFirst()) {
                do {
                    contentList.apply {
                        add(Model.RepositoryNational(
                                cursor.getString(0),
                                cursor.getString(1),
                                cursor.getString(2))
                        )
                    }
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Timber.d { "Error while trying to get posts from database [" + e.message + "]" }
        } finally {
            if (cursor != null && !cursor.isClosed) {
                cursor.close()
            }
            db.close()
        }
        // return  list

        return contentList

    }

    fun deleteAllContentWhenStart(){
        val db = writableDatabase
        Timber.d { "Delete all record risk question" }
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