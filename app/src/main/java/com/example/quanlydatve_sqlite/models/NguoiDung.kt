package com.example.quanlydatve_sqlite.models

data class NguoiDung(
    val id: Int,
    val tenDangNhap: String,
    val matKhau: String,
    val hoTen: String,
    val ngaySinh: String,
    val email: String?,
    val phone: String?,
    val role: String

)
