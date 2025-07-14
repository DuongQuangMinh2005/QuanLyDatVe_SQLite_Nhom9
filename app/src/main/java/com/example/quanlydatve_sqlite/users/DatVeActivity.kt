package com.example.quanlydatve_sqlite.users

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.quanlydatve_sqlite.models.BookingRequest
import com.example.quanlydatve_sqlite.models.ChuyenBay
import com.example.quanlydatve_sqlite.networks.DatabaseHelper
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.customview.ThanhToanBottomSheet
import com.example.quanlydatve_sqlite.utils.FormatDate
import com.example.quanlydatve_sqlite.utils.SharedPrefHelper
import com.google.android.material.textfield.TextInputEditText

class DatVeActivity : AppCompatActivity() {
    private lateinit var flightRoute: TextView
    private lateinit var flightDates: TextView
    private lateinit var ticketRoute: TextView
    private lateinit var ticketDates: TextView
    private lateinit var txtGiaTien: TextView
    private lateinit var txtTax: TextView
    private lateinit var totalPrice: TextView
    private lateinit var editTextQuantity: TextInputEditText
    private lateinit var btnIncrease: ImageButton
    private lateinit var btnDecrease: ImageButton
    private lateinit var radioGroupSeatType: RadioGroup
    private lateinit var paymentGroup: RadioGroup
    private lateinit var btnDatVe: Button
    private lateinit var icLike: ImageView

    private var basePrice: Double = 0.0
    private var basePriceEffective: Double = 0.0
    private var quantity: Int = 1
    private val VAT_PERCENT = 10
    private var chuyenBayId: Int = -1

