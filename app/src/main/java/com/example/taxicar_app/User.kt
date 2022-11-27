package com.example.taxicar_app

data class User(
    var name: String,
    var email: String,
    var uId: String
){
    constructor(): this("","","")
}
