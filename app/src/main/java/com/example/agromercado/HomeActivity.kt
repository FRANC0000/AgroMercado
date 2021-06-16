package com.example.agromercado

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

        checkForLocationPermission()

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

    private fun checkForLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            //Permiso no aceptado por el momento
            requestLocationPermission()
        }else{
            //Acceder a localización
        }
    }

    private fun requestLocationPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            //El usuario ya ha rechazado los permisos
            Toast.makeText(this, "Permiso rechazado, vaya a información de la aplicación", Toast.LENGTH_LONG).show()
        }else{
            //Pedir permisos
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 999)
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 999){ //Nuestros permisos
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permiso a ubicación concedido", Toast.LENGTH_SHORT).show()

            }else{
                //El permiso no ha sido aceptado
                Toast.makeText(this, "Permiso no aceptado", Toast.LENGTH_SHORT).show()
            }
        }
    }

}