    private lateinit var dbHelper: DatabaseHelper
    private var isLiked: Boolean = false
    private lateinit var chuyenBay: ChuyenBay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dat_ve)

        // Ánh xạ view
        flightRoute = findViewById(R.id.flightRoute)
        flightDates = findViewById(R.id.flightDates)
        ticketRoute = findViewById(R.id.ticketRoute)
        ticketDates = findViewById(R.id.ticketDates)
        txtGiaTien = findViewById(R.id.txtGiaTien)
        txtTax = findViewById(R.id.txtTax)
        totalPrice = findViewById(R.id.totalPrice)
        editTextQuantity = findViewById(R.id.editTextQuantity)
        btnIncrease = findViewById(R.id.btnIncrease)
        btnDecrease = findViewById(R.id.btnDecrease)
        radioGroupSeatType = findViewById(R.id.radioGroup_seatType)
        paymentGroup = findViewById(R.id.paymentMethodGroup)
        btnDatVe = findViewById(R.id.btnDatVe)
        icLike = findViewById(R.id.icLike)

        dbHelper = DatabaseHelper(this)

        // Lấy thông tin chuyến bay từ Intent
        chuyenBayId = intent.getIntExtra("EXTRA_CHUYENBAY_ID", -1)
        val tu = intent.getStringExtra("EXTRA_TU") ?: ""
        val den = intent.getStringExtra("EXTRA_DEN") ?: ""
        val ngayDi = intent.getStringExtra("EXTRA_NGAY_DI") ?: ""
        val ngayVe = intent.getStringExtra("EXTRA_NGAY_VE") ?: ""
        basePrice = intent.getDoubleExtra("EXTRA_GIA", 0.0)
        basePriceEffective = basePrice

        chuyenBay = dbHelper.getChuyenBayById(chuyenBayId)
            ?: ChuyenBay(
                id = chuyenBayId,
                tu = tu,
                den = den,
                ngayDi = ngayDi,
                ngayVe = ngayVe,
                giaVe = basePrice,
                hinhAnh = "",
                yeuThich = false
            )

        // Lấy userId để kiểm tra yêu thích
        val userID = SharedPrefHelper.getUserId(this) ?: -1
        isLiked = if (userID != -1) dbHelper.isChuyenBayYeuThich(userID, chuyenBayId) else false
        updateLikeIcon(isLiked)

        // Xử lý nút yêu thích (LIKE/UNLIKE)
        icLike.setOnClickListener {
            val role = SharedPrefHelper.getRole(this)
            val userIDClick = SharedPrefHelper.getUserId(this)

            if (role != "user" || userIDClick == null) {
                Toast.makeText(
                    this,
                    when (role) {
                        null -> "Bạn cần đăng nhập bằng tài khoản người dùng để sử dụng chức năng này!"
                        "admin" -> "Chỉ tài khoản người dùng mới được thêm vào danh sách yêu thích!"
                        else -> "Chỉ tài khoản người dùng mới được sử dụng chức năng này!"
                    },
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            val isCurrentlyLiked = dbHelper.isChuyenBayYeuThich(userIDClick, chuyenBay.id)
            if (isCurrentlyLiked) {
                dbHelper.xoaChuyenBayYeuThich(userIDClick, chuyenBay.id)
                updateLikeIcon(false)
                isLiked = false
                Toast.makeText(this, "Đã bỏ khỏi danh sách yêu thích!", Toast.LENGTH_SHORT).show()
            } else {
                dbHelper.themChuyenBayYeuThich(userIDClick, chuyenBay)
                updateLikeIcon(true)
                isLiked = true

                AlertDialog.Builder(this)
                    .setTitle("Thông báo")
                    .setMessage("Đã thêm vào danh sách yêu thích. Bạn có muốn chuyển đến danh sách chuyến bay yêu thích không?")
                    .setPositiveButton("Có") { _, _ ->
                        val intent = Intent(this, QuanLyChuyenBayYeuThich::class.java)
                        intent.putExtra("EXTRA_USER_ID", userIDClick)
                        startActivity(intent)
                    }
                    .setNegativeButton("Không", null)
                    .show()
            }
        }

        // Hiển thị ngày theo kiểu Việt Nam
        val ngayDiVN = FormatDate.formatNgay(ngayDi)
        val ngayVeVN = FormatDate.formatNgay(ngayVe)
        flightRoute.text = "Từ $tu đến $den"
        flightDates.text = "Ngày đi: $ngayDiVN - Ngày về: $ngayVeVN"
        ticketRoute.text = "Chuyến bay: $tu - $den"
        ticketDates.text = "Ngày: $ngayDiVN - $ngayVeVN"

        editTextQuantity.setText(quantity.toString())
        updatePriceDisplay()

        btnIncrease.setOnClickListener {
            quantity++
            editTextQuantity.setText(quantity.toString())
            updatePriceDisplay()
        }

        btnDecrease.setOnClickListener {
            if (quantity > 1) {
                quantity--
                editTextQuantity.setText(quantity.toString())
                updatePriceDisplay()
            }
        }

        editTextQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val qty = s.toString().toIntOrNull()
                if (qty != null && qty > 0) {
                    quantity = qty
                    updatePriceDisplay()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        radioGroupSeatType.setOnCheckedChangeListener { _, checkedId ->
            basePriceEffective = when (checkedId) {
                R.id.radio_pho_thong -> basePrice
                R.id.radio_pho_thong_dac_biet -> basePrice * 1.12
                R.id.radio_thuong_gia -> basePrice * 3.0
                else -> basePrice
            }
            updatePriceDisplay()
        }

        btnDatVe.setOnClickListener {
            val role = SharedPrefHelper.getRole(this)
            if (role == "admin") {
                Toast.makeText(this, "Bạn không phải user", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (quantity < 1) {
                Toast.makeText(this, "Số lượng vé phải lớn hơn 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (radioGroupSeatType.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Vui lòng chọn loại vé", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (paymentGroup.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bookingRequest = createBookingRequestObject(tu, den, ngayDi, ngayVe)
            Log.d("DEBUG_BOOKING", "bookingRequest gửi xuống: $bookingRequest")

            if (bookingRequest.ngayDi.isEmpty()) {
                Toast.makeText(this, "Ngày đi không hợp lệ!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (bookingRequest.ngayVe.isEmpty()) {
                Toast.makeText(this, "Ngày về không hợp lệ!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            insertBookingToDb(bookingRequest)
        }
    }

    private fun updateLikeIcon(liked: Boolean) {
        if (liked) {
            icLike.setImageResource(R.drawable.ic_like_filled)
            icLike.animate().scaleX(1.2f).scaleY(1.2f).setDuration(120).withEndAction {
                icLike.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
            }.start()
        } else {
            icLike.setImageResource(R.drawable.ic_like_outline)
            icLike.animate().scaleX(0.8f).scaleY(0.8f).setDuration(120).withEndAction {
                icLike.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
            }.start()
        }
    }

    private fun updatePriceDisplay() {
        val price = basePriceEffective * quantity
        val vat = price * VAT_PERCENT / 100
        val total = price + vat
        txtGiaTien.text = formatCurrency(price)
        txtTax.text = formatCurrency(vat)
        totalPrice.text = formatCurrency(total)
    }

    private fun formatCurrency(amount: Double): String {
        return "%,.0f VNĐ".format(amount).replace(',', '.')
    }

    private fun createBookingRequestObject(tu: String, den: String, ngayDi: String, ngayVe: String): BookingRequest {
        val seatType = when (radioGroupSeatType.checkedRadioButtonId) {
            R.id.radio_pho_thong -> "Phổ thông"
            R.id.radio_pho_thong_dac_biet -> "Phổ thông đặc biệt"
            R.id.radio_thuong_gia -> "Thương gia"
            else -> "Không rõ"
        }
        val paymentMethod = when (paymentGroup.checkedRadioButtonId) {
            R.id.radioButton_Chuyenkhoan -> "Chuyển khoản"
            R.id.radioButton_Quetthe -> "Quét thẻ"
            else -> "Không rõ"
        }
        val pricePerTicket = basePriceEffective
        val totalPriceBeforeTax = pricePerTicket * quantity
        val tax = totalPriceBeforeTax * VAT_PERCENT / 100
        val total = totalPriceBeforeTax + tax

        return BookingRequest(
            userId = getUserIdFromSharedPrefOrSession(),
            tu = tu,
            den = den,
            ngayDi = ngayDi,
            ngayVe = ngayVe,
            quantity = quantity,
            ticketType = seatType,
            price = pricePerTicket,
            tax = tax,
            totalPrice = total,
            paymentMethod = paymentMethod
        )
    }

    private fun insertBookingToDb(bookingRequest: BookingRequest) {
        if (bookingRequest.paymentMethod == "Quét thẻ") {
            AlertDialog.Builder(this@DatVeActivity)
                .setTitle("Thông báo")
                .setMessage("Chức năng quét thẻ hiện đang được phát triển. Đơn đặt vé của bạn chưa được lưu.")
                .setPositiveButton("OK") { _, _ -> finish() }
                .setCancelable(false)
                .show()
            return
        }
        try {
            val bookingId = dbHelper.insertBooking(bookingRequest)
            if (bookingId > 0) {
                val booking = dbHelper.getBookingById(bookingId.toInt())
                if (bookingRequest.paymentMethod == "Chuyển khoản" && booking != null) {
                    val sheet = ThanhToanBottomSheet(booking)
                    sheet.show(supportFragmentManager, "ThanhToanBottomSheet")
                } else {
                    Toast.makeText(
                        this@DatVeActivity,
                        "Bạn đã đặt vé thành công!",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }
            } else {
                Log.e("DatVeActivity", "insertBooking thất bại! bookingRequest: $bookingRequest")
                Toast.makeText(
                    this@DatVeActivity,
                    "Đặt vé thất bại. Dữ liệu booking không hợp lệ hoặc lỗi database.",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Log.e("DatVeActivity", "Lỗi khi insertBooking: ${e.message}", e)
            Toast.makeText(
                this@DatVeActivity,
                "Lỗi hệ thống khi lưu booking: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun getUserIdFromSharedPrefOrSession(): Int {
        val userId = SharedPrefHelper.getUserId(this)
        return userId ?: -1
    }
}
