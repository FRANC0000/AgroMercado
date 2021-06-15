package com.example.agromercado

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //setup
        val bundle = intent.extras
        val email = bundle?.getString("email")
        setup(email ?: "")

        val prefs = getSharedPreferences(getString(R.string.prefs_file),
            Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.apply()
    }

    private fun setup(email: String){
        tv_emailValidador.text = email

        btnCerrarSesion.setOnClickListener {
            val prefs = getSharedPreferences(getString(R.string.prefs_file),
                Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()
            showAuth()
            auth.signOut()
            onBackPressed()
        }

        btnMiPerfil.setOnClickListener(){
            val intent = Intent(this, ProfileActivity::class.java).apply { putExtra("email", email) }
            startActivity(intent)
        }

        btnMisProductos.setOnClickListener {
            val intent = Intent(this, ProductsActivity::class.java).apply{
                putExtra("email", email)
            }
            startActivity(intent)
        }
    }

    private fun showAuth(){
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
    }

}