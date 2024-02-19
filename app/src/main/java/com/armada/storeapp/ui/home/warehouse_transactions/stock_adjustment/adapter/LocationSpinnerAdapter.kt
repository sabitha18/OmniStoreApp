package com.armada.storeapp.ui.home.warehouse_transactions.stock_adjustment.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.armada.storeapp.R
import com.armada.storeapp.data.model.response.FromLocationListResponse

class LocationSpinnerAdapter(
    ctx: Context,
    spinnerList: ArrayList<FromLocationListResponse.FromLocationListResponseItem>
) : ArrayAdapter<FromLocationListResponse.FromLocationListResponseItem>(ctx, 0, spinnerList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent);
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent);
    }

    fun createItemView(position: Int, recycledView: View?, parent: ViewGroup): View {
        val sort = getItem(position)

        val view = recycledView ?: LayoutInflater.from(context).inflate(
            R.layout.item_spinner,
            parent,
            false
        )

        sort?.let {
            val textSort = view.findViewById(R.id.tv_spinner_text) as TextView
            textSort.text = it.LOCNAME
        }
        return view
    }
}