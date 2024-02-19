package com.armada.storeapp.ui.omni_orders.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.GetDestinationBinResponse

class StatusSpinnerAdapter(
    ctx: Context,
    spinnerList: ArrayList<String>
) : ArrayAdapter<String>(
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
        val status = getItem(position)

        val view = recycledView ?: LayoutInflater.from(context).inflate(
            R.layout.item_spinner,
            parent,
            false
        )

        status?.let {
            val textBin = view.findViewById(R.id.tv_spinner_text) as TextView
            textBin.text = it
        }
        return view
    }
}