package com.example.agromercado

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.product_post.view.*

class CatalogoAdapter(private val activity: Activity, private val dataset: List<Product>)
    : RecyclerView.Adapter<CatalogoAdapter.ViewHolder>() {

    class ViewHolder(val layout: View) : RecyclerView.ViewHolder(layout)

    private val auth = FirebaseAuth.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout =
            LayoutInflater.from(parent.context).inflate(R.layout.product_post, parent, false)

        return ViewHolder(layout)
    }

    override fun getItemCount() = dataset.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val catalogo = dataset[position]

        holder.layout.prodName_tv.text = catalogo.nombreProducto
        holder.layout.prodCate_tv.text = catalogo.categoria
        holder.layout.prodProv_tv.text = catalogo.nameProductor
        holder.layout.prodPrecio_tv.text = "$ ${catalogo.precio}"

        if (auth.currentUser!!.email == catalogo.emailProductor) {
            holder.layout.buyBtn.visibility = View.INVISIBLE
            holder.layout.buyBtn.width = 0
            holder.layout.detailBtn.setOnClickListener {
                val email = catalogo.emailProductor.toString()
                val idprod = catalogo.uid.toString()
                showDetailProduct(email, idprod)
            }
        } else {
            holder.layout.detailBtn.setOnClickListener {
                val email = catalogo.emailProductor.toString()
                val idprod = catalogo.uid.toString()
                showDetailProduct(email, idprod)
            }
        }
    }

    private fun showDetailProduct(email: String, idprod: String){

        val intent = Intent(activity, ProductDetailActivity::class.java).apply{
            putExtra("email", email)
            putExtra("idprod", idprod)
        }
        activity.startActivity(intent)
    }
}

