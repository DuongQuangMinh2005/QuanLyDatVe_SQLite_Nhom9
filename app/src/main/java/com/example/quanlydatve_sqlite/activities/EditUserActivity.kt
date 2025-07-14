package com.example.quanlydatve_sqlite.activities

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quanlydatve_sqlite.networks.DatabaseHelper
import com.example.quanlydatve_sqlite.R
import java.text.SimpleDateFormat
import java.util.*

class EditUserActivity : AppCompatActivity() {

    private lateinit var edtHoTen: EditText
    private lateinit var edtNgaySinh: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtMatKhau: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tenDangNhap: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        edtHoTen = findViewById(R.id.edtHoTen)
        edtNgaySinh = findViewById(R.id.edtNgaySinh)
        edtEmail = findViewById(R.id.edtEmail)
        edtPhone = findViewById(R.id.edtPhone)
        edtMatKhau = findViewById(R.id.edtMatKhau)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancle)

        dbHelper = DatabaseHelper(this)
        tenDangNhap = intent.getStringExtra("tenDangNhap") ?: return

        val user = dbHelper.getNguoiDung(tenDangNhap)
        if (user != null) {
            edtHoTen.setText(user.hoTen)
            edtNgaySinh.setText(user.ngaySinh)
            edtEmail.setText(user.email)
            edtPhone.setText(user.phone)
            edtMatKhau.setText(user.matKhau)
        }

        applyButtonTouchAnimation(btnSave)
        applyButtonTouchAnimation(btnCancel)

        btnSave.setOnClickListener {
            val hoTen = edtHoTen.text.toString().trim()
            val ngaySinh = edtNgaySinh.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val phone = edtPhone.text.toString().trim()
            val matKhau = edtMatKhau.text.toString().trim()

            if (hoTen.isEmpty() || ngaySinh.isEmpty() || matKhau.isEmpty()) {
                showToast("Vui lòng nhập đầy đủ họ tên, ngày sinh và mật khẩu!")
                return@setOnClickListener
            }

            if (!isValidDate(ngaySinh)) {
                showToast("Ngày sinh không hợp lệ! Định dạng dd/MM/yyyy")
                return@setOnClickListener
            }

            if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast("Email không hợp lệ!")
                return@setOnClickListener
            }

            if (phone.isNotEmpty() && phone.length < 9) {
                showToast("Số điện thoại không hợp lệ!")
                return@setOnClickListener
            }

            val success = dbHelper.updateNguoiDung(
                tenDangNhap, hoTen, ngaySinh, email, phone, matKhau
            )
            Log.d("DEBUG_UPDATE_USER", "Update thành công: $success")

            if (success) {
                showToast("Cập nhật thành công!")
                finish()
            } else {
                showToast("Cập nhật thất bại!")
            }
        }

        btnCancel.setOnClickListener {
            finish()
        }

        val rootView = findViewById<View>(R.id.scrollView) ?: findViewById(android.R.id.content)
        val anim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        rootView.startAnimation(anim)
    }

    private fun applyButtonTouchAnimation(button: Button) {
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(1.05f).scaleY(1.05f).setDuration(100).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                }
            }
            false
        }
    }

    private fun isValidDate(date: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            currentFocus!!.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }
}
