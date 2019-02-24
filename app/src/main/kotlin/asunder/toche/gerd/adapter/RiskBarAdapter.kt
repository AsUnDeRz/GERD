package adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Switch
import android.widget.TextView
import asunder.toche.gerd.Model
import asunder.toche.gerd.R
import asunder.toche.gerd.Utils
import java.util.*

/**
 *Created by ToCHe on 3/1/2018 AD.
 */
class RiskBarAdapter(var data : MutableList<Model.Rain>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    val HEADER_RISKBAR = 0
    val CONTENT_RISKBAR = 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is RiskHolder){
            holder.bind(data[position])
        }
    }

    //var data : MutableList<Model.Rain>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_riskbar,null)
        return   RiskHolder(view)

    }

    override fun getItemCount(): Int {
        return data.size
    }


    /*
    override fun getItemViewType(position: Int): Int {
        return when(position){
            0 -> HEADER_RISKBAR
            else -> CONTENT_RISKBAR
        }
    }
    */


    inner class RiskHolderTitle(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
    inner class RiskHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(rain:Model.Rain){
                val txtDate = itemView?.findViewById<TextView>(R.id.txtDate)
                val txtRisk = itemView?.findViewById<TextView>(R.id.txtRisk)
                val txtCRain = itemView?.findViewById<TextView>(R.id.txtCurrentRain)
                val txtRain3 = itemView?.findViewById<TextView>(R.id.txtRain3)

            var status =""
            when(rain.status){
                Model.StatusRain.LOW -> {
                    status="ต่ำ"
                    txtRisk?.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.green))
                }
                Model.StatusRain.HIGH -> {
                    status="สูง"
                    txtRisk?.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.red))
                }
                Model.StatusRain.NORMAL -> {
                    status="ปานกลาง"
                    txtRisk?.setBackgroundColor(ContextCompat.getColor(itemView.context,R.color.yellow))
                }
                else -> { txtRisk?.setBackgroundColor(Color.TRANSPARENT)}
            }

            txtDate?.text = Utils.getDateSlash(rain.date)
            txtRisk?.text = status
            txtCRain?.text = rain.currentRain.toString()
            txtRain3?.text = rain.previousRain.toString()


        }

    }
}