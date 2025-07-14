package com.example.quanlydatve_sqlite.models

import com.example.quanlydatve_sqlite.R


data class ChuyenBay(
    val id: Int = 0,
    val ngayDi: String,
    val ngayVe: String,
    val tu: String,
    val den: String,
    val hinhAnh: String,
    val giaVe:Double,
    val yeuThich: Boolean = false,
    val thoiGianYeuThich: Long? = null

) {
    fun getHinhAnhResId(): Int {
        return when (hinhAnh) {
            "hanoi" -> R.drawable.hanoi
            "phuquoc" -> R.drawable.phuquoc
            "danang" -> R.drawable.danang
            else -> R.drawable.hanoi
        }
    }
}