package com.example.barcodetest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class BarcodePriceAdapter : RecyclerView.Adapter<BarcodePriceAdapter.ViewHolder>() {

    private var data = listOf<Pair<String, Double>>()

    fun setData(data: List<Pair<String, Double>>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.activity_main, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.barcodeTextView.text = item.first
        holder.priceTextView.text = item.second.toString()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val barcodeTextView: TextView = itemView.findViewById(R.id.barcode_text)
        val priceTextView: TextView = itemView.findViewById(R.id.price_text)
    }
}
