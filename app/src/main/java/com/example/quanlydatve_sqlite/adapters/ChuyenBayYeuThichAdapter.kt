package com.example.quanlydatve_sqlite.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quanlydatve_sqlite.databinding.ItemLikeChuyenBayBinding
import com.example.quanlydatve_sqlite.models.ChuyenBayYeuThich
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.users.DatVeActivity

class ChuyenBayYeuThichAdapter(
    private val context: Context,
    private var list: MutableList<ChuyenBayYeuThich>,
    private val onDelete: (ChuyenBayYeuThich, Int) -> Unit = { _, _ -> },
    private val onClick: (ChuyenBayYeuThich) -> Unit = {}
) : RecyclerView.Adapter<ChuyenBayYeuThichAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemLikeChuyenBayBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLikeChuyenBayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val binding = holder.binding

        // Load hình chuyến bay
        val resId = context.resources.getIdentifier(item.hinhAnh, "drawable", context.packageName)
        if (resId != 0) {
            binding.hinhChuyenBay.setImageResource(resId)
        } else {
            // Nếu không có hình, dùng placeholder
            Glide.with(context)
                .load(R.drawable.background_airplane)
                .into(binding.hinhChuyenBay)
        }

        // Binding thông tin chuyến bay
        binding.tenChuyenBay.text = "${item.tu} - ${item.den}"
        binding.NgayDi.text = item.ngayDi
        binding.NgayVe.text = item.ngayVe
        binding.Gia.text = "%,.0f đ".format(item.giaVe)

        // Xem chi tiết (tùy ý)
        binding.icTicket.setOnClickListener {
            val intent = Intent(context, DatVeActivity::class.java)
            intent.putExtra("EXTRA_TU", item.tu)
            intent.putExtra("EXTRA_DEN", item.den)
            intent.putExtra("EXTRA_NGAY_DI", item.ngayDi)
            intent.putExtra("EXTRA_NGAY_VE", item.ngayVe)
            intent.putExtra("EXTRA_GIA", item.giaVe)
            intent.putExtra("EXTRA_HINHANH", item.hinhAnh)
            context.startActivity(intent)
        }


        // Xóa yêu thích
        binding.icDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa chuyến bay yêu thích này không?")
                .setPositiveButton("Đồng ý") { _, _ ->
                    onDelete(item, position)
                }
                .setNegativeButton("Hủy", null)
                .show()
        }
    }

    override fun getItemCount(): Int = list.size

    fun removeAt(position: Int) {
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updateData(newList: List<ChuyenBayYeuThich>) {
        list = newList.toMutableList()
        notifyDataSetChanged()
    }
}
