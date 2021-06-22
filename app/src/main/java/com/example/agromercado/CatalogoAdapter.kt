package com.example.agromercado

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_home.view.*
import kotlinx.android.synthetic.main.activity_product_detail.view.*
import kotlinx.android.synthetic.main.product_post.view.*
import java.io.Serializable

class CatalogoAdapter(private val activity: Activity, private val dataset: List<Product>)
    : RecyclerView.Adapter<CatalogoAdapter.ViewHolder>() {

    class ViewHolder(val layout: View) : RecyclerView.ViewHolder(layout)

    private var carro = listOf<Carro>()
    private var cantidad = 1
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

        holder.layout.np_product.minValue = 1
        holder.layout.np_product.maxValue = catalogo.cantidad!!.toInt()
        holder.layout.np_product.wrapSelectorWheel = true
        holder.layout.np_product.setOnValueChangedListener{ numberPicker: NumberPicker, oldval: Int, newval: Int ->
            cantidad = newval
        }


        if (auth.currentUser!!.email == catalogo.emailProductor) {
            holder.layout.constraintCompras.visibility = View.INVISIBLE
            holder.layout.constraintCompras.layoutParams.width = 0
            holder.layout.constraintCompras.layoutParams.height = 0
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

            holder.layout.buyBtn.setOnClickListener {
                val precio = catalogo.precio
                val subtotal = precio!! * cantidad
                val cantidadCompra = cantidad
                val idProducto = catalogo.uid
                val nombreProducto = catalogo.nombreProducto

                val productoEnCarro = Carro(idProducto, nombreProducto, precio, cantidadCompra, subtotal)

                carro += productoEnCarro


                AlertDialog.Builder(activity).apply {
                    setTitle("Carro de compras")
                    setMessage("Añadiendo: $cantidadCompra '$nombreProducto' al carrito")
                    setPositiveButton("Ver Carrito"){ dialogInterface: DialogInterface, i: Int ->

                        showCarrito(auth.currentUser!!.email.toString(), carro)

                    }
                    setNegativeButton("Seguir Comprando", null)
                }.show()

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

    private fun showCarrito(email: String, carro: List<Carro>) {
        val intent = Intent(activity, CarroComprasActivity::class.java).apply{
            putExtra("email", email)
            putExtra("carro", carro as Serializable)
        }
        activity.startActivity(intent)

    }
}


