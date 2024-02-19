package com.armada.storeapp.data.model.response

import java.io.Serializable

//data class CollectionListItemModel(
//    var id: Int? = null,
//    var title: String? = null,
//    var name: String? = null,
//    var sub_title: String? = null,
//    var image: String? = null,
//    var type: String? = null,
//    var type_id: String? = null,
//    var path: String? = null,
//    var has_banner: String? = null,
//    var banner: String? = null,
//    var lvl: String? = null,
//    var media_file: String? = null,
//    var media_type: String? = null,
//    var media_thumbnail: String? = null,
//    var regular_price: String? = null,
//    var final_price: String? = null,
//    var has_term: Int? = 0,
//    var term_cms_id: String? = null,
//    var category_id: Int? = 0,
//    var category_path: String? = null,
//    var category_level: String? = null,
//    var has_pattern: Int? = 0,
//    var pattern: ArrayList<PatternsProduct?>? = null,
//    var has_subcategory: Int? = 0,
//    var subcategory_option: String? = null,
//    var subcategories: ArrayList<CollectionListItemModel>? = null,
//    var has_banners: Int? = 0,
//    var banners: ArrayList<String>? = null,
//    var has_collection_groups: Int? = 0,  ///For Sub collection,
//    var stopVideo: Int? = 0,
//) : Serializable

data class CollectionListItemModel(
    var image: String? = null,
    var sort_type: String? = null,
    var sub_title: String? = null,
    var id: Int? = null,
    var title: String? = null,
    var type: String? = null,
    var type_id:String?=null,
    var path :String?=null,
    var lvl:String?=null,
    var has_banner:String?=null,
    var banner:String?=null,
    var media_file:String?=null,
    var media_type:String?=null,
    var media_thumbnail:String?=null,
    var regular_price:String?=null,
    var final_price:String?=null,
    var name:String?=null,
    var category_id : Int?=0,
    var category_path:String?=null,
    var category_level:String?=null,
    var has_pattern : Int? = 0,
    var has_term:Int? = 0,
    var term_cms_id:String?=null,
    var pattern :ArrayList<PatternsProduct?>?=null,
    var has_subcategory : Int? = 0,
    var subcategory_option :String?=null,
    var subcategories : ArrayList<CollectionListItemModel?>?=null,
    var has_banners:Int? = 0,           /////////////////////////////////  use in category and extended category
    var banners:ArrayList<String>?=null, //////////////////////////////////
    var has_collection_groups :Int? = 0,  ///For Sub collection
    var stopVideo:Int? = 0,

):Serializable
//var collection_time_period:Int?=0,
//var timeline_collection_list :ArrayList<TimeLineModel?>?=null,
//var should_reverse:Int? = null
//_______________________________________
//var sort_type: String? = null,
//var collection_time_period: Int? = 0,
//var timeline_collection_list: ArrayList<TimeLineModel?>? = null,
//var should_reverse: Int? = null

data class PatternsProduct(
    val id: Int? = 0,
    var pattern_number: Int? = 0,
    val product_id: Int? = 0,
    val name: String? = "",
    val price: Double? = 0.0,
    val image: String? = "",
    val height: String? = "",
    val show_container_grid: Int? = 0,
    val container_width: String? = "",
    val media_type: String? = "",
    val media_file: String? = ""
) : Serializable