package com.example.agromercado

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var emailM = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnRegister.setOnClickListener {
            val email = et_EmailCreate.text.toString()
            emailM = email
            val pass = et_PasswordCreate.text.toString()
            val name = et_NombreCreate.text.toString()

            auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener {
                    val profile = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                    it.user!!.updateProfile(profile)
                        .addOnSuccessListener {
                            db.collection("Users").document(email).set(hashMapOf(
                                "nombre" to et_NombreCreate.text.toString(),
                                "celular" to 912345678,
                                "comuna" to "Añade una comuna...",
                                "direccion" to "Añade una dirección...",
                                "descripción" to "Añade una descripción...",
                                "enlaces" to "Añade un sitio web...",
                                "fechaNac" to "01/01/2000",
                                "genero" to "Otro",
                                "region" to "Añade una región...",
                                "rut" to "11.111.111-1",
                                "latitud" to -33.5112729808116,
                                "longitud" to -70.75239818684706
                            ))
                            Toast.makeText(this, "Tu cuenta se ha creado correctamente", Toast.LENGTH_SHORT).show()
                            AlertDialog.Builder(this).apply{
                                setTitle("Completar perfil")
                                setMessage("¿Deseas completar tu perfil?")
                                setPositiveButton("Aceptar"){ _: DialogInterface, _: Int ->
                                    showEditProfile(email)
                                }
                                setNegativeButton("No", null)
                            }.show()
                            finish()
                        }
                }
                .addOnFailureListener {
                    AlertDialog.Builder(this).apply{
                        setTitle("Error")
                        setMessage(it.message)
                        setPositiveButton("Aceptar", null)
                    }.show()
                }
        }
    }

    private fun showEditProfile(email: String){
        val intent = Intent(this, editarPerfilActivity::class.java).apply{
            putExtra("email", email)
        }
        startActivity(intent)
    }
}