package com.example.quanlydatve_sqlite.admin

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.quanlydatve_sqlite.models.ChuyenBay
import com.example.quanlydatve_sqlite.networks.DatabaseHelper
import com.example.quanlydatve_sqlite.R
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class AddChuyenBayActivity : AppCompatActivity() {

    private lateinit var imgHinh: ImageView
    private lateinit var btnAddImage: ImageButton
    private lateinit var edtTu: AutoCompleteTextView
    private lateinit var edtDen: AutoCompleteTextView
    private lateinit var edtNgayDi: EditText
    private lateinit var edtNgayVe: EditText
    private lateinit var edtGiaVe: EditText
    private lateinit var btnAdd: Button

    private var imageFileName: String = ""
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private lateinit var dbHelper: DatabaseHelper

    companion object {
        private const val REQUEST_IMAGE_PICK = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_add_chuyen_bay)

        imgHinh = findViewById(R.id.imgHinh)
        btnAddImage = findViewById(R.id.btnAddImage)
        edtTu = findViewById(R.id.diemDi)
        edtDen = findViewById(R.id.diemDen)
        edtNgayDi = findViewById(R.id.ngayDi)
        edtNgayVe = findViewById(R.id.ngayVe)
        edtGiaVe = findViewById(R.id.giaVe)
        btnAdd = findViewById(R.id.btnAdd)

        dbHelper = DatabaseHelper(this)

        val danhSachTinhThanh = listOf(
            "An Giang", "Bà Rịa - Vũng Tàu", "Bắc Giang", "Bắc Kạn", "Bạc Liêu", "Bắc Ninh", "Bến Tre",
            "Bình Định", "Bình Dương", "Bình Phước", "Bình Thuận", "Cà Mau", "Cần Thơ", "Cao Bằng",
            "Đà Nẵng", "Đắk Lắk", "Đắk Nông", "Điện Biên", "Đồng Nai", "Đồng Tháp", "Gia Lai", "Hà Giang",
            "Hà Nam", "Hà Nội", "Hà Tĩnh", "Hải Dương", "Hải Phòng", "Hậu Giang", "Hòa Bình", "Hưng Yên",
            "Khánh Hòa", "Kiên Giang", "Kon Tum", "Lai Châu", "Lâm Đồng", "Lạng Sơn", "Lào Cai",
            "Long An", "Nam Định", "Nghệ An", "Ninh Bình", "Ninh Thuận", "Phú Thọ", "Phú Yên", "Quảng Bình",
            "Quảng Nam", "Quảng Ngãi", "Quảng Ninh", "Quảng Trị", "Sóc Trăng", "Sơn La", "Tây Ninh",
            "Thái Bình", "Thái Nguyên", "Thanh Hóa", "Thừa Thiên Huế", "Tiền Giang", "TP. Hồ Chí Minh",
            "Trà Vinh", "Tuyên Quang", "Vĩnh Long", "Vĩnh Phúc", "Yên Bái"
        )

        val adapterTinh = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, danhSachTinhThanh)
        edtTu.setAdapter(adapterTinh)
        edtDen.setAdapter(adapterTinh)

        btnAddImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        edtNgayDi.setOnClickListener {
            showDatePicker { date -> edtNgayDi.setText(date) }
        }

        edtNgayVe.setOnClickListener {
            showDatePicker { date -> edtNgayVe.setText(date) }
        }

        btnAdd.setOnClickListener {
            val tu = edtTu.text.toString().trim()
            val den = edtDen.text.toString().trim()
            val ngayDi = edtNgayDi.text.toString().trim()
            val ngayVe = edtNgayVe.text.toString().trim()
            val giaVe = edtGiaVe.text.toString().trim().toDoubleOrNull()

            if (tu.isEmpty() || den.isEmpty() || ngayDi.isEmpty() || ngayVe.isEmpty() || giaVe == null) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin hợp lệ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val tenAnh = if (imageFileName.isNotEmpty()) imageFileName else "user_avatar"

            val chuyenBay = ChuyenBay(
                ngayDi = ngayDi,
                ngayVe = ngayVe,
                tu = tu,
                den = den,
                giaVe = giaVe,
                hinhAnh = tenAnh
            )

            val success = dbHelper.insertChuyenBay(chuyenBay)
            if (success) {
                Toast.makeText(this, "Thêm chuyến bay thành công", Toast.LENGTH_SHORT).show()
                val intent = Intent()
                setResult(RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showDatePicker(onDateSet: (String) -> Unit) {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, y, m, d ->
            calendar.set(y, m, d)
            onDateSet(dateFormat.format(calendar.time))
        }, year, month, day).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            val selectedImageUri: Uri? = data.data
            if (selectedImageUri != null) {
                showSaveImageDialog(selectedImageUri)
            }
        }
    }

    private fun showSaveImageDialog(uri: Uri) {
        val editText = EditText(this)
        editText.hint = "Nhập tên ảnh (vd: hanoi.jpg)"

        AlertDialog.Builder(this)
            .setTitle("Lưu ảnh")
            .setMessage("Nhập tên file ảnh (kết thúc bằng .jpg hoặc .png):")
            .setView(editText)
            .setPositiveButton("Lưu") { _, _ ->
                val fileName = editText.text.toString().trim()
                if (!fileName.endsWith(".jpg") && !fileName.endsWith(".png")) {
                    Toast.makeText(this, "Tên ảnh phải kết thúc bằng .jpg hoặc .png", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                val savedPath = saveImageToInternalStorage(this, uri, fileName)
                if (savedPath != null) {
                    imageFileName = fileName

                    Glide.with(this).load(savedPath).into(imgHinh)
                    Toast.makeText(this, "Lưu ảnh thành công", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Lưu ảnh thất bại", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun saveImageToInternalStorage(context: Context, uri: Uri, fileName: String): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val directory = File(context.filesDir, "anhchuyenbay")
            if (!directory.exists()) {
                directory.mkdirs()
            }

            val file = File(directory, fileName)
            if (file.exists()) file.delete()

            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
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
