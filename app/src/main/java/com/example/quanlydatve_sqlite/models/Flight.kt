package com.example.quanlydatve_sqlite.models

data class Flight(
    val id: Int,
    val ten: String,
    val ngayDi: String,
    val ngayVe: String,
    val gia: String,
    val hinhAnhResId: Int,
    val description: String
)
