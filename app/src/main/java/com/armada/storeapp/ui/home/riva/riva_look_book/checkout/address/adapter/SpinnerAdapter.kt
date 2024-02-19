package com.armada.storeapp.ui.home.riva.riva_look_book.checkout.address.adapter


import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.CountryListDataModel
import com.armada.storeapp.ui.utils.Utils
import com.armada.storeapp.ui.utils.Utils.getDynamicStringFromApi
import java.util.*


/********* Adapter class extends with BaseAdapter and implements with OnClickListener  */
class SpinnerAdapter
/*************  CustomAdapter Constructor  */
    (
    private val activity: Activity,
    textViewResourceId: Int,
    private val data: ArrayList<CountryListDataModel>,
    var res: Resources


) : ArrayAdapter<CountryListDataModel>(
    activity,
    textViewResourceId,
    data
) {

    internal var font: Typeface? = null
    private var tempValues: String? = null
    internal var inflater: LayoutInflater =
        activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    internal var locale: Locale? = null
    internal var typeSemiBold: Typeface? = null
    internal var typeNormal: Typeface? = null

    init {
        /***********  Layout inflater to call external xml layout ()  */
    }

    /********** Take passed values  */

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)

    }

    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        /********** Inflate spinner_rows.xml file for each row ( Defined below )  */
        val row = inflater.inflate(R.layout.spinner_items, parent, false)


        /***** Get each Model object from Arraylist  */
        tempValues = null
        tempValues = Utils.getDynamicStringFromApi(activity, data[position].full_name)


        val label = row.findViewById<View>(R.id.txtItem) as TextView
        label.text = tempValues

        if (position == 0)
            label.setTextColor(ContextCompat.getColor(activity, R.color.black))
        else label.setTextColor(ContextCompat.getColor(activity, R.color.black))

        return row
    }
}
