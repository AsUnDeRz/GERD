package asunder.toche.gerd

import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by ToCHe on 10/21/2017 AD.
 */
object Model {


    enum class StatusRain{
        HIGH,NORMAL,LOW
    }

    data class Eq(val redM:Float,var redC:Float,var yellowM:Float,var yellowC:Float)
    data class valRain(val x:Float,val y:Float)

    // x = previous  y = current
    data class Rain(var id:Int,var date: Date,var currentRain:Float,var previousRain:Float,var status:StatusRain){
        constructor() : this(0,Date(),0.0f,0.0f,StatusRain.LOW)
    }


}