package com.example.agromercado

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        //Setup
        setup()
    }

    private fun setup(){
        btnIngresar.setOnClickListener {
            val email = et_Email.text.toString()
            val pass = et_Password.text.toString()

            auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener {
                    Toast.makeText(this, "Se ha autenticado al usuario", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java).apply{
                        putExtra("email", email)
                    }
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    AlertDialog.Builder(this).apply{
                        setTitle("Error")
                        setMessage(it.message)
                        setPositiveButton("Aceptar", null)
                    }.show()
                }
        }

        btnRegistrar.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }
}