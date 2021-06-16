package com.example.agromercado

import com.google.firebase.firestore.Exclude

class Product(val nombreProducto: String? = null,
              val fecha: String? = null,
              val nameProductor: String? = null, //se saca de Users
              val precio: Int? = null,
              val categoria: String? = null,
              val descripcion: String? = null,
              val cantidad: Int? = null,
              val emailProductor : String? = null,
              val unidadMedida : String? = null,
              val contactoProductor : Int? = null) { //se saca de Users

    @Exclude
    @set:Exclude
    @get:Exclude
    var uid: String? = null

    constructor() : this (null, null, null, null, null, null, null, null, null, null)
}