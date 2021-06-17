package com.example.agromercado

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_edit_product.*
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.activity_product_detail.contactoProductor_et
import kotlinx.android.synthetic.main.activity_product_detail.emailProductor_et
import kotlinx.android.synthetic.main.activity_product_detail.fecha_et
import kotlinx.android.synthetic.main.activity_product_detail.nameProductor_et

class EditProductActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        //setup
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val idprod = bundle?.getString("idprod")

        var fecha2 = ""
        var nameproductor2 = ""
        var email2 = ""
        var contacto2 = 1



        db.collection("Productos")
            .document(idprod?:"")
            .get()
            .addOnSuccessListener {

                editnombreproducto_et.setText(it.get("nombreProducto").toString()) //editable
                fecha_et.setText(it.get("fecha").toString())
                fecha2 = it.get("fecha").toString()

                nameProductor_et.setText(it.get("nameProductor").toString())
                nameproductor2 = it.get("nameProductor").toString()

                editarprecio_et.setText(it.get("precio").toString()) //editable
                editarcategoria_et.setText(it.get("categoria").toString()) //editable
                editardescripcion_et.setText(it.get("descripcion").toString()) //editable
                editarcantidad_et.setText(it.get("cantidad").toString()) //editable
                emailProductor_et.setText(it.get("emailProductor").toString())
                email2 = it.get("emailProductor").toString()

                editarunidadMedida_et.setText(it.get("unidadMedida").toString()) //editable
                contactoProductor_et.setText(it.get("contactoProductor").toString())
                contacto2 = it.get("contactoProductor").toString().toInt()

            }

        saveEditProductBtn.setOnClickListener {
            db.collection("Productos")
                .document(idprod?:"")
                .set(
                    hashMapOf(
                        "nombreProducto" to editnombreproducto_et.text.toString(),
                        "fecha" to fecha2,
                        "nameProductor" to nameproductor2,
                        "precio" to editarprecio_et.text.toString().toInt(),
                        "categoria" to editarcategoria_et.text.toString(),
                        "descripcion" to editardescripcion_et.text.toString(),
                        "cantidad" to editarcantidad_et.text.toString().toInt(),
                        "emailProductor" to email2,
                        "unidadMedida" to editarunidadMedida_et.text.toString(),
                        "contactoProductor" to contacto2
                    )
                ).addOnSuccessListener {
                    val intent = Intent(this, ProductDetailActivity::class.java).apply {
                        putExtra("email", email)
                        putExtra("idprod", idprod)
                    }
                    startActivity(intent)
                    finish()
                }
        }
    }
}