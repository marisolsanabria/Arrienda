package com.example.arrrienda3.models

import android.graphics.Bitmap
import com.google.firebase.database.Exclude
import java.util.*
import kotlin.collections.ArrayList

class Post {
    private var postDescription:String=""
    private var date:Date= Date()
    private var title:String=""
    private var listFotos: ArrayList<String> = arrayListOf()
    private var userId:String=""
    private var video:String=""
    private var backGround:String=""
    //vamos a ponerle el id
    //para decirle a firebase que no quiero que me incluya esto porque cuando creemos un nuevo post intentará crear un nuevo uid si no ponemos lo siguiente

   @Exclude
    @set:Exclude
    @get:Exclude
    var uid:String?=null


    //un constructor vacio que requiere firebase para darnos la info

   constructor()
    constructor(postDescription:String,date:Date,title: String, listFotos: ArrayList<String>,userId:String, video:String, background: String){
       this.postDescription=postDescription
        this.date=date
        this.title=title
        this.listFotos=listFotos
        this.userId=userId
        this.video=video
        this.backGround=backGround
   }

    fun getPostDescription():String{
        return postDescription
    }
    fun setPostDescription(postDescription: String) {
        this.postDescription = postDescription
    }


    fun getDate():Date{
        return date
    }
    fun setDate(date: Date) {
        this.date=date
    }

    fun getTitle():String{
        return title
    }
    fun setTitle(title: String) {
        this.title=title
    }

    fun getListFotos():ArrayList<String>{
        return listFotos
    }
    fun setListFotos(listFotos: ArrayList<String>) {
        this.listFotos=listFotos
    }

    fun getUserId():String{
        return userId
    }
    fun setUserId(userId: String) {
        this.userId=userId
    }

    fun getVideo():String{
        return video
    }
    fun setVideo(video: String) {
        this.video=video
    }

    fun getBackground():String{
        return backGround
    }
    fun setBackgrund(video: String) {
        this.backGround=backGround
    }




}