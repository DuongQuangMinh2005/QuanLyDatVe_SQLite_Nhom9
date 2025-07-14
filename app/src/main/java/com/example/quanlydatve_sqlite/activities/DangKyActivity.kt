package com.example.quanlydatve_sqlite.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quanlydatve_sqlite.networks.DatabaseHelper
import com.example.quanlydatve_sqlite.R
import java.text.SimpleDateFormat
import java.util.*

class DangKyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dang_ky)

        val edtUsername = findViewById<EditText>(R.id.edt_username)
        val edtPassword = findViewById<EditText>(R.id.edt_password)
        val edtHoTen = findViewById<EditText>(R.id.edtHoTen)
        val edtNgaySinh = findViewById<EditText>(R.id.edtNgaySinh)
        val edtEmail = findViewById<EditText>(R.id.edt_email)
        val edtPhone = findViewById<EditText>(R.id.edt_phone)
        val btnDangKy = findViewById<Button>(R.id.btn_dang_ky)

        edtNgaySinh.setOnClickListener {
            showDatePicker(edtNgaySinh)
        }
        edtNgaySinh.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) showDatePicker(edtNgaySinh)
        }

        btnDangKy.setOnClickListener {
            val username = edtUsername.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            val hoTen = edtHoTen.text.toString().trim()
            val ngaySinh = edtNgaySinh.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val phone = edtPhone.text.toString().trim()

            if (username.isEmpty() || password.isEmpty() || hoTen.isEmpty() || ngaySinh.isEmpty() ||email.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin bắt buộc", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = DatabaseHelper(this)
            val success = db.dangKyNguoiDung(username, password, hoTen, ngaySinh, email, phone, "user")

            if (success) {
                Toast.makeText(this, "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Tên đăng nhập đã tồn tại hoặc lỗi đăng ký.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val currentDate = editText.text.toString().let {
            try {
                sdf.parse(it)
            } catch (e: Exception) { null }
        }
        currentDate?.let { calendar.time = it }

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                editText.setText(sdf.format(selectedCalendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
        datePickerDialog.show()
    }
}
