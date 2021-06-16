package com.example.agromercado

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.agromercado.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityProfileBinding
    private val db = FirebaseFirestore.getInstance()

    var emailM = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // GMaps
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //Setup
        val bundle:Bundle? = intent.extras
        val email = bundle?.getString("email")
        emailM = email?:""
        obtenerDatos(email?:"")

        editButton.setOnClickListener() {
            showEditProfile(email?:"")
            //finish()
            //authLayout.visibility = View.INVISIBLE
            //onBackPressed()

        }
        Log.d("doc", ""+emailM)
    }


    private fun obtenerDatos(email: String){
        Log.d("doc", "$email")
        db.collection("Users")
                .document(email)
                .get()
                .addOnSuccessListener { task ->
                    nombreTextView.setText(task.get("nombre") as String?)
                    descripcionTextView.setText(task.get("descripción") as String?)

                    if (task.get("enlaces") == "Añade un sitio web..." || task.get("enlaces") == ""){
                        enlacesTextView.visibility = View.INVISIBLE
                    }
                    else{
                        enlacesTextView.setText("Sitio web: "+ task.get("enlaces"))
                    }
                    direccionTextView.setText("Dirección: "+ task.get("direccion"))
                    comunaTextView.setText("Comuna: " + task.get("comuna"))
                    regionTextView.setText("Región: "+ task.get("region"))
                    celularTextView.setText("N°: "+task.get("celular").toString())
                }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
                }
    }

    private fun showEditProfile(email: String){
        val intent = Intent(this, EditProfileActivity::class.java).apply{
            putExtra("email", email)
        }
        startActivity(intent)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        db.collection("Users")
            .document(emailM)
            .get()
            .addOnSuccessListener { task ->
                val latitud = task.get("latitud") as Double
                val longitud = task.get("longitud") as Double
                val nombre = task.get("nombre") as String

                Log.d("doc", "$latitud , $longitud , $nombre")

                val dispo = LatLng(latitud, longitud)
                mMap.addMarker(MarkerOptions().position(dispo).title(nombre)) //Marcador rojo
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dispo, 18f)) //Movimiento y zoom del visor
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Error al cargar geodatos", Toast.LENGTH_SHORT).show()
            }
    }
}