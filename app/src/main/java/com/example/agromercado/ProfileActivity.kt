package com.example.agromercado

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.location.Geocoder
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class ProfileActivity : AppCompatActivity(), OnMapReadyCallback {

    private val auth = FirebaseAuth.getInstance()
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

        if (auth.currentUser?.email != email){

            editButton.layoutParams.width = 0
            editButton.layoutParams.height= 0
            editButton.visibility = View.INVISIBLE
        }

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
                        enlacesTextView.layoutParams.height = 0
                        enlacesTextView.layoutParams.width = 0
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
                //val direccion = task.get("direccion") as String
                //val comuna = task.get("comuna") as String
                //val region = task.get("region") as String

                Log.d("doc", "$latitud , $longitud , $nombre")

                val dispo = LatLng(latitud, longitud)
                //val cerca1 = LatLng(-33.51087, -70.75311)
                //val cerca2 = LatLng(-33.51087, -70.75144)

                /*fun getLatLong(){
                    val geocoder = Geocoder(this)
                    val listaDir = geocoder.getFromLocationName("Talcahuano #770, Lo Prado, Región Metropolitana", 1)
                    if (listaDir.isNotEmpty()){
                        var direcc = listaDir[0]
                        var london = LatLng(direcc.latitude, direcc.longitude)
                        mMap.addMarker(MarkerOptions().position(london).title(nombre))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(london, 16f)) //Movimiento y zoom de la cámara
                    }
                }*/

                //getLatLong()

                fun getAddress(latitud: Double, longitud: Double): String {
                    val geocoder = Geocoder(this)
                    val list = geocoder.getFromLocation(latitud, longitud, 1)
                    return list[0].getAddressLine(0)
                }

                val address = getAddress(latitud, longitud)
                val carrotMarker = mMap.addMarker(MarkerOptions().position(dispo).title(nombre).snippet(address).icon(
                    BitmapDescriptorFactory.fromResource(R.drawable.carrotmarker)))
                //mMap.addMarker(MarkerOptions().position(cerca1).title(nombre).snippet("cerca1").draggable(true))
                //mMap.addMarker(MarkerOptions().position(cerca2).title(nombre).snippet("cerca2"))
                carrotMarker //Marcador
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dispo, 16f)) //Movimiento y zoom de la cámara
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Error al cargar geodatos", Toast.LENGTH_SHORT).show()
            }
    }
}