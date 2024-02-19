package com.armada.storeapp.ui.home.riva.riva_look_book.checkout.address

import java.text.SimpleDateFormat
import java.util.*

object DateFormatter {


    fun formatDateToEpoch(
    ): Long {
        var timeZone = "GMT+5.30"
        val sdf = SimpleDateFormat("dd MMM yyyy hh:mm:ss a")
        if (timeZone.isNullOrEmpty())
            timeZone = Calendar.getInstance().getTimeZone().getID()
        sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
        val currentDate=Date()
        val parsedDateString=sdf.format(currentDate).replace("+05.30","")
        val parsedDate=sdf.parse(parsedDateString)
        return parsedDate.time
    }
//    Date date = new Date();
//    System.out .println("Default Date:"+date.toString());
//    System.out .println("System Date: "+formatDateToString(date, "dd MMM yyyy hh:mm:ss a", null));
//    System.out .println("System Date in PST: "+formatDateToString(date, "dd MMM yyyy hh:mm:ss a", "PST"));
//    System.out .println("System Date in IST: "+formatDateToString(date, "dd MMM yyyy hh:mm:ss a", "IST"));
//    System.out .println("System Date in GMT: "+formatDateToString(date, "dd MMM yyyy hh:mm:ss a", "GMT"));

}