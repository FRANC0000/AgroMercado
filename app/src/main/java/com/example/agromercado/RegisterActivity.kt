package com.example.agromercado

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        btnRegister.setOnClickListener {
            val email = et_EmailCreate.text.toString()
            val pass = et_PasswordCreate.text.toString()
            val name = et_NombreCreate.text.toString()

            auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener {
                    val profile = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                    it.user!!.updateProfile(profile)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Tu cuenta se ha creado correctamente", Toast.LENGTH_SHORT).show()
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
}