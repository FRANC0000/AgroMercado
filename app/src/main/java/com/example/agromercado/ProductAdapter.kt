package com.example.agromercado

import android.app.Activity
import android.content.Intent
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile.view.*
import kotlinx.android.synthetic.main.product_post.view.*

class ProductAdapter(private val activity: Activity, private val dataset : List<Product>)
    :RecyclerView.Adapter<ProductAdapter.ViewHolder>(){

    private val auth = FirebaseAuth.getInstance()

    class  ViewHolder (val layout: View) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.product_post, parent, false)
        return ViewHolder(layout)
    }

    override fun getItemCount() = dataset.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = dataset[position]

        holder.layout.prodName_tv.text = product.nombreProducto
        holder.layout.prodCate_tv.text = product.categoria
        holder.layout.prodProv_tv.text = product.nameProductor
        holder.layout.prodPrecio_tv.text = "$" + product.precio.toString()


        if (auth.currentUser!!.email != product.emailProductor){
            holder.layout.visibility = View.INVISIBLE
            holder.layout.layoutParams.height = 0
            holder.layout.layoutParams.width = 0
        }
        else{
            holder.layout.detailBtn.setOnClickListener {
                val email = auth.currentUser!!.email.toString()
                showDetailProduct(email)
            }
        }
    }

    private fun showDetailProduct(email: String){
        val intent = Intent(activity, ProductDetailActivity::class.java).apply{
            putExtra("email", email)
        }
        activity.startActivity(intent)
    }
}