package com.example.arrrienda3.models

import android.net.Uri

object Constants {
    var constants: Constants? = null
    fun shared(): Constants? {
        if (constants == null) {
            constants = Constants
        }
        return constants
    }

    var imageUri : ArrayList<String>? = null
    var postImagenes= listOf<String>()
    var postId:String?=null
    var videoUrl: String? = ""
    var videoURI: Uri?=null
    var backgraoundUri:String?=null
}