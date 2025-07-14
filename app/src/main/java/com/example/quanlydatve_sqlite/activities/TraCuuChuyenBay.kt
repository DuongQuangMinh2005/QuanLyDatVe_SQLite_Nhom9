package com.example.quanlydatve_sqlite.activities

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quanlydatve_sqlite.R
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TraCuuChuyenBay : AppCompatActivity() {

    private lateinit var edtTu: MaterialAutoCompleteTextView
    private lateinit var edtDen: MaterialAutoCompleteTextView
    private lateinit var ediNgayDi: EditText
    private lateinit var ediNgayVe: EditText
    private lateinit var btnTimKiem: Button

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tra_cuu)

        edtTu = findViewById(R.id.edtTu)
        edtDen = findViewById(R.id.edtDen)
        ediNgayDi = findViewById(R.id.ediNgayDi)
        ediNgayVe = findViewById(R.id.ediNgayVe)
        btnTimKiem = findViewById(R.id.btnTimKiem)

        setupAutoComplete()
        setupDatePicker()

        btnTimKiem.setOnClickListener {
            val tu = edtTu.text.toString().trim()
            val den = edtDen.text.toString().trim()
            val ngayDi = ediNgayDi.text.toString().trim()
            val ngayVe = ediNgayVe.text.toString().trim()

            if (tu.isEmpty() || den.isEmpty() || ngayDi.isEmpty() || ngayVe.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = Intent(this, DanhSachChuyenBayPhuHopActivity::class.java).apply {
                putExtra("MODE", "PHU_HOP")
                putExtra("EXTRA_TU", tu)
                putExtra("EXTRA_DEN", den)
                putExtra("EXTRA_NGAY_DI", ngayDi)
                putExtra("EXTRA_NGAY_VE", ngayVe)
            }
            startActivity(intent)

        }
    }

    private fun setupAutoComplete() {
        val diaDiem = arrayOf(
            "An Giang", "Bà Rịa - Vũng Tàu", "Bạc Liêu", "Bắc Giang", "Bắc Kạn", "Bắc Ninh",
            "Bến Tre", "Bình Dương", "Bình Định", "Bình Phước", "Bình Thuận", "Cà Mau",
            "Cao Bằng", "Cần Thơ", "Đà Nẵng", "Đắk Lắk", "Đắk Nông", "Điện Biên", "Đồng Nai",
            "Đồng Tháp", "Gia Lai", "Hà Giang", "Hà Nam", "Hà Nội", "Hà Tĩnh", "Hải Dương",
            "Hải Phòng", "Hậu Giang", "Hòa Bình", "Hưng Yên", "Khánh Hòa", "Kiên Giang",
            "Kon Tum", "Lai Châu", "Lạng Sơn", "Lào Cai", "Lâm Đồng", "Long An", "Nam Định",
            "Nghệ An", "Ninh Bình", "Ninh Thuận", "Phú Thọ", "Phú Yên", "Quảng Bình",
            "Quảng Nam", "Quảng Ngãi", "Quảng Ninh", "Quảng Trị", "Sóc Trăng", "Sơn La",
            "Tây Ninh", "Thái Bình", "Thái Nguyên", "Thanh Hóa", "Thừa Thiên Huế", "Tiền Giang",
            "TP. Hồ Chí Minh", "Trà Vinh", "Tuyên Quang", "Vĩnh Long", "Vĩnh Phúc", "Yên Bái", "Đà Lạt", "Phú Quốc"
        )

        val diaDiemAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, diaDiem)
        edtTu.setAdapter(diaDiemAdapter)
        edtDen.setAdapter(diaDiemAdapter)
    }

    private fun setupDatePicker() {
        ediNgayDi.setOnClickListener {
            showDatePicker { date ->
                ediNgayDi.setText(date)
            }
        }

        ediNgayVe.setOnClickListener {
            showDatePicker { date ->
                ediNgayVe.setText(date)
            }
        }
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDay)
            onDateSelected(dateFormat.format(selectedDate.time))
        }, year, month, day)
        datePickerDialog.show()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}