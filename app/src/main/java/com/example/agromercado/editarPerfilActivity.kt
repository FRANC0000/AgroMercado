package com.example.agromercado

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_editar_perfil.*
import kotlinx.android.synthetic.main.activity_editar_perfil.celularTextView
import kotlinx.android.synthetic.main.activity_editar_perfil.comunaTextView
import kotlinx.android.synthetic.main.activity_editar_perfil.descripcionTextView
import kotlinx.android.synthetic.main.activity_editar_perfil.direccionTextView
import kotlinx.android.synthetic.main.activity_editar_perfil.enlacesTextView
import kotlinx.android.synthetic.main.activity_editar_perfil.nombreTextView
import kotlinx.android.synthetic.main.activity_editar_perfil.regionTextView
import kotlinx.android.synthetic.main.activity_profile.*

class editarPerfilActivity : AppCompatActivity() {
    val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_perfil)

        val bundle: Bundle? = intent.extras
        val email = bundle?.getString("email")
        setup(email ?: "")
        obtenerDatos(email ?: "")

        datePicker.setOnClickListener {
            showDatePickerDialog()
        }

    }

    private fun showDatePickerDialog() {
        val datePickerr =
            DatePickerFragment { day, month, year -> onDateSelected(day, month, year) }
        datePickerr.show(supportFragmentManager, "DatePicker")
    }

    fun onDateSelected(day: Int, month: Int, year: Int) {
        val month2 = month + 1
        datePicker.setText("$day/$month2/$year")
    }

    private fun setup(email: String) {

        guardarDatosButton.setOnClickListener() {
            db.collection("Users").document(email).set(
                hashMapOf(
                    "rut" to rutTextView.text.toString(),
                    "nombre" to nombreTextView.text.toString(),
                    "fechaNac" to datePicker.text.toString(),
                    "genero" to generoTextView.text.toString(),
                    "celular" to celularTextView.text.toString().toInt(),
                    "direccion" to direccionTextView.text.toString(),
                    "comuna" to comunaTextView.text.toString(),
                    "region" to regionTextView.text.toString(),
                    "descripción" to descripcionTextView.text.toString(),
                    "latitud" to latitudTextView.text.toString().toDouble(),
                    "longitud" to longitudTextView.text.toString().toDouble(),
                    "enlaces" to enlacesTextView.text.toString()
                )
            ).addOnSuccessListener {
                Toast.makeText(applicationContext, "Datos modificados", Toast.LENGTH_SHORT).show()
                val intent =
                    Intent(this, ProfileActivity::class.java).apply { putExtra("email", email) }
                startActivity(intent)
                finish()

            }.addOnFailureListener {
                //Buscar cómo mostrar cuál es el error
                AlertDialog.Builder(this).apply {
                    setTitle("Error")
                    setMessage(it.message)
                    setPositiveButton("Aceptar", null)
                }.show()
            }
        }
    }

    private fun obtenerDatos(email: String) {
        db.collection("Users")
            .document(email)
            .get()
            .addOnSuccessListener { task ->
                rutTextView.setText(task.get("rut").toString())
                nombreTextView.setText(task.get("nombre") as String?)
                datePicker.setText(task.get("fechaNac") as String?)
                generoTextView.setText(task.get("genero") as String?)
                latitudTextView.setText(task.get("latitud").toString())
                longitudTextView.setText(task.get("longitud").toString())
                descripcionTextView.setText(task.get("descripción") as String?)
                enlacesTextView.setText(task.get("enlaces") as String?)
                celularTextView.setText(task.get("celular").toString())
                direccionTextView.setText(task.get("direccion") as String?)
                comunaTextView.setText(task.get("comuna") as String?)
                regionTextView.setText(task.get("region") as String?)
            }
            .addOnFailureListener {
                AlertDialog.Builder(this).apply {
                    setTitle("Error")
                    setMessage(it.message)
                    setPositiveButton("Aceptar", null)
                }.show()
            }
    }
}