package com.example.quanlydatve_sqlite.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.models.Flight

class DanhSachChuyenBayNoiBatAdapter(
    private val flights: List<Flight>,
    private val onItemClick: (Flight) -> Unit,
    private val infinite: Boolean = false // CHỈ MainActivity mới set true
) : RecyclerView.Adapter<DanhSachChuyenBayNoiBatAdapter.FlightViewHolder>() {

    companion object {
        private const val LOOP_FACTOR = 1000
    }

    inner class FlightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.Image)
        val name: TextView = itemView.findViewById(R.id.Name)
        val gia: TextView = itemView.findViewById(R.id.Gia)
        val mota: TextView = itemView.findViewById(R.id.detailDescriptionTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_flight, parent, false)
        return FlightViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        if (flights.isEmpty()) return
        val actualPos = if (infinite) position % flights.size else position
        val flight = flights[actualPos]
        holder.name.text = flight.ten
        holder.gia.text = formatGiaVND(flight.gia)
        holder.mota.text = flight.description
        holder.image.setImageResource(flight.hinhAnhResId)

        holder.itemView.setOnClickListener { onItemClick(flight) }
    }

    override fun getItemCount(): Int =
        if (flights.isEmpty()) 0
        else if (infinite) flights.size * LOOP_FACTOR
        else flights.size

    private fun formatGiaVND(giaStr: String): String {
        val cleanGiaStr = giaStr.replace("[^\\d]".toRegex(), "")
        val giaInt = cleanGiaStr.toIntOrNull() ?: 0
        return "%,d VNĐ".format(giaInt).replace(',', '.')
    }
}
