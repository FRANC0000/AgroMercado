package com.example.agromercado

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.api.Distribution
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_carro_compras.*
import java.text.SimpleDateFormat
import java.util.*

class CarroComprasActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carro_compras)


        val bundle = intent.extras
        val carro = bundle?.getSerializable("carro") as MutableList<Carro>
        val email = bundle?.getString("email")

        Log.d("doc", "$carro")

        detalleBoletaRV.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@CarroComprasActivity)
            adapter = CarroAdapter(carro)

        }

        db.collection("Users")
                .document(email?:"")
                .get()
                .addOnSuccessListener {

                    nombreComprador_boleta_tv.text = it.get("nombre").toString()
                    emailComprador_boleta_tv.text = email?:""
                    val sdf = SimpleDateFormat("dd/M/yyyy")
                    fecha_boleta_tv.text = sdf.format(Date()).toString()

                    var pagoTotal = 0

                    for (detalle in carro){
                        pagoTotal += detalle.subtotal!!
                    }

                    pagoTotal_boleta_tv.text = "$$pagoTotal"
                }



    }
}