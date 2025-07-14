package com.example.quanlydatve_sqlite.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.models.Flight

class FlightAdapter(
    private val flights: List<Flight>,
    private val onItemClick: (Flight) -> Unit
) : RecyclerView.Adapter<FlightAdapter.FlightViewHolder>() {

    inner class FlightViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.Image)
        val name: TextView = itemView.findViewById(R.id.Name)
        val gia: TextView = itemView.findViewById(R.id.Gia)
        val mota: TextView = itemView.findViewById(R.id.detailDescriptionTextView)

        fun bind(flight: Flight) {
            name.text = flight.ten
            gia.text = formatGiaVND(flight.gia)
            mota.text = flight.description
            image.setImageResource(flight.hinhAnhResId)
            itemView.setOnClickListener {
                onItemClick(flight)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlightViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_flight, parent, false)
        return FlightViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlightViewHolder, position: Int) {
        holder.bind(flights[position])
    }

    override fun getItemCount(): Int = flights.size

    private fun formatGiaVND(giaStr: String): String {
        val cleanGiaStr = giaStr.replace("[^\\d]".toRegex(), "")
        val giaInt = cleanGiaStr.toIntOrNull() ?: 0

        // Debug log để kiểm tra
        android.util.Log.d("FlightAdapter", "Gia goc='$giaStr', sau khi xoa ky tu la='$cleanGiaStr', so=$giaInt")

        return "%,d VNĐ".format(giaInt).replace(',', '.')
    }

}
