package com.example.quanlydatve_sqlite.admin

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MotionEvent
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.quanlydatve_sqlite.models.ChuyenBay
import com.example.quanlydatve_sqlite.networks.DatabaseHelper
import com.example.quanlydatve_sqlite.R
import java.util.*

class EditChuyenBayActivity : AppCompatActivity() {

    private lateinit var edtTu: EditText
    private lateinit var edtDen: EditText
    private lateinit var edtNgayDi: EditText
    private lateinit var edtNgayVe: EditText
    private lateinit var edtGiaVe: EditText
    private lateinit var imgHinh: ImageView
    private lateinit var btnSave: Button

    private lateinit var dbHelper: DatabaseHelper
    private var idChuyenBay: Int = -1
    private var hinhAnh: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_chuyen_bay)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        dbHelper = DatabaseHelper(this)
        edtTu = findViewById(R.id.diemDi)
        edtDen = findViewById(R.id.diemDen)
        edtNgayDi = findViewById(R.id.ngayDi)
        edtNgayVe = findViewById(R.id.ngayVe)
        edtGiaVe = findViewById(R.id.giaVe)
        imgHinh = findViewById(R.id.hinhChuyenBay)
        btnSave = findViewById(R.id.btnSave)

        edtTu.isEnabled = false
        edtDen.isEnabled = false

        intent?.let {
            idChuyenBay = it.getIntExtra("id", -1)
            edtTu.setText(it.getStringExtra("tu"))
            edtDen.setText(it.getStringExtra("den"))
            edtNgayDi.setText(it.getStringExtra("ngayDi"))
            edtNgayVe.setText(it.getStringExtra("ngayVe"))
            edtGiaVe.setText(it.getDoubleExtra("giaVe", 0.0).toString())
            hinhAnh = it.getStringExtra("hinhAnh") ?: ""
            loadImage(hinhAnh)
        }

        edtNgayDi.setOnClickListener { showDatePicker(edtNgayDi) }
        edtNgayVe.setOnClickListener { showDatePicker(edtNgayVe) }

        btnSave.setOnClickListener {
            val ngayDi = edtNgayDi.text.toString().trim()
            val ngayVe = edtNgayVe.text.toString().trim()
            val giaVe = edtGiaVe.text.toString().trim().toDoubleOrNull()

            if (ngayDi.isEmpty() || ngayVe.isEmpty() || giaVe == null) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin hợp lệ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updated = ChuyenBay(
                id = idChuyenBay,
                tu = edtTu.text.toString(),
                den = edtDen.text.toString(),
                ngayDi = ngayDi,
                ngayVe = ngayVe,
                giaVe = giaVe,
                hinhAnh = hinhAnh
            )

            val success = dbHelper.updateChuyenBay(updated)
            if (success) {
                Toast.makeText(this, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
            }
        }

        applyButtonTouchAnimation(btnSave)

        val rootView = findViewById<ScrollView>(R.id.scrollView)
        val anim = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        rootView.startAnimation(anim)
    }

    private fun loadImage(hinh: String) {
        if (hinh.startsWith("http")) {
            Glide.with(this).load(hinh).into(imgHinh)
        } else {
            val resId = resources.getIdentifier(hinh, "drawable", packageName)
            imgHinh.setImageResource(if (resId != 0) resId else R.drawable.user_avatar)
        }
    }

    private fun applyButtonTouchAnimation(button: Button) {
        button.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> v.animate().scaleX(1.05f).scaleY(1.05f).duration = 100
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> v.animate().scaleX(1f).scaleY(1f).duration = 100
            }
            false
        }
    }

    private fun showDatePicker(targetEditText: EditText) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                targetEditText.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
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
