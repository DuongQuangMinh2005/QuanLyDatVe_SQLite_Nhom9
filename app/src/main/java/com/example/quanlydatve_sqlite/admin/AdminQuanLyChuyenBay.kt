package com.example.quanlydatve_sqlite.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.databinding.ItemAdminChuyenbayBinding
import com.example.quanlydatve_sqlite.models.ChuyenBay
import com.example.quanlydatve_sqlite.networks.DatabaseHelper
import java.io.File
import java.text.DecimalFormat

class AdminQuanLyChuyenBay : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: ChuyenBayAdapter
    private var danhSachChuyenBay = mutableListOf<ChuyenBay>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_qlchuyenbay)

        dbHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerViewChuyenBayAdmin)
        recyclerView.layoutManager = LinearLayoutManager(this)

        super.onResume()
        loadChuyenBay()

        val btnBack = findViewById<ImageView>(R.id.icBack)
        btnBack.setOnClickListener {
            finish()
        }

    }
    override fun onResume() {
        super.onResume()
        loadChuyenBay()
    }
    private fun loadChuyenBay() {
        danhSachChuyenBay = dbHelper.getAllChuyenBayRaw().toMutableList()
        adapter = ChuyenBayAdapter(danhSachChuyenBay)
        recyclerView.adapter = adapter
    }


    inner class ChuyenBayAdapter(private val list: MutableList<ChuyenBay>) : RecyclerView.Adapter<ChuyenBayAdapter.ChuyenBayViewHolder>() {

        inner class ChuyenBayViewHolder(val binding: ItemAdminChuyenbayBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChuyenBayViewHolder {
            val binding = ItemAdminChuyenbayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ChuyenBayViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ChuyenBayViewHolder, position: Int) {
            val cb = list[position]
            with(holder.binding) {
                val context = holder.itemView.context
                val fileName = cb.hinhAnh

                val nameWithoutExtension = fileName.substringBeforeLast(".", fileName)
                val resId = context.resources.getIdentifier(nameWithoutExtension, "drawable", context.packageName)

                if (resId != 0) {
                    Log.d("IMAGE_LOAD", "Load từ drawable: $fileName, resId=$resId")
                    hinhChuyenBay.setImageResource(resId)
                } else {
                    val imageFile = File(context.filesDir, "anhchuyenbay/$fileName")

                    if (imageFile.exists()) {
                        Log.d("IMAGE_LOAD", "Load từ internal storage: ${imageFile.absolutePath}")
                    } else {
                        Log.e("IMAGE_LOAD", "KHÔNG tìm thấy file: ${imageFile.absolutePath}")
                    }

                    Glide.with(context)
                        .load(imageFile)
                        .placeholder(R.drawable.background_airplane)
                        .error(R.drawable.background_airplane)
                        .into(hinhChuyenBay)
                }


                tenChuyenBay.text = "${cb.tu} → ${cb.den}"
                NgayDi.text = cb.ngayDi
                NgayVe.text = cb.ngayVe
                val formatter = DecimalFormat("#,###")
                Gia.text = "${formatter.format(cb.giaVe)} VNĐ"

                btnXoa.setOnClickListener {
                    AlertDialog.Builder(this@AdminQuanLyChuyenBay)
                        .setTitle("Xác nhận xóa")
                        .setMessage("Bạn có chắc muốn xóa chuyến bay này?")
                        .setPositiveButton("Xóa") { _, _ ->
                            val realPosition = holder.adapterPosition
                            if (realPosition != RecyclerView.NO_POSITION) {
                                val item = list[realPosition]
                                val success = dbHelper.deleteChuyenBay(item.id)
                                if (success) {
                                    list.removeAt(realPosition)
                                    notifyItemRemoved(realPosition)
                                    Toast.makeText(this@AdminQuanLyChuyenBay, "Đã xóa chuyến bay", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this@AdminQuanLyChuyenBay, "Xóa thất bại", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        .setNegativeButton("Hủy", null)
                        .show()
                }

                btnSua.visibility = View.VISIBLE
                btnSua.setOnClickListener {
                    val intent = Intent(this@AdminQuanLyChuyenBay, EditChuyenBayActivity::class.java).apply {
                        putExtra("id", cb.id)
                        putExtra("tu", cb.tu)
                        putExtra("den", cb.den)
                        putExtra("ngayDi", cb.ngayDi)
                        putExtra("ngayVe", cb.ngayVe)
                        putExtra("giaVe", cb.giaVe)
                        putExtra("hinhAnh", cb.hinhAnh)
                    }
                    startActivity(intent)
                }

                val btnAddChuyenBay = findViewById<View>(R.id.btnAddChuyenBay)
                btnAddChuyenBay.setOnClickListener {
                    val intent = Intent(this@AdminQuanLyChuyenBay, AddChuyenBayActivity::class.java)
                    startActivity(intent)
                }

            }
        }

        override fun getItemCount(): Int = list.size
    }
}
