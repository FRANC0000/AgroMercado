package com.example.agromercado

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.carro_detail.view.*
import kotlinx.android.synthetic.main.product_post.view.*

class CarroAdapter(private val carrito : MutableList<Carro>) : RecyclerView.Adapter<CarroAdapter.ViewHolder>() {

    class ViewHolder(val layout: View) : RecyclerView.ViewHolder(layout)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.carro_detail, parent, false)
        return ViewHolder(layout)
    }

    override fun getItemCount(): Int = carrito.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val detallecarro = carrito[position]
        holder.layout.nombreprod_carro_tv.text = detallecarro.nombreProducto
        holder.layout.precioprod_carro_tv.text = detallecarro.precioProducto.toString()
        holder.layout.cantidadprod_carro_tv.text = detallecarro.cantidad.toString()
        holder.layout.subtotal_carro_tv.text = detallecarro.subtotal.toString()

        holder.layout.eliminarBtn.setOnClickListener {
            carrito.removeAt(position)
            notifyDataSetChanged()

        }

    }
}