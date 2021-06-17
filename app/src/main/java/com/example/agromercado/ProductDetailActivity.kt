package com.example.agromercado

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_product_detail.*

class ProductDetailActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        //setup
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val idprod = bundle?.getString("idprod")

        db.collection("Productos")
            .document(idprod?:"")
            .get()
            .addOnSuccessListener {

                nombreProducto_et.setText(it.get("nombreProducto").toString())
                fecha_et.setText(it.get("fecha").toString())
                nameProductor_et.setText(it.get("nameProductor").toString())
                precio_et.setText(it.get("precio").toString())
                categoria_et.setText(it.get("categoria").toString())
                descripcion_et.setText(it.get("descripcion").toString())
                cantidad_et.setText(it.get("cantidad").toString())
                emailProductor_et.setText(it.get("emailProductor").toString())
                unidadMedida_et.setText(it.get("unidadMedida").toString())
                contactoProductor_et.setText(it.get("contactoProductor").toString())

            }

        editProductBtn.setOnClickListener {
            val intent = Intent(this, EditProductActivity::class.java).apply{
                putExtra("email", email)
                putExtra("idprod", idprod)
            }
            startActivity(intent)
            finish()

        }

        deleteProductBtn.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("Error")
                setMessage("¿Seguro que quieres eliminar este producto?")
                setPositiveButton("Aceptar"){ dialogInterface: DialogInterface, i: Int ->

                    db.collection("Productos")
                        .document(idprod?:"")
                        .delete()
                        .addOnSuccessListener {
                            finish()

                            val intent = Intent(this@ProductDetailActivity, ProductsActivity::class.java).apply{
                                putExtra("email", email)
                            }
                            startActivity(intent)
                        }

                }
                setNegativeButton("No", null)
            }.show()
        }
    }
}