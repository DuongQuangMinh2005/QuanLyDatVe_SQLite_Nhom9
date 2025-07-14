package com.example.quanlydatve_sqlite.users

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quanlydatve_sqlite.models.Booking
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.activities.MainActivity
import com.example.quanlydatve_sqlite.utils.FormatDate

class HoaDonBooking : AppCompatActivity() {

    private fun generateInvoiceHtml(booking: Booking): String {
        Log.d("HoaDonBooking", "booking.ngayDi = ${booking.ngayDi}, booking.ngayVe = ${booking.ngayVe}")
        return """
        <html>
        <head>
            <style>
                body { font-family: Arial, sans-serif; color: #212121; }
                .invoice-container { padding: 12px; border-radius: 12px; background: #f5f5f5; }
                .invoice-title { font-size: 20px; font-weight: bold; color: #1976D2; margin-bottom: 8px; }
                .invoice-row { margin-bottom: 6px; }
                .highlight { color: #D32F2F; font-size: 18px; }
            </style>
        </head>
        <body>
            <div class="invoice-container">
                <div class="invoice-title">✈️ Chi tiết hóa đơn đặt vé</div>
                <div class="invoice-row">📍 <b>Từ:</b> ${booking.tu}</div>
                <div class="invoice-row">📍 <b>Đến:</b> ${booking.den}</div>
                <div class="invoice-row">🗓️ <b>Ngày đi:</b> ${FormatDate.formatNgay(booking.ngayDi)}</div>
                <div class="invoice-row">🗓️ <b>Ngày về:</b> ${FormatDate.formatNgay(booking.ngayVe)}</div>
                <div class="invoice-row">🎟️ <b>Loại vé:</b> ${booking.ticketType}</div>
                <div class="invoice-row">🔢 <b>Số lượng:</b> ${booking.quantity}</div>
                <div class="invoice-row">💳 <b>Thanh toán:</b> ${booking.paymentMethod}</div>
                <hr style="margin-top:20px; margin-bottom:16px; border: 0.5px solid #888888;">
                <div class="invoice-row highlight"><b>💰 Tổng tiền: </b> ${"%,.0f VNĐ".format(booking.totalPrice).replace(',', '.')}</div>
            </div>
        </body>
        </html>
        """.trimIndent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hoa_don)

        val btnTrangChu = findViewById<Button>(R.id.btnTrangchu)
        val btnDownload = findViewById<Button>(R.id.btnDownload)
        val invoiceWebView = findViewById<WebView>(R.id.invoiceText)

        val booking = intent.getSerializableExtra("BOOKING") as? Booking

        if (booking == null) {
            Toast.makeText(this, "Không nhận được dữ liệu đặt vé", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        invoiceWebView.settings.javaScriptEnabled = false
        invoiceWebView.setBackgroundColor(0x00000000)
        invoiceWebView.loadDataWithBaseURL(null, generateInvoiceHtml(booking), "text/html", "utf-8", null)

        btnTrangChu.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            })
            finish()
        }

        btnDownload.setOnClickListener {
            Toast.makeText(this, "Tải xuống đang được phát triển", Toast.LENGTH_SHORT).show()
        }
    }
}
