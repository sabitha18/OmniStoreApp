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
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.CountryListCityModel
import com.armada.storeapp.ui.utils.Utils
import java.util.*

class CitySpinnerAdapter(
    private val activity: Activity,
    textViewResourceId: Int,
    private val data: ArrayList<CountryListCityModel>,
    var res: Resources
) : ArrayAdapter<CountryListCityModel>(activity, textViewResourceId, data) {
    internal var font: Typeface? = null
    internal var tempValues: String? = null
    internal var inflater: LayoutInflater
    internal var locale: Locale? = null


    init {
        /***********  Layout inflator to call external xml layout ()  */
        inflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

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
        tempValues = Utils.getDynamicStringFromApi(activity, data[position].name)

        val label = row.findViewById<View>(R.id.txtItem) as TextView
        val relbg = row.findViewById<View>(R.id.relSpinBg) as RelativeLayout
        //relbg.setBackgroundColor(Color.parseColor("#ffffff"));
        label.text = tempValues

        if (position == 0)
            label.setTextColor(activity.resources.getColor(R.color.stroke_color))
        else label.setTextColor(activity.resources.getColor(R.color.black))

        return row
    }


}