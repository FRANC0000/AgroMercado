package com.example.agromercado

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_add_product.*
import kotlinx.android.synthetic.main.activity_edit_profile.*


class EditProfileActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    var posGender = 0

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

        //spinner unidad de medida
        val genero = listOf("Masculino", "Femenino", "Otro")
        val adaptadorGender = ArrayAdapter(this, android.R.layout.simple_spinner_item, genero)
        gender_tv.adapter = adaptadorGender
        gender_tv.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                posGender = position
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
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

            if(rutTextView.text.isEmpty() ||
                name_tv.text.isEmpty() ||
                datePicker.text.isEmpty() ||
                cel_tv.text.isEmpty() ||
                dir_tv.text.isEmpty() ||
                comuna_tv.text.isEmpty() ||
                region_tv.text.isEmpty() ||
                desc_tv.text.isEmpty() ||
                lat_tv.text.isEmpty() ||
                long_tv.text.isEmpty()) {

                AlertDialog.Builder(this).apply {
                    setTitle("Error")
                    setMessage("Los campos no pueden estar vac??os")
                    setPositiveButton("Aceptar", null)
                }.show()
            }
            else {
                var generoPalabras = ""

                if (posGender == 0){
                    generoPalabras = "Masculino"
                }

                if (posGender == 1){
                    generoPalabras = "Femenino"
                }

                if (posGender == 2){
                    generoPalabras = "Otro"
                }

                db.collection("Users").document(email).set(
                    hashMapOf(
                        "rut" to rutTextView.text.toString(),
                        "nombre" to name_tv.text.toString(),
                        "fechaNac" to datePicker.text.toString(),
                        "genero" to generoPalabras,
                        "celular" to cel_tv.text.toString().toInt(),
                        "direccion" to dir_tv.text.toString(),
                        "comuna" to comuna_tv.text.toString(),
                        "region" to region_tv.text.toString(),
                        "descripci??n" to desc_tv.text.toString(),
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
                    //Buscar c??mo mostrar cu??l es el error
                    AlertDialog.Builder(this).apply {
                        setTitle("Error")
                        setMessage(it.message)
                        setPositiveButton("Aceptar", null)
                    }.show()
                }
            }
        }
    }

    private fun obtenerDatos(email: String) {
        db.collection("Users")
            .document(email)
            .get()
            .addOnSuccessListener { task ->
                val geocoder = Geocoder(this)
                val listaDir = geocoder.getFromLocationName((task.get("direccion") as String? +", "+ task.get("comuna") as String? +", "+ task.get("region") as String?), 1)
                if (listaDir.isNotEmpty()){
                    var direcc = listaDir[0]
                    var lat = direcc.latitude
                    var long = direcc.longitude
                    lat_tv.setText(lat.toString())
                    long_tv.setText(long.toString())
                }
                else{
                    lat_tv.setText("-33.51087")
                    long_tv.setText("-70.75311")
                }
                rutTextView.setText(task.get("rut") as String?)
                name_tv.setText(task.get("nombre") as String?)
                datePicker.setText(task.get("fechaNac") as String?)
                desc_tv.setText(task.get("descripci??n") as String?)
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