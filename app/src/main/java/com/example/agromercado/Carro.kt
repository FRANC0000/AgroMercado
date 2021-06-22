package com.example.agromercado

import com.google.firebase.firestore.Exclude
import java.io.Serializable

class Carro (idproducto: String? = null,
             val nombreProducto: String? = null,
             val precioProducto: Int? = null,
             val cantidad: Int? = null,
             val subtotal: Int? = null): Serializable {

    @Exclude
    @set:Exclude
    @get:Exclude
    var id: String? = idproducto

    constructor() : this(null, null, null, null, null)
}