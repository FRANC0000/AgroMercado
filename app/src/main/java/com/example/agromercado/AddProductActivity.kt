package com.example.agromercado

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.activity_product_detail.*
import kotlinx.android.synthetic.main.activity_products.*
import java.text.SimpleDateFormat
import java.util.*

class AddProductActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    var emailM = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)

        //setup
        val bundle = intent.extras
        val email = bundle?.getString("email")
        emailM = email?:""


        newProductBtn.setOnClickListener {

            if(nombreprod_tv.text.isEmpty() ||
                precioprod_tv.text.isEmpty() ||
                categoriaprod_tv.text.isEmpty() ||
                descrprod_tv.text.isEmpty() ||
                cantprod_tv.text.isEmpty() ||
                unimed_tv.text.isEmpty()) {

                    AlertDialog.Builder(this).apply {
                        setTitle("Error")
                        setMessage("Los campos no pueden estar vacíos")
                        setPositiveButton("Aceptar", null)
                    }.show()
                }
            else {

                val nombreproduct = nombreprod_tv.text.toString()
                val precio = precioprod_tv.text.toString().toInt()
                val categoria = categoriaprod_tv.text.toString()
                val desc = descrprod_tv.text.toString()
                val cantidad = cantprod_tv.text.toString().toInt()
                val unidadMedida = unimed_tv.text.toString()
                val emailProductor = emailM.toString()
                val sdf = SimpleDateFormat("dd/M/yyyy")
                val fecha = sdf.format(Date()).toString()

                db.collection("Users")
                    .document(emailM)
                    .get()
                    .addOnSuccessListener {
                        val nombreProductor = it.get("nombre").toString()
                        val contactoProductor = it.get("celular").toString().toInt()

                        val product = Product(
                            nombreproduct,
                            fecha,
                            nombreProductor,
                            precio,
                            categoria,
                            desc,
                            cantidad,
                            emailProductor,
                            unidadMedida,
                            contactoProductor
                        )

                        db.collection("Productos").add(product)
                            .addOnSuccessListener {
                                AlertDialog.Builder(this).apply {
                                    setTitle("Subir nuevo producto")
                                    setMessage("Se ha creado el producto '$nombreproduct' correctamente")
                                    setPositiveButton("Aceptar") { _: DialogInterface, _: Int ->
                                        finish()
                                        showProducts(emailM)
                                    }
                                }.show()
                            }
                            .addOnFailureListener {
                                AlertDialog.Builder(this).apply {
                                    setTitle("Error")
                                    setMessage("Ocurrió un error al crear el producto '$nombreproduct'")
                                    setPositiveButton("Entendido", null)
                                }.show()
                            }

                    }
            }
        }
    }

    private fun showProducts(email : String){
        val intent = Intent(this, ProductsActivity::class.java).apply{
            putExtra("email", email)
        }
        startActivity(intent)
    }
}