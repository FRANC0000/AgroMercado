package com.example.agromercado

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_products.*

class ProductsActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_products)

        //setup
        val bundle = intent.extras
        val email = bundle?.getString("email")
        //setup(email ?: "")

        db.collection("Productos")
            .addSnapshotListener { value, error ->

                val products = value!!.toObjects(Product::class.java)
                Log.d("doc", "products $products")

                products.forEachIndexed { index, product ->
                    product.uid = value.documents[index].id
                }

                rvProducts.apply {
                    setHasFixedSize(true)
                    layoutManager = LinearLayoutManager(this@ProductsActivity)
                    adapter = ProductAdapter(this@ProductsActivity, products)
                }

            }

        addProductBtn.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java).apply{
                putExtra("email", email)
            }
            startActivity(intent)
            finish()
        }

    }
}