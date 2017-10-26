package adapter

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import asunder.toche.gerd.Model
import asunder.toche.gerd.R
import asunder.toche.gerd.Utils
import com.github.ajalt.timberkt.d
import java.util.*


/**
 * Created by ToCHe on 10/26/2017 AD.
 */
class RainAdapter(var data:MutableList<Model.Rain>) : RecyclerView.Adapter<RainAdapter.RainHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RainHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.item_rain,null)
        return RainHolder(view)
    }

    override fun onBindViewHolder(holder: RainHolder?, position: Int) {
        holder?.bind(holder.adapterPosition)
    }

    override fun getItemCount(): Int {
        return data.size
    }


    inner class RainHolder(itemView: View?) : RecyclerView.ViewHolder(itemView){
        fun bind(position: Int){
            var rain = data[position]
            val txtDate = itemView?.findViewById<TextView>(R.id.txt_date)
            val edtRain = itemView?.findViewById<EditText>(R.id.edt_rain)

            txtDate?.text = Utils.getDateSlash(rain.date)
            edtRain?.setText(rain.currentRain.toString())

            edtRain?.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
                if(hasFocus){
                    if(edtRain?.text.toString() == "0.0"){
                        edtRain?.text?.clear()
                    }
                }else{
                    if(edtRain?.text.toString() == ""){
                        data[adapterPosition].currentRain = 0.0f
                        d{"onTextChanged currentRain position[$position] = 0.0"}
                    }
                }
            }


            edtRain?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(!s.isNullOrEmpty() && !s.isNullOrBlank()){
                        data[adapterPosition].currentRain = s.toString().toFloat()
                        d{"onTextChanged currentRain position[$position] = $s"}
                    }else{
                        data[adapterPosition].currentRain = 0.0f
                        d{"onTextChanged currentRain position[$position] = $s"}
                    }
                }
            })

        }

    }
}