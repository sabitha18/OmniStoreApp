package com.armada.storeapp.ui.home.riva.riva_look_book.products.product_details

import android.text.Editable
import android.text.Html
import org.xml.sax.XMLReader

///html tag handler
open class MyTagHandler : Html.TagHandler {
    var first = true
    var parent: String? = null
    var index = 1

    override fun handleTag(
        opening: Boolean,
        tag: String,
        output: Editable,
        xmlReader: XMLReader
    ) {

        if (tag == "ul")
            parent = "ul"
        else if (tag == "ol") parent = "ol"
        if (tag == "li" || tag == "&nbsp") {
            if (parent == "ul") {
                if (first) {
                    output.append("\n\t")
                    first = false
                } else {
                    first = true
                }
            } else {
                if (first) {
                    output.append("\n\t" + index + ". ")
                    first = false
                    index++
                } else {
                    first = true
                }
            }
        }
    }
}