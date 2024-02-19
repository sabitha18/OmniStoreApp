package com.armada.storeapp.data.model.response

data class SubCategoryResponse(
    val `data`: Data?,
    val message: String?,
    val status: Int?,
    val success: Boolean?
) {
    data class Data(
        val collectionGroups: ArrayList<CollectionGroupsItemModel>?,
        val get_all_category: String?
    ) {
//        data class CollectionGroup(
//            val collection_list: ArrayList<Collection>?,
//            val hide_collection_sub_title: Int?,
//            val hide_collection_title: Int?,
//            val hide_title: Int?,
//            val hide_underline: Int?,
//            val id: Int?,
//            val image_height: Int?,
//            val image_margin: Int?,
//            val image_width: Int?,
//            val margin_bottom: Int?,
//            val margin_top: Int?,
//            val should_reverse: Int?,
//            val title: String?
//        ) {
//            data class Collection(
//                val banners: List<Any?>?,
//                val category_id: Int?,
//                val category_level: String?,
//                val category_path: String?,
//                val has_banners: Int?,
//                val has_collection_groups: Int?,
//                val has_pattern: Int?,
//                val has_subcategory: Int?,
//                val id: Int?,
//                val image: String?,
//                val media_file: String?,
//                val media_thumbnail: String?,
//                val media_type: String?,
//                val name: String?,
//                val pattern: List<Any?>?,
//                val sub_title: String?,
//                val subcategories: List<Any?>?,
//                val subcategory_option: String?
//            )
//        }
    }
}