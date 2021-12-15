package com.example.arrrienda3.models

class User {
    private var uid:String=""
    private var name:String=""
    private var id:String=""
    private var email:String=""
    private var phone:String=""
    private var ubication:String=""
    private var photo:String=""
    private var bio:String=""

    constructor()
    constructor(uid: String, name: String, id: String, email: String, phone: String, ubication: String, photo: String, bio: String){
        this.uid=uid
        this.name=name
        this.id=id
        this.email=email
        this.phone=phone
        this.ubication=ubication
        this.photo=photo
        this.bio=bio
    }

    fun getName():String{
        return name
    }
    fun setName(name: String){
        this.name=name
    }

    fun getid():String{
        return id
    }
    fun setid(id: String){
        this.id=id
    }

    fun getemail():String{
        return email
    }
    fun setemail(email: String){
        this.email=email
    }

    fun getphone():String{
        return phone
    }
    fun setphone(phone: String){
        this.phone=phone
    }


    fun getubication():String{
        return ubication
    }
    fun setubication(ubication: String){
        this.ubication=ubication
    }



    fun getPhoto():String{
        return photo
    }
    fun setPhoto(photo: String){
        this.photo=photo
    }




    fun getBio():String{
        return bio
    }
    fun setBio(bio: String){
        this.bio=bio
    }




    fun getuid():String{
        return uid
    }
    fun setuid(uid: String){
        this.uid=uid
    }


}