package com.nusantarian.donasi.util

class Helper {
    //to change substring color and type to bold
    fun getColoredSpanned(text: String, color: String): String =
        "<font color = $color><b>$text</b></font>"
}