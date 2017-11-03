package adapter

import android.content.Context
import android.content.DialogInterface
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import asunder.toche.gerd.*
import com.afollestad.materialdialogs.MaterialDialog
import com.github.ajalt.timberkt.d
import com.github.ybq.android.spinkit.style.FadingCircle
import java.util.*


/**
 * Created by ToCHe on 10/26/2017 AD.
 */
class RainAdapter(var data:MutableList<Model.Rain>,var limit:String) : RecyclerView.Adapter<RainAdapter.RainHolder>(){


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

            if(adapterPosition == data.size-1){
                edtRain?.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        val imm = edtRain.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(edtRain.windowToken, 0)
                        //Snackbar.make(edtRain,"DONE",Snackbar.LENGTH_LONG).show()
                        DialogSave(edtRain.context)
                        return@OnEditorActionListener true
                    }
                    false
                })
            }

        }

    }

    fun DialogSave(context: Context){
        MaterialDialog.Builder(context)
                .content("คุณต้องการบันทึกข้อมูลปริมาณน้ำฝนย้อนหลังหรือไม่")
                .positiveText("ตกลง")
                .negativeText("ยกเลิก")
                .onPositive { dialog, which ->
                    //save data
                    val appDb = AppDatabase(context)
                    if(appDb.getRainList(limit).size > 0) {
                        d{" Compare for update or add Rain data"}
                        Utils.compareData(AppDatabase(context), data, limit)
                    }else{
                        d { "add new Rain data 1st" }
                        data.forEach {
                            appDb.addRain(it.currentRain,it.date.time)
                        }
                    }
                    d{"Check size after update =${appDb.getRainList(limit).size}"}
                    RainFragment2.proLoad.visibility = View.VISIBLE
                    RainFragment1.loadChart(context)
                    Handler().postDelayed({
                        dialog.dismiss()
                        ActivityRain.viewPager.setCurrentItem(0,true)
                        RainFragment2.proLoad.visibility = View.GONE
                    },3000)
                }
                .show()
    }
}