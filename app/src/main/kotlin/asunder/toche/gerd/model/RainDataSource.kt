package asunder.toche.gerd

import android.annotation.SuppressLint
import android.util.Log
import android.util.SparseArray
import asunder.toche.gerd.Model.StatusRain.*
import com.github.ajalt.timberkt.Timber.d
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by ToCHe on 10/27/2017 AD.
 */

// data:ArrayList<Array<String>>
class RainDataSource(val data:MutableList<Model.Rain>) : TableDataSource<String, String, String, String> {

    val mItemsCache = WeakHashMap<Int, List<String>>()
    @SuppressLint("UseSparseArrays")
    val mChangedItems = SparseArray<SparseArray<String>>()
    override var rowsCount: Int = 0
    override var columnsCount: Int = 0
    override var firstHeaderData: String = getItemData(0,0)


    init {
        init()
    }

    override fun getRowHeaderData(index: Int): String {
        return getItemData(index, 0)
    }

    override fun getColumnHeaderData(index: Int): String {
        return getItemData(0, index)
    }

    override fun getItemData(rowIndex: Int, columnIndex: Int): String {
        try {
            val rowList = getRow(rowIndex)
            return rowList[columnIndex]
        } catch (e: Exception) {
            Log.e(TAG, "get rowIndex=$rowIndex; colIndex=$columnIndex;\ncache = ", e)
            return ""
        }

    }

    fun getRowValues(rowIndex: Int): List<String> {
        return getRow(rowIndex)
    }



     fun init() {
        //add header
        rowsCount = data.size
        columnsCount = 5


    }



     fun getRow(rowIndex: Int): List<String> {
        val result = ArrayList<String>()
        //convert data type วันที่/ ปริมาณน้ำฝน/ปริมาณน้ำฝนสะสม/ระดับความเสี่ยงต่อการเกิดดินถล่ม
         //result.addAll(data[rowIndex])

        if(rowIndex == 0) {
            val db =arrayOf("วันที่","ระดับความเสี่ยง", "น้ำฝนสะสม3วัน","ปริมาณน้ำฝน","ช่วงเวลา"  )
            result.addAll(db)

        }else{
            result.apply {
                var status =""
                add(Utils.getDateSlash(data[rowIndex].date))
                when(data[rowIndex].status){
                    LOW -> { status="ต่ำ"}
                    HIGH -> { status="สูง"}
                    NORMAL -> { status="ปานกลาง"}
                }
                add(status)
                add(data[rowIndex].previousRain.toInt().toString())
                add(data[rowIndex].currentRain.toInt().toString())
                add("07:00 ${Utils.getPreviusDate(data[rowIndex].date)} - 07:00 ${Utils.getDateSlash(data[rowIndex].date)}")
            }
        }


        d{"Check Data row$rowIndex  [$result]"}

        return result
    }

    companion object {
         val TAG = RainDataSource::class.java.simpleName
         val READ_FILE_LINES_LIMIT = 200
    }
}
