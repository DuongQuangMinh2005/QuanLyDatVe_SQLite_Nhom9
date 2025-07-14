package com.example.quanlydatve_sqlite.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlydatve_sqlite.adapters.ChuyenBayAdapter
import com.example.quanlydatve_sqlite.adapters.DanhSachChuyenBayNoiBatAdapter
import com.example.quanlydatve_sqlite.networks.DatabaseHelper
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.users.DatVeActivity
import com.example.quanlydatve_sqlite.utils.SharedPrefHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class DanhSachChuyenBayPhuHopActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var txtTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_danh_sach_chuyen_bay)

        dbHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerViewChuyenBay)
        txtTitle = findViewById(R.id.txtTitleDanhSach)

        val mode = intent.getStringExtra("MODE") ?: "PHU_HOP"
        val tu = intent.getStringExtra("EXTRA_TU")?.trim() ?: ""
        val den = intent.getStringExtra("EXTRA_DEN")?.trim() ?: ""
        val ngayDi = intent.getStringExtra("EXTRA_NGAY_DI")?.trim() ?: ""
        val ngayVe = intent.getStringExtra("EXTRA_NGAY_VE")?.trim() ?: ""

        if (mode == "PHU_HOP") {
            txtTitle.text = "Chuyến bay\n$tu đến $den"
            recyclerView.layoutManager = LinearLayoutManager(this)
            fetchChuyenBaysPhuHop(tu, den, ngayDi, ngayVe)
        } else {
            txtTitle.text = "Thông tin chuyến bay\n$tu đến $den"
            recyclerView.layoutManager = GridLayoutManager(this, 2)
            fetchThongTinChuyenBay(tu, den)
        }
    }

    private fun fetchChuyenBaysPhuHop(tu: String, den: String, ngayDi: String, ngayVe: String) {
        MainScope().launch(Dispatchers.IO) {
            try {
                val danhSach = dbHelper.getChuyenBays(tu, den, ngayDi, ngayVe)
                launch(Dispatchers.Main) {
                    if (danhSach.isEmpty()) {
                        Toast.makeText(
                            this@DanhSachChuyenBayPhuHopActivity,
                            "Không tìm thấy chuyến bay phù hợp",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val adapter = ChuyenBayAdapter(danhSach) { chuyenBay ->
                            val role = SharedPrefHelper.getRole(this@DanhSachChuyenBayPhuHopActivity)
                            when {
                                role == null -> {
                                    Toast.makeText(this@DanhSachChuyenBayPhuHopActivity, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show()
                                }
                                role == "admin" -> {
                                    Toast.makeText(this@DanhSachChuyenBayPhuHopActivity, "Tài khoản admin không thể đặt vé!", Toast.LENGTH_SHORT).show()
                                }
                                role == "user" -> {
                                    val intent = Intent(this@DanhSachChuyenBayPhuHopActivity, DatVeActivity::class.java).apply {
                                        putExtra("EXTRA_TU", chuyenBay.tu)
                                        putExtra("EXTRA_DEN", chuyenBay.den)
                                        putExtra("EXTRA_NGAY_DI", chuyenBay.ngayDi)
                                        putExtra("EXTRA_NGAY_VE", chuyenBay.ngayVe)
                                        putExtra("EXTRA_GIA", chuyenBay.giaVe)
                                    }
                                    startActivity(intent)
                                }
                                else -> {
                                    Toast.makeText(this@DanhSachChuyenBayPhuHopActivity, "Không xác định quyền truy cập!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        recyclerView.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    Toast.makeText(this@DanhSachChuyenBayPhuHopActivity, "Lỗi khi tải dữ liệu: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun fetchThongTinChuyenBay(tu: String, den: String) {
        MainScope().launch(Dispatchers.IO) {
            try {
                val danhSach = dbHelper.getAllChuyenBay(tu, den, "", "")
                launch(Dispatchers.Main) {
                    if (danhSach.isEmpty()) {
                        Toast.makeText(
                            this@DanhSachChuyenBayPhuHopActivity,
                            "Không có chuyến bay nào từ $tu đến $den!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // KHÔNG truyền infinite (hoặc truyền false)
                        val adapter = DanhSachChuyenBayNoiBatAdapter(danhSach, { flight ->
                            val giaDouble = parseGiaStringToDouble(flight.gia)
                            val intent = Intent(this@DanhSachChuyenBayPhuHopActivity, DatVeActivity::class.java).apply {
                                putExtra("EXTRA_CHUYENBAY_ID", flight.id)
                                putExtra("EXTRA_TU", tu)
                                putExtra("EXTRA_DEN", den)
                                putExtra("EXTRA_NGAY_DI", flight.ngayDi)
                                putExtra("EXTRA_NGAY_VE", flight.ngayVe)
                                putExtra("EXTRA_GIA", giaDouble)
                                putExtra("EXTRA_HINHANH", flight.hinhAnhResId)
                            }
                            startActivity(intent)
                        }, infinite = false)
                        recyclerView.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    Toast.makeText(this@DanhSachChuyenBayPhuHopActivity, "Lỗi khi tải dữ liệu: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun parseGiaStringToDouble(giaStr: String): Double {
        val cleanStr = giaStr.replace("[^\\d]".toRegex(), "")
        return cleanStr.toDoubleOrNull() ?: 0.0
    }

    private fun formatGiaVND(giaInt: Int): String {
        val nf = NumberFormat.getInstance(Locale("vi", "VN"))
        return nf.format(giaInt) + " VNĐ"
    }
}
