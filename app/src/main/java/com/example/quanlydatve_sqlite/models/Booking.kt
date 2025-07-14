package com.example.quanlydatve_sqlite.models

import java.io.Serializable

data class Booking(
    val id: Int = 0,
    val tu: String,
    val den: String,
    val ngayDi: String,
    val ngayVe: String,
    val quantity: Int,
    val ticketType: String,
    val totalPrice: Double,
    val paymentMethod: String,
    val tenNganHang: String? = null,
    val soTaiKhoan: String? = null,
    val noiDung: String? = null
) : Serializable
