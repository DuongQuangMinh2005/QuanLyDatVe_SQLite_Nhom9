package com.example.quanlydatve_sqlite.models

data class ChuyenBayYeuThich(
    val id: Int,
    val userID: Int,
    val chuyenBayID: Int,
    val ngayDi: String,
    val ngayVe: String,
    val tu: String,
    val den: String,
    val hinhAnh: String,
    val giaVe: Double,
    val thoiGianYeuThich: Long?
)
