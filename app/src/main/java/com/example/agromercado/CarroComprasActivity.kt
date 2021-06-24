package com.example.agromercado

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_carro_compras.*
import kotlinx.android.synthetic.main.activity_carro_compras.view.*
import java.text.SimpleDateFormat
import java.util.*


class CarroComprasActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    var pagoTotal = 0
    var posPago = 0
    var carroG = mutableListOf<Carro>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carro_compras)

        val bundle = intent.extras
        val carro = bundle?.getSerializable("carro") as MutableList<Carro>
        carroG = carro
        val email = bundle.getString("email")


        Log.d("doc", "$carro")

        //spinner metodo de pago
        val metodoPago = listOf("Débito", "Crédito", "Junaeb")
        val adaptadorMetodoPago = ArrayAdapter(this, android.R.layout.simple_spinner_item, metodoPago)
        metodoPagoSpinner.adapter = adaptadorMetodoPago
        metodoPagoSpinner.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                posPago = position
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        //card
        detalleBoletaRV.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@CarroComprasActivity)
            adapter = CarroAdapter(carro)

        }
        totalAPagar()
        Pagar()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, HomeActivity::class.java).apply{
            putExtra("email", auth.currentUser?.email.toString())
        })
    }


    fun totalAPagar(){
        //tomar total a pagar
        for (detalle in carroG){
            pagoTotal += detalle.subtotal!!
        }
        pagoTotal_boleta_tv.text = "$$pagoTotal"
    }


    private fun Pagar(){
        PagarBtn.setOnClickListener {
            if (carroG.isEmpty()){
                AlertDialog.Builder(this).apply {
                    setTitle("Carrito")
                    setMessage("El carro está vacío.")
                    setPositiveButton("Aceptar"){ dialogInterface: DialogInterface, i: Int ->
                        finish()
                    }
                }.show()
            }
            else{
                totalAPagar()
                var direccion = ""
                var comuna = ""
                var region = ""
                val entrega :Boolean
                var metodoPalabras = ""

                if(despachoRB.isChecked){
                    entrega = true

                    if (direccionDespacho_et.text.isEmpty()
                            && comunaDespacho_et.text.isEmpty()
                            && regionDespacho_et.text.isEmpty()) {

                        AlertDialog.Builder(this).apply {
                            setTitle("Datos de despacho")
                            setMessage("Los datos de despacho no pueden estar vacíos")
                            setPositiveButton("Aceptar", null)
                        }.show()
                    }else{
                        direccion = direccionDespacho_et.text.toString()
                        comuna = comunaDespacho_et.text.toString()
                        region = regionDespacho_et.text.toString()
                    }

                }
                else{
                    entrega = false
                }

                //pasar item seleccionado en spinner a Nombre
                if (posPago == 0){
                    metodoPalabras = "Débito"
                }
                if (posPago == 1){
                    metodoPalabras = "Crédito"
                }
                if (posPago == 2){
                    metodoPalabras = "Junaeb"
                }

                AlertDialog.Builder(this).apply{
                    setTitle("Confirmar Pago")
                    setMessage("El total a pagar es: $ $pagoTotal. " + System.getProperty("line.separator") +
                            "Su metodo de pago es: $metodoPalabras. " + System.getProperty("line.separator") +
                            "¿Desea confirmar el pago?")
                    setPositiveButton("Confirmar pago"){ _: DialogInterface, _: Int ->
                        Toast.makeText(this.context, "Generando pago...", Toast.LENGTH_SHORT).show()

                        val uid = UUID.randomUUID().toString()
                        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm a")
                        val fecha = sdf.format(Date()).toString()

                        db.collection("Ventas")
                                .document(uid)
                                .set(hashMapOf(
                                        "comprador" to auth.currentUser!!.email.toString(),
                                        "fecha" to fecha,
                                        "metodoPago" to metodoPalabras,
                                        "nombreComprador" to auth.currentUser!!.displayName.toString(),
                                        "pagoTotal" to pagoTotal,
                                        "Entrega" to entrega,
                                        "direccionEntrega" to direccion,
                                        "comunaEntrega" to comuna,
                                        "regionEntrega" to region
                                ))
                                .addOnSuccessListener {

                                    carroG.forEachIndexed{ i: Int, carro: Carro ->
                                        db.collection("Ventas")
                                                .document(uid)
                                                .collection("Detalle")
                                                .document(carro.id.toString())
                                                .set(hashMapOf(
                                                        "cantidad" to carro.cantidad,
                                                        "nombreProducto" to carro.nombreProducto,
                                                        "precioProducto" to carro.precioProducto,
                                                        "subtotal" to carro.subtotal
                                                ))
                                                .addOnSuccessListener {
                                                    db.collection("Productos")
                                                            .document(carro.id.toString())
                                                            .get()
                                                            .addOnSuccessListener {
                                                                val newCantidad = it["cantidad"].toString().toInt() - carro.cantidad!!

                                                                db.collection("Productos")
                                                                        .document(carro.id.toString())
                                                                        .set(hashMapOf(
                                                                                "cantidad" to newCantidad,
                                                                                "categoria" to it.get("categoria"),
                                                                                "contactoProductor" to it.get("contactoProductor"),
                                                                                "descripcion" to it.get("descripcion"),
                                                                                "emailProductor" to it.get("emailProductor"),
                                                                                "fecha" to it.get("fecha"),
                                                                                "nameProductor" to it.get("nameProductor"),
                                                                                "nombreProducto" to it.get("nombreProducto"),
                                                                                "precio" to it.get("precio"),
                                                                                "unidadMedida" to it.get("unidadMedida")
                                                                        ))
                                                                        .addOnSuccessListener {
                                                                            Toast.makeText(this.context, "Stock Actualizado", Toast.LENGTH_SHORT).show()
                                                                        }
                                                            }
                                                }
                                    }
                                    Toast.makeText(this.context, "Se ha generado el pago exitosamente.", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                    }
                    setNegativeButton("Cancelar"){ dialogInterface: DialogInterface, i: Int ->
                        pagoTotal = 0
                        pagoTotal_boleta_tv.text = "$$pagoTotal"
                    }
                }.show()
            }
        }
    }
}