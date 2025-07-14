package com.example.quanlydatve_sqlite.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.quanlydatve_sqlite.R

class BeginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_begin)

        val btnRegister = findViewById<Button>(R.id.btn_register)
        val btnSignIn = findViewById<Button>(R.id.btn_sign_in)

        btnRegister.setOnClickListener {
            val intent = Intent(this, DangKyActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.fade_out)
        }

        btnSignIn.setOnClickListener {
            val intent = Intent(this, DangNhapActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.fade_out)
        }
    }
}