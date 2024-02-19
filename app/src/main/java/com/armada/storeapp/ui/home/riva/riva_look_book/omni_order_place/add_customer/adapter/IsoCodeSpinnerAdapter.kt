package com.armada.storeapp.ui.home.riva.riva_look_book.omni_order_place.add_customer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.armada.storeapp.R
import com.armada.storeapp.data.model.IsoCode
import com.armada.storeapp.data.model.response.GetDestinationBinResponse
import com.armada.storeapp.data.model.response.GetStateResponse
import com.armada.storeapp.data.model.response.StateMaster

class IsoCodeSpinnerAdapter(
    ctx: Context,
    spinnerList: ArrayList<IsoCode>
) : ArrayAdapter<IsoCode>(
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
        val isoCode = getItem(position)

        val view = recycledView ?: LayoutInflater.from(context).inflate(
            R.layout.item_spinner,
            parent,
            false
        )

        isoCode?.let {
            val textView = view.findViewById(R.id.tv_spinner_text) as TextView
            textView.text = it.countryCode + " ${it.isoCode}"
        }
        return view
    }
}