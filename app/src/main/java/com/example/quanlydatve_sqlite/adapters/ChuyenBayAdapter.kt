package com.example.quanlydatve_sqlite.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.models.ChuyenBay

class ChuyenBayAdapter(
    private val danhSach: List<ChuyenBay>,
    private val onDatVeClick: (ChuyenBay) -> Unit
) : RecyclerView.Adapter<ChuyenBayAdapter.ChuyenBayViewHolder>() {

    inner class ChuyenBayViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val imgHinh: ImageView = view.findViewById(R.id.imgHinh)
        val txtTuDen: TextView = view.findViewById(R.id.txtTuDen)
        val txtNgayDi: TextView = view.findViewById(R.id.txtNgayDi)
        val txtNgayVe: TextView = view.findViewById(R.id.txtNgayVe)
        val txtGia: TextView = view.findViewById(R.id.txtGia)
        val btnDatVe: Button = view.findViewById(R.id.btnDatVe)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChuyenBayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chuyen_bay, parent, false)
        return ChuyenBayViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChuyenBayViewHolder, position: Int) {
        val chuyenBay = danhSach[position]
        holder.txtTuDen.text = "${chuyenBay.tu} → ${chuyenBay.den}"
        holder.txtNgayDi.text = "Ngày đi: ${chuyenBay.ngayDi}"
        holder.txtNgayVe.text = "Ngày về: ${chuyenBay.ngayVe}"
        holder.txtGia.text = String.format("Giá vé: %.0f VNĐ", chuyenBay.giaVe)
        holder.imgHinh.setImageResource(chuyenBay.getHinhAnhResId())

        holder.btnDatVe.setOnClickListener {
            onDatVeClick(chuyenBay)
        }
    }

    override fun getItemCount(): Int = danhSach.size
}
