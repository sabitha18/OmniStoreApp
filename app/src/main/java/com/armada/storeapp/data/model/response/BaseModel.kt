package com.armada.storeapp.data.model.response

/**
 * Created by Leza Solutions on 15-10-2015.
 */
import java.io.Serializable


class BaseModel : Serializable {
    var id: String = ""
    var latitude: String = ""
    var longitude: String = ""
    var image: String = ""
    var status: String = ""
    var value: String = ""
    var price: String = ""
    var url: String = ""
    var link: String = ""
    var type: String = ""
    var placeID: String = ""
    var name: String = ""
    var from: String = ""
    var phone: String = ""
    var location: String = ""
    var logo: String = ""
    var block_no: String = ""
    var description: String= ""
    var show: String= ""
    var facebook: String= ""
    var instagram: String= ""
    var `package`: Int = 0
    var street: String= ""
    var apartment: String= ""
    var unit: String= ""
    var floor: String= ""
    var area = ""
    var governorate = ""
    var set: String= ""
    var note: String= ""
    var building: String= ""
    var email: String= ""
    var address= ""
    var country = ""
    var postalcode = ""
    var countryCode="" //short_name , likely to be store code
    var city=""


}
