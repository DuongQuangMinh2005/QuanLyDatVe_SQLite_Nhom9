package com.example.quanlydatve_sqlite.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quanlydatve_sqlite.admin.AdminQuanLyChuyenBay
import com.example.quanlydatve_sqlite.networks.DatabaseHelper
import com.example.quanlydatve_sqlite.models.NguoiDung
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.utils.SharedPrefHelper

class ProfileActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var btnEdit: Button
    private lateinit var btnQuanLyChuyenBay: Button
    private lateinit var btnLogout: Button

    private var nguoiDung: NguoiDung? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        btnLogout = findViewById(R.id.btnLogout)

        dbHelper = DatabaseHelper(this)

        btnQuanLyChuyenBay = findViewById(R.id.btnQuanLyChuyenBay)
        btnEdit = findViewById(R.id.btnEdit)

        loadUserData()

        btnEdit.setOnClickListener {
            if (nguoiDung != null) {
                val intent = Intent(this, EditUserActivity::class.java)
                intent.putExtra("tenDangNhap", nguoiDung!!.tenDangNhap)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Không thể chỉnh sửa nếu chưa đăng nhập!", Toast.LENGTH_SHORT).show()
            }
        }

        btnQuanLyChuyenBay.setOnClickListener {
            val role = SharedPrefHelper.getRole(this)
            when {
                role == "admin" -> {
                    val intent = Intent(this, AdminQuanLyChuyenBay::class.java)
                    startActivity(intent)
                }
                role == null -> {
                    Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, "Chỉ tài khoản admin mới được truy cập chức năng này.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnLogout.setOnClickListener {
            SharedPrefHelper.clearLoginSession(this)

            val intent = Intent(this, DangNhapActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            Toast.makeText(this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onResume() {
        super.onResume()
        loadUserData()
    }

    private fun loadUserData() {
        val username = SharedPrefHelper.getUsername(this)
        Log.d("ProfileActivity", "username: $username")

        if (username.isNullOrEmpty()) {
            Toast.makeText(this, "Chưa đăng nhập!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, DangNhapActivity::class.java))
            finish()
            return
        }

        nguoiDung = dbHelper.getUserByUsername(username)
        if (nguoiDung != null) {
            findViewById<TextView>(R.id.tv_name)?.text = nguoiDung?.hoTen ?: "N/A"
            findViewById<TextView>(R.id.tv_email)?.text = nguoiDung?.email ?: "N/A"
            findViewById<TextView>(R.id.tv_sodienthoai)?.text = nguoiDung?.phone ?: "N/A"
            findViewById<TextView>(R.id.tv_ngaysinh)?.text = nguoiDung?.ngaySinh ?: "N/A"
            findViewById<TextView>(R.id.tv_matkhau)?.text = "*****"
        } else {
            Toast.makeText(this, "Không tìm thấy người dùng!", Toast.LENGTH_SHORT).show()
        }
    }

}
