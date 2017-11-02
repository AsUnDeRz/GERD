package asunder.toche.gerd

import com.github.ajalt.timberkt.Timber.d
import com.github.mikephil.charting.data.Entry
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by ToCHe on 10/21/2017 AD.
 */
object Utils {

    fun getDateSlash(date: Date):String{
        val fmtOut = SimpleDateFormat("dd/MM/yyyy")
        return fmtOut.format(date)
    }

    fun getPreviusDate(date:Date):String{
        var calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DATE, -1)
        return getDateSlash(calendar.time)
    }



    fun CalEq(r : MutableList<Model.valRain>) : Model.Eq{
        val redM = (r[1].y-r[0].y)/(r[1].x-r[0].x)
        val yellowM = (r[3].y-r[2].y)/(r[3].x-r[2].x)
        val redC = r[0].y-(redM*r[0].x)
        val yellowC = r[3].y-(yellowM*r[3].x)

        d{"redM[$redM] yellowM[$yellowM] redC[$redC] yellowC[$yellowC]"}

        return Model.Eq(redM,redC,yellowM,yellowC)
    }

    fun FilterStatusRain(r:Model.Rain,eq: Model.Eq,offset:MutableList<Model.valRain>):Model.StatusRain {

        if (r.currentRain >= ((eq.redM * r.previousRain) + eq.redC) && r.currentRain >= offset[4].y && r.previousRain >= offset[4].x) {
            return Model.StatusRain.HIGH
        } else if (r.currentRain >= ((eq.yellowM * r.previousRain) + eq.yellowC) && r.currentRain >= offset[5].y && r.previousRain >= offset[5].x) {
            return Model.StatusRain.NORMAL
        }
        return Model.StatusRain.LOW
    }

    fun CalRainFall(r:MutableList<Model.valRain>,data : MutableList<Model.Rain>){

        //init condition by region
        val eq = CalEq(r)


        //cal and add previous 3 day rain
        for( i in 0 until data.lastIndex){
            val iStart = i +1
            val iEnd = i+ 4
            val previous = (iStart until iEnd)
                    .map { if(it <= data.lastIndex){data[it].currentRain}else{0.0f} }
                    .sum()

            data[i].previousRain = previous
            d{"Previous rain [$previous] by Date ["+data[i]+"]"}


            val statusRain = FilterStatusRain(data[i],eq,r)

            d{"Status Rain return $statusRain"}
            data[i].status = statusRain
        }
    }

    fun initRain(size:Int):MutableList<Model.Rain>{
        var data: MutableList<Model.Rain> = ArrayList()
        for(i in -size until  0){
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),7,0,0)
            calendar.add(Calendar.DATE,i)
            data.add(Model.Rain(0,calendar.time,0.0f,0.0f,Model.StatusRain.LOW))
        }
        data.removeAt(0)
        data.add(Model.Rain(0,Date(),0.0f,0.0f,Model.StatusRain.LOW))

        /*
        String sDate = "31012014";
SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy", Locale.getDefault());
Date date = dateFormat.parse(sDate);
Calendar calendar = Calendar.getInstance();
calendar.setTime(date);
calendar.add(Calendar.DATE, -1);
String yesterdayAsString = dateFormat.format(calendar.getTime());
         */
        data.sortByDescending { it.date }
        return data
    }

    class EntryYComparator : Comparator<Entry> {
        override fun compare(entry1: Entry, entry2: Entry): Int {
            val diff = entry1.y - entry2.y
            return if (diff == 0f) {
                0
            }else {
                if (diff > 0f)
                    1
                else
                    -1
            }
        }
    }

    fun compareData(appDb:AppDatabase,dataNew:MutableList<Model.Rain>,limit:String){
        var dataInDb = appDb.getRainList(limit)
        dataNew.forEach {
            d{"Data New "+it.toString()}
        }
        dataInDb.sortBy { it.date }
        for(a in dataInDb){
            //update rain when map date but values is dif
            dataNew.filter { Utils.getDateSlash(it.date) == Utils.getDateSlash(a.date) }
                    .forEach {
                        if(it.currentRain != a.currentRain){
                            d{"Update current rain in db"}
                            appDb.updateRain(Model.Rain(a.id,it.date,it.currentRain,0.0f,Model.StatusRain.LOW))
                        }
                    }
           // d{"Check size dataNew ${dataNew.size}"}
        }

        //filter new date
        dataNew.filter { dataInDb[dataInDb.lastIndex].date.time < it.date.time }
                .forEach {
                    d{"have newDate more one"}
                    d{Utils.getDateSlash(it.date)}
                    //add to database
                    appDb.addRain(it.currentRain,it.date.time)
                }
    }

    fun synchronizeData(appDb: AppDatabase,limit: String):MutableList<Model.Rain> {
        var rawData = appDb.getRainList("30")
        var newData = initRain(limit.toInt())

        d{"Check rawData size ${rawData.size} newData size ${newData.size}"}
        if(rawData.size > 0){
            for(raw in rawData){
                newData.filter { Utils.getDateSlash(it.date) == Utils.getDateSlash(raw.date) }
                        .forEach {
                            it.currentRain = raw.currentRain
                        }
            }
            return newData
        }else{
            return newData
        }
    }


}