package com.armada.riva.Category.Algolia

/**
 * Created by User999 on 7/30/2018.
 */
import java.io.Serializable

/**
 * Created by User999 on 5/29/2018.
 */

class FirebaseCategoryModel : Serializable {

    var category_id: String? = null
    var category_name: String? = null
    var views: Int = 0


    constructor() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    constructor(category_id: String, category_name: String, views: Int) {
        this.category_id = category_id
        this.category_name = category_name
        this.views = views
    }

}