package com.example.agromercado

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_edit_profile.*


class EditProfileActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val bundle: Bundle? = intent.extras
        val email = bundle?.getString("email")
        setup(email ?: "")
        obtenerDatos(email ?: "")

        datePicker.setOnClickListener {
            showDatePickerDialog()
        }

        subirArchivo.setOnClickListener{
            checkForStoragePermission()
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
                    "nombre" to name_tv.text.toString(),
                    "fechaNac" to datePicker.text.toString(),
                    "genero" to gender_tv.text.toString(),
                    "celular" to cel_tv.text.toString().toInt(),
                    "direccion" to dir_tv.text.toString(),
                    "comuna" to comuna_tv.text.toString(),
                    "region" to region_tv.text.toString(),
                    "descripción" to desc_tv.text.toString(),
                    "latitud" to lat_tv.text.toString().toDouble(),
                    "longitud" to long_tv.text.toString().toDouble(),
                    "enlaces" to enlaces_tv.text.toString()
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
                rutTextView.setText(task.get("rut") as String?)
                name_tv.setText(task.get("nombre") as String?)
                datePicker.setText(task.get("fechaNac") as String?)
                gender_tv.setText(task.get("genero") as String?)
                lat_tv.setText(task.get("latitud").toString())
                long_tv.setText(task.get("longitud").toString())
                desc_tv.setText(task.get("descripción") as String?)
                enlaces_tv.setText(task.get("enlaces") as String?)
                cel_tv.setText(task.get("celular").toString())
                dir_tv.setText(task.get("direccion") as String?)
                comuna_tv.setText(task.get("comuna") as String?)
                region_tv.setText(task.get("region") as String?)
            }
            .addOnFailureListener {
                AlertDialog.Builder(this).apply {
                    setTitle("Error")
                    setMessage(it.message)
                    setPositiveButton("Aceptar", null)
                }.show()
            }
    }

    private fun checkForStoragePermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            //Abrir almacenamiento
            Toast.makeText(this,"Accediendo a almacenamiento", Toast.LENGTH_SHORT).show()

        }else{
            requestStoragePermission()
        }
    }

    private fun requestStoragePermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
            //El usuario ya ha rechazado los permisos
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Permiso rechazado")
            builder.setMessage("El permiso ya fue rechazado una vez, para concederlo debe hacerlo manualmente desde configuraciones")
            builder.setPositiveButton("Ir") { dialogInterface: DialogInterface, i: Int ->
                val intent = Intent(Settings.ACTION_APPLICATION_SETTINGS)
                startActivity(intent)
            }
            builder.setNegativeButton("No conceder", { dialogInterface: DialogInterface, i: Int -> })
            builder.show()

        }else{
            //Pedir permisos
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 777)
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 777){ //Nuestros permisos
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Accediendo a almacenamiento", Toast.LENGTH_SHORT).show()

            }else{
                //El permiso no ha sido aceptado
                Toast.makeText(this, "Permiso rechazado por primera vez", Toast.LENGTH_SHORT).show()
            }
        }
    }
}