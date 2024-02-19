package com.armada.storeapp.ui.home.riva.riva_look_book.select_country.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.armada.storeapp.R
import com.armada.storeapp.data.model.StoreData
import com.armada.storeapp.data.model.response.CountryStoreResponse
import com.armada.storeapp.databinding.LvSelectStoreOnStartBinding
import com.armada.storeapp.ui.MainActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.RivaLookBookActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.riva_login.RivaLoginActivity
import com.armada.storeapp.ui.home.riva.riva_look_book.select_country.SelectCountryActivity
import com.armada.storeapp.ui.utils.SharedpreferenceHandler

class SelectStoreAdapter(
    private val activity: SelectCountryActivity,
    private val storeList: ArrayList<CountryStoreResponse.StoreDetail>
) : RecyclerView.Adapter<SelectStoreAdapter.MyViewHolder>() {

    private var screenWidth: Int = 0
    private var screenHeight: Int = 0
    private var productWidth: Int = 0
    private var productheight: Int = 0
    private var arrListImage: ArrayList<ImageView> = ArrayList()


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectStoreAdapter.MyViewHolder {
        return MyViewHolder(
            LvSelectStoreOnStartBinding.inflate(
                LayoutInflater.from(activity),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SelectStoreAdapter.MyViewHolder, position: Int) {

        val metrics = activity.resources.displayMetrics

        holder.binding.txtCountryName.text = storeList[position].name

        holder.binding.root.id = position

        holder.binding.root.setOnClickListener {
            val sharedpreferenceHandler = SharedpreferenceHandler(activity)
            sharedpreferenceHandler.saveData(
                SharedpreferenceHandler.RIVA_SELECTED_COUNTRY,
                storeList.get(position).store_code_en
            )
            sharedpreferenceHandler.saveData(
                SharedpreferenceHandler.RIVA_SELECTED_CURRENCY,
                storeList.get(position).currency_code
            )

            activity?.clearDatabase()

            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
            activity.finish()
        }

        when (storeList[position].code) {
            "base" -> {
                holder.binding.imgFlag.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        R.drawable.international
                    )
                )
            }
            "bahrain" -> {
                holder.binding.imgFlag.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        R.drawable.bahrain
                    )
                )
            }
            "qatar" -> {
                holder.binding.imgFlag.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        R.drawable.qatar
                    )
                )
            }
            "ksa" -> {
                holder.binding.imgFlag.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        R.drawable.ksa
                    )
                )
            }
            "uae" -> {
                holder.binding.imgFlag.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        R.drawable.uae
                    )
                )
            }
            "kuwait" -> {
                holder.binding.imgFlag.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        R.drawable.kuwait
                    )
                )
            }
            "oman" -> {
                holder.binding.imgFlag.setImageDrawable(
                    ContextCompat.getDrawable(
                        activity,
                        R.drawable.oman
                    )
                )
            }
        }

        screenWidth = (metrics.widthPixels)
        productWidth = screenWidth
        productheight = ((productWidth / 1.75).toInt())
        screenHeight = productheight / 2

        val smallParam =
            RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, productheight)
        smallParam.addRule(RelativeLayout.CENTER_HORIZONTAL)
    }

    override fun getItemCount(): Int {
        return storeList.size
    }

    class MyViewHolder(val binding: LvSelectStoreOnStartBinding) :
        RecyclerView.ViewHolder(binding.root)
}
