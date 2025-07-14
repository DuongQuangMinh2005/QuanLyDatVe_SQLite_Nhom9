package com.example.quanlydatve_sqlite.users

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlydatve_sqlite.models.Booking
import com.example.quanlydatve_sqlite.adapters.BookingAdapter
import com.example.quanlydatve_sqlite.networks.DatabaseHelper
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.activities.MainActivity

class LichSuDatVeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BookingAdapter
    private lateinit var databaseHelper: DatabaseHelper
    private val bookingList = mutableListOf<Booking>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_danh_sach_ve)

        databaseHelper = DatabaseHelper(this)

        recyclerView = findViewById(R.id.recyclerViewLichSu) // ✅ gọi trước
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = BookingAdapter(bookingList, this, databaseHelper)
        recyclerView.adapter = adapter

        loadLichSu()

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val btnTrangChu = findViewById<Button>(R.id.btnTrangChu)
        btnTrangChu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    private fun loadLichSu() {
        val db = databaseHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Booking ORDER BY id DESC", null)
        bookingList.clear()

        if (cursor.moveToFirst()) {
            do {
                val booking = Booking(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    tu = cursor.getString(cursor.getColumnIndexOrThrow("tu")),
                    den = cursor.getString(cursor.getColumnIndexOrThrow("den")),
                    ngayDi = cursor.getString(cursor.getColumnIndexOrThrow("ngayDi")),
                    ngayVe = cursor.getString(cursor.getColumnIndexOrThrow("ngayVe")),
                    quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity")),
                    ticketType = cursor.getString(cursor.getColumnIndexOrThrow("ticketType")),
                    totalPrice = cursor.getDouble(cursor.getColumnIndexOrThrow("totalPrice")),
                    paymentMethod = cursor.getString(cursor.getColumnIndexOrThrow("paymentMethod"))
                )
                bookingList.add(booking)
            } while (cursor.moveToNext())
        } else {
            Toast.makeText(this, "Không có lịch sử đặt vé!", Toast.LENGTH_SHORT).show()
        }

        cursor.close()
        db.close()
        adapter.notifyDataSetChanged()
    }
}
