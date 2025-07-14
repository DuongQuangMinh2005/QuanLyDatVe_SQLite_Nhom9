package com.example.quanlydatve_sqlite.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quanlydatve_sqlite.networks.DatabaseHelper
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.utils.SharedPrefHelper

class DangNhapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dang_nhap)

        val edtUsername = findViewById<EditText>(R.id.edt_username)
        val edtPassword = findViewById<EditText>(R.id.edt_password)
        val btnDangNhap = findViewById<Button>(R.id.btn_dang_nhap)
        val btnDangKy = findViewById<TextView>(R.id.btn_dang_ky)
        val mainContent = findViewById<LinearLayout>(R.id.main_content)

        mainContent.alpha = 0f
        mainContent.animate()
            .alpha(1f)
            .setDuration(800)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        val fullText = "Chưa có tài khoản? Đăng ký"
        val spannableString = SpannableString(fullText)
        val startIndex = fullText.indexOf("Đăng ký")
        val endIndex = startIndex + "Đăng ký".length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(this@DangNhapActivity, "Đăng ký người dùng", Toast.LENGTH_SHORT).show()
                 val intent = Intent(this@DangNhapActivity, DangKyActivity::class.java)
                 startActivity(intent)
            }

            override fun updateDrawState(ds: android.text.TextPaint) {
                super.updateDrawState(ds)
                ds.color = Color.parseColor("#8B5CF6")
                ds.isUnderlineText = true
            }
        }

        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#8B5CF6")), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(UnderlineSpan(), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        btnDangKy.text = spannableString
        btnDangKy.movementMethod = LinkMovementMethod.getInstance()
        btnDangKy.highlightColor = Color.TRANSPARENT

        btnDangNhap.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                }
            }
            false
        }

        btnDangNhap.setOnClickListener {
            val username = edtUsername.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = DatabaseHelper(this)
            val user = db.getNguoiDungByTenDangNhap(username)
            if (user != null && user.matKhau == password) {
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

                SharedPrefHelper.saveLoginSession(this, user.id)

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()
            } else {
                Toast.makeText(this, "Tài khoản hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show()
            }
        }

    }
}
