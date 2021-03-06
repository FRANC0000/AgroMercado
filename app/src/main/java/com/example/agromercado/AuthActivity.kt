package com.example.agromercado

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val GOOGLE_SIGN_IN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        //Setup
        setup()
        session()
    }


    private fun session(){

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)

        if(email != null){
            showMenu(email)
        }
    }

    private fun showMenu(email:String){
        val menuIntent = Intent(this, HomeActivity::class.java).apply{
            putExtra("email", email)
        }
        startActivity(menuIntent)
    }
    private fun setup(){
        btnIngresar.setOnClickListener {
            val email = et_Email.text.toString()
            val pass = et_Password.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, pass)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Se ha autenticado al usuario", Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this, HomeActivity::class.java).apply {
                            putExtra("email", email)
                        }
                        startActivity(intent)
                        finish()
                    }
                    .addOnFailureListener {
                        AlertDialog.Builder(this).apply {
                            setTitle("Error")
                            setMessage(it.message)
                            setPositiveButton("Aceptar", null)
                        }.show()
                    }
            }else{
                AlertDialog.Builder(this).apply {
                    setTitle("Error")
                    setMessage("Los campos email y contrase??a no pueden estar vac??os")
                    setPositiveButton("Aceptar", null)
                }.show()
            }
        }

        googleButton.setOnClickListener(){
            //COnfiguracion

            val googleConf =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()

            startActivityForResult(googleClient.signInIntent, GOOGLE_SIGN_IN )

        }

        btnRegistrar.setOnClickListener{
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }

    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLE_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                if (account != null) {
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential)
                        .addOnCompleteListener() {

                            if (it.isSuccessful) {
                                Toast.makeText(this,"Te has registrado exitosamente",Toast.LENGTH_SHORT)

                                db.collection("Users")
                                    .document(account.email?:"")
                                    .get()
                                    .addOnSuccessListener {
                                        if (it.data.isNullOrEmpty()){
                                            AlertDialog.Builder(this).apply {
                                                setTitle("Tu perfil est?? incompleto")
                                                setMessage("??Deseas completar tu perfil?")
                                                setPositiveButton("Si") { _: DialogInterface, _: Int ->
                                                    db.collection("Users")
                                                        .document(account.email?:"")
                                                        .set(hashMapOf(
                                                            "nombre" to account.displayName,
                                                            "latitud" to -33.5112729808116,
                                                            "longitud" to -70.75239818684706
                                                        ))
                                                    showEditProfile(account.email ?: "")
                                                }
                                                setNegativeButton("No") { _: DialogInterface, _: Int ->
                                                    db.collection("Users")
                                                        .document(account.email?:"")
                                                        .set(hashMapOf(
                                                            "nombre" to account.displayName,
                                                            "celular" to 999999999,
                                                            "comuna" to "A??ade una comuna...",
                                                            "direccion" to "A??ade una direcci??n...",
                                                            "descripci??n" to "A??ade una descripci??n...",
                                                            "enlaces" to "A??ade un sitio web...",
                                                            "fechaNac" to "01/01/2000",
                                                            "genero" to "Otro",
                                                            "region" to "A??ade una regi??n...",
                                                            "rut" to "11.111.111-1",
                                                            "latitud" to -33.5112729808116,
                                                            "longitud" to -70.75239818684706))
                                                        .addOnSuccessListener {
                                                            showMenu(account.email ?: "")
                                                        }

                                                }
                                            }.show()
                                        }
                                        else{
                                            showMenu(account.email?:"")
                                        }
                                    }
                            }
                        }
                }
            }catch (e: ApiException){
                showAlert()
            }
        }
    }

    private fun showEditProfile(email: String){
        val intent = Intent(this, EditProfileActivity::class.java).apply{
            putExtra("email", email)
        }
        startActivity(intent)
    }
}
