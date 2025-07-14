package com.example.quanlydatve_sqlite.activities

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlydatve_sqlite.adapters.DanhSachChuyenBayNoiBatAdapter
import com.example.quanlydatve_sqlite.networks.DatabaseHelper
import com.example.quanlydatve_sqlite.models.NguoiDung
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.customview.AutoScrollTextView
import com.example.quanlydatve_sqlite.users.LichSuDatVeActivity
import com.example.quanlydatve_sqlite.users.QuanLyChuyenBayYeuThich
import com.example.quanlydatve_sqlite.utils.SharedPrefHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DanhSachChuyenBayNoiBatAdapter
    private lateinit var dbHelper: DatabaseHelper
    private lateinit var tenNhanVienTextView: TextView
    private lateinit var vaiTroTextView: TextView

    // Auto-scroll logic
    private val autoScrollHandler = Handler(Looper.getMainLooper())
    private var autoScrollRunnable: Runnable? = null
    private var scrollSpeedPx = 10
    private var scrollIntervalMs = 15L
    private var isAutoScrolling = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)
        dbHelper.fixHinhAnhPath()

        recyclerView = findViewById(R.id.recyclerViewChuyenBay)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.addItemDecoration(HorizontalSpacingItemDecoration(24))

        tenNhanVienTextView = findViewById(R.id.tenAdmin)
        vaiTroTextView = findViewById(R.id.vaiTro)

        val newsText = findViewById<AutoScrollTextView>(R.id.news_text)
        newsText.isSelected = true
        newsText.text = "Tin tức cực HOT: Vé máy bay giá rẻ chỉ từ 999K • Đặt ngay kẻo lỡ !"
        newsText.startScroll(speed = 60f)

        val btnProfile = findViewById<Button>(R.id.btnProfile)
        btnProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        val nguoiDung = SharedPrefHelper.getCurrentUser(this)
        if (nguoiDung != null) {
            tenNhanVienTextView.text = nguoiDung.hoTen
            vaiTroTextView.text = nguoiDung.role
        } else {
            tenNhanVienTextView.text = "Chưa đăng nhập"
            vaiTroTextView.text = "Chưa rõ"
        }

        findViewById<AppCompatButton>(R.id.btn_flight_search).setOnClickListener {
            startActivity(Intent(this, TraCuuChuyenBay::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_like -> {
                    val role = SharedPrefHelper.getRole(this)
                    when {
                        role == null -> {
                            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show()
                        }
                        role == "user" -> {
                            val userId = SharedPrefHelper.getUserId(this)
                            if (userId == null) {
                                Toast.makeText(this, "Không xác định được người dùng!", Toast.LENGTH_SHORT).show()
                            } else {
                                val intent = Intent(this, QuanLyChuyenBayYeuThich::class.java)
                                intent.putExtra("EXTRA_USER_ID", userId)
                                startActivity(intent)
                            }
                            true
                        }

                        role == "admin" -> {
                            Toast.makeText(this, "Bạn không phải là user.", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this, "Không xác định quyền truy cập!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    true
                }
                R.id.nav_search -> {
                    startActivity(Intent(this, TraCuuChuyenBay::class.java))
                    true
                }
                R.id.nav_ticket -> {
                    val role = SharedPrefHelper.getRole(this)
                    when {
                        role == null -> {
                            Toast.makeText(this, "Bạn chưa đăng nhập!", Toast.LENGTH_SHORT).show()
                        }
                        role == "user" -> {
                            Toast.makeText(this, "Chuyển tới lịch sử đặt vé.", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, LichSuDatVeActivity::class.java))
                        }
                        role == "admin" -> {
                            Toast.makeText(this, "Bạn không phải là user.", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(this, "Không xác định quyền truy cập!", Toast.LENGTH_SHORT).show()
                        }
                    }
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }

        bottomNav.selectedItemId = R.id.nav_home

        // Khi chạm vào RecyclerView sẽ dừng auto-scroll, nhưng cho phép click vào item
        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                stopAutoScroll()
            }
            false
        }

        fetchFlights()
    }

    private fun fetchFlights() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val flights = dbHelper.getFeaturedChuyenBays()
                launch(Dispatchers.Main) {
                    if (flights.isEmpty()) {
                        Toast.makeText(
                            this@MainActivity,
                            "Không có chuyến bay nào!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        adapter = DanhSachChuyenBayNoiBatAdapter(flights, { selectedFlight ->
                            stopAutoScroll()
                            val intent = Intent(this@MainActivity, DanhSachChuyenBayPhuHopActivity::class.java).apply {
                                putExtra("MODE", "THONG_TIN")
                                putExtra("EXTRA_TU", extractTuFromTen(selectedFlight.ten))
                                putExtra("EXTRA_DEN", extractDenFromTen(selectedFlight.ten))
                            }
                            startActivity(intent)
                        }, true) // truyền true ở đây
                        recyclerView.adapter = adapter


                        recyclerView.post {
                            val mid = adapter.itemCount / 2
                            recyclerView.scrollToPosition(mid)
                            startAutoScroll()
                        }
                    }
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Lỗi tải dữ liệu: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                Log.e("MainActivity", "Lỗi khi tải dữ liệu: ${e.message}", e)
            }
        }
    }

    private fun startAutoScroll() {
        if (isAutoScrolling) return
        isAutoScrolling = true
        if (autoScrollRunnable == null) {
            autoScrollRunnable = object : Runnable {
                override fun run() {
                    if (!isAutoScrolling) return
                    recyclerView.smoothScrollBy(scrollSpeedPx, 0)
                    autoScrollHandler.postDelayed(this, scrollIntervalMs)
                }
            }
        }
        autoScrollHandler.post(autoScrollRunnable!!)
    }

    private fun stopAutoScroll() {
        isAutoScrolling = false
        autoScrollRunnable?.let { autoScrollHandler.removeCallbacks(it) }
    }

    override fun onDestroy() {
        stopAutoScroll()
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        if (!isAutoScrolling && recyclerView.adapter != null && recyclerView.adapter!!.itemCount > 0) {
            startAutoScroll()
        }
    }

    private fun extractTuFromTen(ten: String): String {
        return ten.split("→").getOrNull(0)?.trim() ?: ""
    }

    private fun extractDenFromTen(ten: String): String {
        return ten.split("→").getOrNull(1)?.trim() ?: ""
    }

    class HorizontalSpacingItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: android.view.View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.right = space
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.left = space
            }
        }
    }
}
