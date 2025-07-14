package com.example.quanlydatve_sqlite.users

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.adapters.ChuyenBayYeuThichAdapter
import com.example.quanlydatve_sqlite.models.ChuyenBayYeuThich
import com.example.quanlydatve_sqlite.networks.DatabaseHelper
import com.example.quanlydatve_sqlite.utils.SharedPrefHelper

class QuanLyChuyenBayYeuThich : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var adapter: ChuyenBayYeuThichAdapter
    private var userId: Int = -1
    private var danhSachChuyenBay = mutableListOf<ChuyenBayYeuThich>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_like_chuyen_bay)

        userId = intent.getIntExtra("EXTRA_USER_ID", -1)
        if (userId == -1) {
            userId = SharedPrefHelper.getUserId(this) ?: -1
        }
        if (userId == -1) {
            Toast.makeText(this, "Không xác định được người dùng!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }


        dbHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerViewLikeChuyenBay)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ChuyenBayYeuThichAdapter(
            context = this,
            list = danhSachChuyenBay,
            onDelete = { item, position ->
                dbHelper.xoaChuyenBayYeuThich(userId, item.chuyenBayID)
                adapter.removeAt(position)
                Toast.makeText(this, "Đã xóa chuyến bay yêu thích", Toast.LENGTH_SHORT).show()
            },
            onClick = { item ->
                val intent = Intent(this, DatVeActivity::class.java)
                intent.putExtra("EXTRA_TU", item.tu)
                intent.putExtra("EXTRA_DEN", item.den)
                intent.putExtra("EXTRA_NGAY_DI", item.ngayDi)
                intent.putExtra("EXTRA_NGAY_VE", item.ngayVe)
                intent.putExtra("EXTRA_GIA", item.giaVe)
                intent.putExtra("EXTRA_HINHANH", item.hinhAnh)
                startActivity(intent)
            }
        )
        recyclerView.adapter = adapter

        findViewById<ImageView>(R.id.icBack).setOnClickListener { finish() }
        findViewById<ImageButton>(R.id.icReload).setOnClickListener {
            loadChuyenBayYeuThich()
            Toast.makeText(this, "Đã tải lại danh sách!", Toast.LENGTH_SHORT).show()
        }

        loadChuyenBayYeuThich()
    }

    override fun onResume() {
        super.onResume()
        loadChuyenBayYeuThich()
    }

    private fun loadChuyenBayYeuThich() {
        danhSachChuyenBay = dbHelper.getAllChuyenBayYeuThichTheoUser(userId).toMutableList()
        adapter.updateData(danhSachChuyenBay)
    }
}
