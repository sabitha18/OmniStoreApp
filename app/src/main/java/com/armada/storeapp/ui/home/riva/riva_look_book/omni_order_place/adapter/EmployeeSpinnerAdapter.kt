package com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.CityMaster
import com.armada.storeapp.data.model.response.EmployeeMaster
import com.armada.storeapp.data.model.response.GetDestinationBinResponse

class EmployeeSpinnerAdapter(
    ctx: Context,
    spinnerList: ArrayList<EmployeeMaster>
) : ArrayAdapter<EmployeeMaster>(
    ctx,
    0,
    spinnerList
) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent);
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent);
    }

    fun createItemView(position: Int, recycledView: View?, parent: ViewGroup): View {
        val employeeMaster = getItem(position)

        val view = recycledView ?: LayoutInflater.from(context).inflate(
            R.layout.item_spinner,
            parent,
            false
        )

        employeeMaster?.let {
            val textView = view.findViewById(R.id.tv_spinner_text) as TextView
            textView.text = it.empcodename
        }
        return view
    }
}