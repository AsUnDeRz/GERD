package asunder.toche.gerd


/**
 * Created by ToCHe on 10/27/2017 AD.
 */
interface TableDataSource<out TFirstHeaderDataType, out TRowHeaderDataType, out TColumnHeaderDataType, out TItemDataType> {

    val rowsCount: Int

    val columnsCount: Int

    val firstHeaderData: TFirstHeaderDataType

    fun getRowHeaderData(index: Int): TRowHeaderDataType

    fun getColumnHeaderData(index: Int): TColumnHeaderDataType

    fun getItemData(rowIndex: Int, columnIndex: Int): TItemDataType

}