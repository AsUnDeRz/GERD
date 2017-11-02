package asunder.toche.gerd

import android.content.Context
import android.graphics.Color
import android.widget.TextView
import android.support.v4.graphics.ColorUtils
import android.graphics.drawable.GradientDrawable
import android.support.v4.content.ContextCompat
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.Glide
import android.text.TextUtils
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.request.target.Target
import com.cleveroad.adaptivetablelayout.LinkedAdaptiveTableAdapter
import com.cleveroad.adaptivetablelayout.ViewHolderImpl
import java.lang.Exception


/**
 * Created by ToCHe on 10/27/2017 AD.
 */
class SampleLinkedTableAdapter(var context: Context,var mTableDataSource: TableDataSource<String, String, String, String>) : LinkedAdaptiveTableAdapter<ViewHolderImpl>() {

    val mLayoutInflater: LayoutInflater = LayoutInflater.from(context)
    val mColumnWidth: Int
    val mRowHeight: Int
    val mHeaderHeight: Int
    val mHeaderWidth: Int

    init {
        val res = context.resources
        mColumnWidth = res.getDimensionPixelSize(R.dimen.column_width)
        mRowHeight = res.getDimensionPixelSize(R.dimen.row_height)
        mHeaderHeight = res.getDimensionPixelSize(R.dimen.column_header_height)
        mHeaderWidth = res.getDimensionPixelSize(R.dimen.row_header_width)
    }

    override fun getRowCount(): Int {
        return mTableDataSource.rowsCount
    }

    override fun getColumnCount(): Int {
        return mTableDataSource.columnsCount
    }

    override fun onCreateItemViewHolder(parent: ViewGroup): ViewHolderImpl {
        return TestViewHolder(mLayoutInflater.inflate(R.layout.item_card, parent, false))
    }

    override fun onCreateColumnHeaderViewHolder(parent: ViewGroup): ViewHolderImpl {
        return TestHeaderColumnViewHolder(mLayoutInflater.inflate(R.layout.item_header_column, parent, false))
    }

    override fun onCreateRowHeaderViewHolder(parent: ViewGroup): ViewHolderImpl {
        return TestHeaderRowViewHolder(mLayoutInflater.inflate(R.layout.item_header_row, parent, false))
    }

    override fun onCreateLeftTopHeaderViewHolder(parent: ViewGroup): ViewHolderImpl {
        return TestHeaderLeftTopViewHolder(mLayoutInflater.inflate(R.layout.item_header_left_top, parent, false))
    }

    override fun onBindViewHolder(viewHolder: ViewHolderImpl, row: Int, column: Int) {
        val vh = viewHolder as TestViewHolder
        var itemData = mTableDataSource.getItemData(row, column) // skip headers

        if (TextUtils.isEmpty(itemData)) {
            itemData = ""
        }

        when(itemData){
            "ต่ำ" -> { vh.tvText.setBackgroundColor(ContextCompat.getColor(context,R.color.green))}
            "สูง" -> { vh.tvText.setBackgroundColor(ContextCompat.getColor(context,R.color.red))}
            "ปานกลาง" -> {vh.tvText.setBackgroundColor(ContextCompat.getColor(context,R.color.yellow))}
            else -> {vh.tvText.setBackgroundColor(Color.TRANSPARENT)}
        }

        itemData = itemData.trim({ it <= ' ' })
        vh.tvText.visibility = View.VISIBLE
        vh.ivImage.setVisibility(View.VISIBLE)
        vh.tvText.setText(itemData)
        Glide.with(vh.ivImage.getContext())
                .load(itemData)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(object : RequestListener<String, GlideDrawable> {
                    override fun onException(e: Exception?, model: String?, target: Target<GlideDrawable>?, isFirstResource: Boolean): Boolean {
                        vh.ivImage.setVisibility(View.INVISIBLE)
                        vh.tvText.visibility = View.VISIBLE
                        return false
                    }

                    override fun onResourceReady(resource: GlideDrawable?, model: String?, target: Target<GlideDrawable>?, isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
                        vh.ivImage.setVisibility(View.VISIBLE)
                        vh.tvText.visibility = View.INVISIBLE
                        return false
                    }

                })
                .into(vh.ivImage)
    }

    override fun onBindHeaderColumnViewHolder(viewHolder: ViewHolderImpl, column: Int) {
        val vh = viewHolder as TestHeaderColumnViewHolder

        vh.tvText.setText(mTableDataSource.getColumnHeaderData(column))  // skip left top header
        val color = COLORS[column % COLORS.size]

        val gd = GradientDrawable(
                if (mIsRtl) GradientDrawable.Orientation.RIGHT_LEFT else GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(ColorUtils.setAlphaComponent(color, 50), 0x00000000))
        gd.cornerRadius = 0f
        //vh.vGradient.setBackground(gd)
        //vh.vLine.setBackgroundColor(color)
    }

    override fun onBindHeaderRowViewHolder(viewHolder: ViewHolderImpl, row: Int) {
        val vh = viewHolder as TestHeaderRowViewHolder
        vh.tvText.setText(mTableDataSource.getItemData(row, 0))
    }

    override fun onBindLeftTopHeaderViewHolder(viewHolder: ViewHolderImpl) {
        val vh = viewHolder as TestHeaderLeftTopViewHolder
        vh.tvText.setText(mTableDataSource.firstHeaderData)
    }

    override fun getColumnWidth(column: Int): Int {
        return mColumnWidth
    }

    override fun getHeaderColumnHeight(): Int {
        return mHeaderHeight
    }

    override fun getRowHeight(row: Int): Int {
        return mRowHeight
    }

    override fun getHeaderRowWidth(): Int {
        return mHeaderWidth
    }

    //------------------------------------- view holders ------------------------------------------

    class TestViewHolder (itemView: View) : ViewHolderImpl(itemView) {
        var tvText: TextView
        var ivImage: ImageView


        init {
            tvText = itemView.findViewById(R.id.tvText)
            ivImage = itemView.findViewById(R.id.ivImage) as ImageView
        }
    }

     class TestHeaderColumnViewHolder (itemView: View) : ViewHolderImpl(itemView) {
         var tvText: TextView
         //var vGradient: View
         var vLine: View

        init {
            tvText = itemView.findViewById(R.id.tvText)
            //vGradient = itemView.findViewById(R.id.vGradient)
            vLine = itemView.findViewById(R.id.vLine)
        }
    }

     class TestHeaderRowViewHolder(itemView: View) : ViewHolderImpl(itemView) {
         var tvText: TextView

        init {
            tvText = itemView.findViewById(R.id.tvText)
        }
    }

    class TestHeaderLeftTopViewHolder (itemView: View) : ViewHolderImpl(itemView) {
         var tvText: TextView

        init {
            tvText = itemView.findViewById(R.id.tvText)
        }
    }

    companion object {
        private val COLORS = intArrayOf(-0x19d5f0, -0x16e19d, -0x63d850, -0x98c549, -0xc0ae4b, -0xa98804, -0xfc560c, -0xff432c, -0xff6978, -0xda64dc, -0x743cb6, -0x3223c7, -0x14c5, -0x3ef9, -0x6800, -0xa8de)
    }
}