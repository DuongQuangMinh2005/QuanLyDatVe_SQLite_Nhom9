package com.example.quanlydatve_sqlite.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlydatve_sqlite.networks.DatabaseHelper
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.models.Booking

class BookingAdapter(
    private val bookingList: List<Booking>,
    private val context: Context,
    private val dbHelper: DatabaseHelper
) : RecyclerView.Adapter<BookingAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val img: ImageView = view.findViewById(R.id.Image)
        val name: TextView = view.findViewById(R.id.Name)
        val ngayDi: TextView = view.findViewById(R.id.NgayDi)
        val ngayVe: TextView = view.findViewById(R.id.NgayVe)
        val loaiVe: TextView = view.findViewById(R.id.LoaiVe)
        val soLuong: TextView = view.findViewById(R.id.SoLuong)
        val thanhTien: TextView = view.findViewById(R.id.ThanhTien)
        val phuongThuc: TextView = view.findViewById(R.id.PhuongThuc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ve_chuyen_bay, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = bookingList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val booking = bookingList[position]

        holder.name.text = "${booking.tu} → ${booking.den}"
        holder.ngayDi.text = "Đi: ${booking.ngayDi}"
        holder.ngayVe.text = "Về: ${booking.ngayVe}"
        holder.loaiVe.text = "Loại vé: ${booking.ticketType}"
        holder.soLuong.text = "Số lượng: ${booking.quantity}"
        holder.thanhTien.text = "Tổng tiền: %,d VNĐ".format(booking.totalPrice.toInt())
        holder.phuongThuc.text = "Thanh toán: ${booking.paymentMethod}"

        val imageName = dbHelper.getHinhAnhForRoute(booking.tu, booking.den)
        val imageResId = context.resources.getIdentifier(
            imageName, "drawable", context.packageName
        )


        if (imageResId != 0) {
            holder.img.setImageResource(imageResId)
        } else {
            holder.img.setImageResource(R.drawable.ic_airplane_decor)
        }
    }
}
