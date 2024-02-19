package com.armada.storeapp.data.model.response

import java.io.Serializable

data class TimeLineModel(
    val id: Int,
    val image: String,
    var media_file: String,
    val media_thumbnail: String,
    var media_type: String,
    val products: ArrayList<TimeLineProduct>,
    val title: String,
    var duration: Long,
    var video_time_period: Long,
    var arrListDuration: ArrayList<Long>
) : Serializable

data class TimeLineProduct(
    val final_price: String,
    val image: String,
    val name: String,
    val product_id: Int,
    val regular_price: String,
    var is_wishList: Boolean

) : Serializable