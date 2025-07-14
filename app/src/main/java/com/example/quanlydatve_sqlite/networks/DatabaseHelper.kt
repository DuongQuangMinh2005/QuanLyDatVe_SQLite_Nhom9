package com.example.quanlydatve_sqlite.networks

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.models.Booking
import com.example.quanlydatve_sqlite.models.BookingRequest
import com.example.quanlydatve_sqlite.models.ChuyenBay
import com.example.quanlydatve_sqlite.models.ChuyenBayYeuThich
import com.example.quanlydatve_sqlite.models.Flight
import com.example.quanlydatve_sqlite.models.NguoiDung


class DatabaseHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "flight_booking.db"
        private const val DATABASE_VERSION = 24
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableNguoiDung = """
            CREATE TABLE IF NOT EXISTS NguoiDung (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tenDangNhap TEXT NOT NULL UNIQUE,
                matKhau TEXT NOT NULL,
                hoTen TEXT NOT NULL,
                ngaySinh TEXT NOT NULL,
                email TEXT,
                phone TEXT,
                role TEXT NOT NULL
            );
        """.trimIndent()

        val createTableBooking = """
            CREATE TABLE IF NOT EXISTS Booking (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tu TEXT NOT NULL,
                den TEXT NOT NULL,
                ngayDi TEXT NOT NULL,
                ngayVe TEXT NOT NULL,
                quantity INTEGER NOT NULL,
                ticketType TEXT NOT NULL,
                totalPrice REAL NOT NULL,
                paymentMethod TEXT NOT NULL,
                tenNganHang TEXT,
                soTaiKhoan TEXT,
                noiDung TEXT,
                userId INTEGER,
                price REAL,
                tax REAL

            );
        """.trimIndent()

        val createTableChuyenBay = """
            CREATE TABLE IF NOT EXISTS ChuyenBay (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                tu TEXT NOT NULL,
                den TEXT NOT NULL,
                ngayDi TEXT NOT NULL,
                ngayVe TEXT NOT NULL,
                gia REAL NOT NULL,
                hinhAnh TEXT,
                yeuThich INTEGER DEFAULT 0,
                thoiGianYeuThich INTEGER


            );
        """.trimIndent()

        val createTableChuyenBayYeuThich = """
            CREATE TABLE IF NOT EXISTS ChuyenBayYeuThich (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                userID INTEGER NOT NULL,
                chuyenBayID INTEGER NOT NULL,
                ngayDi TEXT NOT NULL,
                ngayVe TEXT NOT NULL,
                tu TEXT NOT NULL,
                den TEXT NOT NULL,
                hinhAnh TEXT,
                giaVe REAL,
                thoiGianYeuThich INTEGER
            );
        """.trimIndent()

        db.execSQL(createTableChuyenBayYeuThich)


        db.execSQL(createTableNguoiDung)
        db.execSQL(createTableBooking)
        db.execSQL(createTableChuyenBay)

        // Chèn dữ liệu mẫu nếu bảng ChuyenBay rỗng
        val cursor = db.rawQuery("SELECT COUNT(*) FROM ChuyenBay", null)
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()

        if (count == 0) {
            val insertChuyenBayData = """
                INSERT INTO ChuyenBay (tu, den, ngayDi, ngayVe, gia, hinhAnh) VALUES
                ('Hà Nội', 'Phú Quốc', '15/06/2025', '20/06/2025', 1500000, 'phuquoc'),
                ('Đà Nẵng', 'Hà Nội', '01/07/2025', '05/07/2025', 1200000, 'hanoi'),
                ('Phú Quốc', 'Đà Nẵng', '25/06/2025', '30/06/2025', 1400000, 'danang'),
                ('Hồ Chí Minh', 'Nha Trang', '10/07/2025', '15/07/2025', 1100000, 'nhatrang'),
                ('Đà Lạt', 'Hà Nội', '20/07/2025', '25/07/2025', 1300000, 'dalat'),
                ('Quảng Ngãi', 'Đà Nẵng', '05/08/2025', '10/08/2025', 900000, 'quangngai'),
                ('Vũng Tàu', 'Phú Quốc', '12/08/2025', '18/08/2025', 1250000, 'vungtau'),
                ('Hà Nội', 'Đà Lạt', '22/08/2025', '28/08/2025', 1350000, 'dalat'),
                ('Nha Trang', 'Vũng Tàu', '01/09/2025', '05/09/2025', 1150000, 'vungtau'),
                ('Đà Nẵng', 'Quảng Ngãi', '08/09/2025', '12/09/2025', 950000, 'quangngai'),
                ('Hà Nội', 'Phú Quốc', '05/07/2025', '10/07/2025', 1550000, 'phuquoc'),
                ('Đà Nẵng', 'Hà Nội', '10/07/2025', '15/07/2025', 1250000, 'hanoi'),
                ('Phú Quốc', 'Đà Nẵng', '01/08/2025', '06/08/2025', 1450000, 'danang'),
                ('Hà Nội', 'Đà Lạt', '01/09/2025', '06/09/2025', 1400000, 'dalat'),
                ('Nha Trang', 'Vũng Tàu', '10/09/2025', '15/09/2025', 1170000, 'vungtau');
            """.trimIndent()


            val insertNguoiDungData = """
                INSERT INTO NguoiDung (tenDangNhap, matKhau, hoTen, ngaySinh, email, phone, role) VALUES
                ('admin', '123456', 'Nguyễn Văn A', '30/06/2005', 'admin@example.com', '0123456789','admin'),
                ('duongminhhuy', 'huy123', 'Dương Minh Huy', '30/06/2005', 'huy@example.com', '0981522360','user');
            """.trimIndent()

            db.execSQL(insertChuyenBayData)
            db.execSQL(insertNguoiDungData)
            Log.d("DatabaseHelper", "Chèn dữ liệu mẫu thành công")
        }

        Log.d("DatabaseHelper", "onCreate: Tạo bảng thành công")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Booking")
        db.execSQL("DROP TABLE IF EXISTS ChuyenBay")
        db.execSQL("DROP TABLE IF EXISTS NguoiDung")
        onCreate(db)
        Log.d("DatabaseHelper", "onUpgrade: Đã nâng cấp DB từ version $oldVersion đến $newVersion")
    }

    fun dangKyNguoiDung(
        tenDangNhap: String,
        matKhau: String,
        hoTen: String,
        ngaySinh: String,
        email: String?,
        phone: String?,
        role: String = "user"
    ): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("tenDangNhap", tenDangNhap)
            put("matKhau", matKhau)
            put("hoTen", hoTen)
            put("ngaySinh", ngaySinh)
            put("email", email)
            put("phone", phone)
            put("role", role)
        }

        Log.d("DatabaseHelper", "Đang đăng ký người dùng: $tenDangNhap, $hoTen, $ngaySinh, $email, $phone, role=$role")

        return try {
            val result = db.insertOrThrow("NguoiDung", null, values)
            if (result != -1L) {
                Log.d("DatabaseHelper", "Đăng ký người dùng thành công với ID: $result")
                true
            } else {
                Log.e("DatabaseHelper", "Lỗi: Không thể insert vào bảng NguoiDung")
                false
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Lỗi khi đăng ký người dùng", e)
            false
        } finally {
            db.close()
        }
    }

    fun getChuyenBays(tu: String, den: String, ngayDi: String, ngayVe: String): List<ChuyenBay> {
        val list = mutableListOf<ChuyenBay>()
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM ChuyenBay WHERE tu = ? AND den = ? AND ngayDi = ? AND ngayVe = ?",
            arrayOf(tu, den, ngayDi, ngayVe)
        )

        while (cursor.moveToNext()) {
            list.add(
                ChuyenBay(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    tu = cursor.getString(cursor.getColumnIndexOrThrow("tu")),
                    den = cursor.getString(cursor.getColumnIndexOrThrow("den")),
                    ngayDi = cursor.getString(cursor.getColumnIndexOrThrow("ngayDi")),
                    ngayVe = cursor.getString(cursor.getColumnIndexOrThrow("ngayVe")),
                    giaVe = cursor.getDouble(cursor.getColumnIndexOrThrow("gia")),
                    hinhAnh = cursor.getString(cursor.getColumnIndexOrThrow("hinhAnh"))
                )
            )
        }
        cursor.close()
        db.close()
        return list
    }

    fun getFeaturedChuyenBays(): List<Flight> {
        val list = mutableListOf<Flight>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ChuyenBay", null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val tu = cursor.getString(cursor.getColumnIndexOrThrow("tu"))
            val den = cursor.getString(cursor.getColumnIndexOrThrow("den"))
            val gia = cursor.getDouble(cursor.getColumnIndexOrThrow("gia"))
            val hinhAnhTen = cursor.getString(cursor.getColumnIndexOrThrow("hinhAnh"))
            val ngayDi = cursor.getString(cursor.getColumnIndexOrThrow("ngayDi"))
            val ngayVe = cursor.getString(cursor.getColumnIndexOrThrow("ngayVe"))

            val resId = context.resources.getIdentifier(hinhAnhTen, "drawable", context.packageName)
            val title = "$tu → $den"
            val description = "Chuyến bay từ $tu đến $den"
            val giaStr = "%,.0f VNĐ".format(gia).replace(',', '.')

            list.add(
                Flight(
                    id = id,
                    ten = title,
                    ngayDi = ngayDi,
                    ngayVe = ngayVe,
                    gia = giaStr,
                    hinhAnhResId = resId,
                    description = description
                )
            )
        }

        cursor.close()
        db.close()
        return list.shuffled().take(5)
    }

    fun getNguoiDungByTenDangNhap(username: String): NguoiDung? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM NguoiDung WHERE tenDangNhap = ?", arrayOf(username))

        var user: NguoiDung? = null
        if (cursor.moveToFirst()) {
            user = NguoiDung(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                tenDangNhap = cursor.getString(cursor.getColumnIndexOrThrow("tenDangNhap")),
                matKhau = cursor.getString(cursor.getColumnIndexOrThrow("matKhau")),
                hoTen = cursor.getString(cursor.getColumnIndexOrThrow("hoTen")),
                ngaySinh = cursor.getString(cursor.getColumnIndexOrThrow("ngaySinh")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                role = cursor.getString(cursor.getColumnIndexOrThrow("role"))

            )
        }
        cursor.close()
        db.close()
        return user
    }

    fun getNguoiDung(tenDangNhap: String): NguoiDung? {
        val db = readableDatabase
        val cursor = db.query(
            "NguoiDung",
            null,
            "tenDangNhap = ?",
            arrayOf(tenDangNhap),
            null, null, null
        )

        var user: NguoiDung? = null
        if (cursor.moveToFirst()) {
            user = NguoiDung(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                tenDangNhap = tenDangNhap,
                matKhau = cursor.getString(cursor.getColumnIndexOrThrow("matKhau")),
                hoTen = cursor.getString(cursor.getColumnIndexOrThrow("hoTen")),
                ngaySinh = cursor.getString(cursor.getColumnIndexOrThrow("ngaySinh")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                role = cursor.getString(cursor.getColumnIndexOrThrow("role"))
            )
        }
        cursor.close()
        db.close()
        return user
    }

    fun updateNguoiDung(
        tenDangNhap: String,
        hoTen: String,
        ngaySinh: String,
        email: String?,
        phone: String?,
        matKhau: String

        ): Boolean {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("hoTen", hoTen)
                put("ngaySinh", ngaySinh)
                put("email", email)
                put("phone", phone)
                put("matKhau", matKhau)
            }

            Log.d("DatabaseHelper", "Cập nhật người dùng: $tenDangNhap - $hoTen - $ngaySinh - $email - $phone")

            val rowsAffected = db.update("NguoiDung", values, "tenDangNhap = ?", arrayOf(tenDangNhap))

            if (rowsAffected > 0) {
                Log.d("DatabaseHelper", "Cập nhật thành công: $rowsAffected dòng bị ảnh hưởng.")
            } else {
                Log.e("DatabaseHelper", "Cập nhật thất bại cho tài khoản: $tenDangNhap")
            }

            db.close()
            return rowsAffected > 0
    }

    fun getAllChuyenBay(tu: String, den: String, ngayDi: String, ngayVe: String): List<Flight> {
        val db = readableDatabase
        val flights = mutableListOf<Flight>()
        val query = "SELECT * FROM ChuyenBay WHERE tu = ? AND den = ?"
        val cursor = db.rawQuery(query, arrayOf(tu, den))

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val tuDb = cursor.getString(cursor.getColumnIndexOrThrow("tu"))
            val denDb = cursor.getString(cursor.getColumnIndexOrThrow("den"))
            val ngayDiDb = cursor.getString(cursor.getColumnIndexOrThrow("ngayDi"))
            val ngayVeDb = cursor.getString(cursor.getColumnIndexOrThrow("ngayVe"))
            val giaDouble = cursor.getDouble(cursor.getColumnIndexOrThrow("gia"))
            val hinhAnh = cursor.getString(cursor.getColumnIndexOrThrow("hinhAnh"))

            val ten = "$tuDb → $denDb"
            val giaStr = giaDouble.toInt().toString()
            val description = "Khởi hành: $ngayDiDb\nVề: $ngayVeDb\nGiá: $giaStr VNĐ"

            flights.add(
                Flight(
                    id = id,
                    ten = ten,
                    ngayDi = ngayDiDb,
                    ngayVe = ngayVeDb,
                    gia = giaStr,
                    hinhAnhResId = when (hinhAnh) {
                        "hanoi" -> R.drawable.hanoi
                        "phuquoc" -> R.drawable.phuquoc
                        "danang" -> R.drawable.danang
                        "nhatrang" -> R.drawable.nhatrang
                        "dalat" -> R.drawable.dalat
                        "quangngai" -> R.drawable.quangngai
                        "vungtau" -> R.drawable.vungtau
                        else -> R.drawable.hanoi
                    },
                    description = description
                )
            )
        }

        cursor.close()
        db.close()
        return flights
    }
    fun getHinhAnhForRoute(tu: String, den: String): String {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT hinhAnh FROM ChuyenBay WHERE tu = ? AND den = ? LIMIT 1",
            arrayOf(tu, den)
        )

        var hinhAnh = "ic_airplane_decor" // hình mặc định

        if (cursor.moveToFirst()) {
            hinhAnh = cursor.getString(cursor.getColumnIndexOrThrow("hinhAnh"))
        }

        cursor.close()
        db.close()
        return hinhAnh
    }

    fun deleteChuyenBay(id: Int): Boolean {
        val db = writableDatabase
        val rows = db.delete("ChuyenBay", "id = ?", arrayOf(id.toString()))
        db.close()
        return rows > 0
    }

    fun getAllChuyenBayRaw(): List<ChuyenBay> {
        val list = mutableListOf<ChuyenBay>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ChuyenBay", null)
        while (cursor.moveToNext()) {
            list.add(
                ChuyenBay(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    tu = cursor.getString(cursor.getColumnIndexOrThrow("tu")),
                    den = cursor.getString(cursor.getColumnIndexOrThrow("den")),
                    ngayDi = cursor.getString(cursor.getColumnIndexOrThrow("ngayDi")),
                    ngayVe = cursor.getString(cursor.getColumnIndexOrThrow("ngayVe")),
                    giaVe = cursor.getDouble(cursor.getColumnIndexOrThrow("gia")),
                    hinhAnh = cursor.getString(cursor.getColumnIndexOrThrow("hinhAnh"))
                )
            )
        }
        cursor.close()
        db.close()
        return list
    }

    fun updateChuyenBay(chuyenBay: ChuyenBay): Boolean {
        return try {
            writableDatabase.use { db ->
                val values = ContentValues().apply {
                    put("tu", chuyenBay.tu)
                    put("den", chuyenBay.den)
                    put("ngayDi", chuyenBay.ngayDi)
                    put("ngayVe", chuyenBay.ngayVe)
                    put("gia", chuyenBay.giaVe)
                    put("hinhAnh", chuyenBay.hinhAnh)
                }

                val result = db.update("ChuyenBay", values, "id = ?", arrayOf(chuyenBay.id.toString()))
                result > 0
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun insertChuyenBay(chuyenBay: ChuyenBay): Boolean {
        val db = writableDatabase
        return try {
            val values = ContentValues().apply {
                put("tu", chuyenBay.tu)
                put("den", chuyenBay.den)
                put("ngayDi", chuyenBay.ngayDi)
                put("ngayVe", chuyenBay.ngayVe)
                put("gia", chuyenBay.giaVe)
                put("hinhAnh", chuyenBay.hinhAnh)
            }
            val result = db.insert("ChuyenBay", null, values)
            result != -1L
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.close()
        }
    }
    fun getChuyenBayById(id: Int): ChuyenBay? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ChuyenBay WHERE id = ?", arrayOf(id.toString()))
        var chuyenBay: ChuyenBay? = null

        if (cursor.moveToFirst()) {
            chuyenBay = ChuyenBay(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                ngayDi = cursor.getString(cursor.getColumnIndexOrThrow("ngayDi")),
                ngayVe = cursor.getString(cursor.getColumnIndexOrThrow("ngayVe")),
                tu = cursor.getString(cursor.getColumnIndexOrThrow("tu")),
                den = cursor.getString(cursor.getColumnIndexOrThrow("den")),
                hinhAnh = cursor.getString(cursor.getColumnIndexOrThrow("hinhAnh")),
                giaVe = cursor.getDouble(cursor.getColumnIndexOrThrow("gia")),
                yeuThich = cursor.getInt(cursor.getColumnIndexOrThrow("yeuThich")) == 1 // map int->boolean
            )
        }
        cursor.close()
        db.close()
        return chuyenBay
    }

    fun fixHinhAnhPath() {
        val db = writableDatabase
        val cursor = db.rawQuery("SELECT id, hinhAnh FROM ChuyenBay", null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val hinhAnh = cursor.getString(cursor.getColumnIndexOrThrow("hinhAnh"))
            if (hinhAnh.contains("/")) {
                val fileNameOnly = hinhAnh.substringAfterLast("/")
                val values = ContentValues().apply {
                    put("hinhAnh", fileNameOnly)
                }
                db.update("ChuyenBay", values, "id = ?", arrayOf(id.toString()))
            }
        }
        cursor.close()
        db.close()
    }
    fun updatePaymentInfo(
        bookingId: Int,
        tenNganHang: String,
        soTaiKhoan: String,
        noiDung: String
    ): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("tenNganHang", tenNganHang)
            put("soTaiKhoan", soTaiKhoan)
            put("noiDung", noiDung)
            put("paymentMethod", "Chuyển khoản")
        }
        val rows = db.update(
            "Booking",
            values,
            "id = ?",
            arrayOf(bookingId.toString())
        )
        db.close()
        return rows > 0
    }

    fun insertBooking(booking: BookingRequest): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put("userId", booking.userId)
            put("tu", booking.tu)
            put("den", booking.den)
            put("ngayDi", booking.ngayDi)
            put("ngayVe", booking.ngayVe)
            put("quantity", booking.quantity)
            put("ticketType", booking.ticketType)
            put("price", booking.price)
            put("tax", booking.tax)
            put("totalPrice", booking.totalPrice)
            put("paymentMethod", booking.paymentMethod)
        }
        val result = db.insert("Booking", null, values)
        db.close()
        return result
    }

    fun getBookingById(id: Int): Booking? {
        val db = this.readableDatabase
        val cursor = db.query(
            "Booking",
            null,
            "id = ?",
            arrayOf(id.toString()),
            null, null, null
        )
        var booking: Booking? = null
        if (cursor.moveToFirst()) {
            booking = Booking(
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
        }
        cursor.close()
        db.close()
        return booking
    }
    fun getUserById(id: Int): NguoiDung? {
        val db = readableDatabase
        val cursor = db.query("NguoiDung", null, "id=?", arrayOf(id.toString()), null, null, null)
        var user: NguoiDung? = null
        if (cursor.moveToFirst()) {
            user = NguoiDung(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                tenDangNhap = cursor.getString(cursor.getColumnIndexOrThrow("tenDangNhap")),
                matKhau = cursor.getString(cursor.getColumnIndexOrThrow("matKhau")),
                hoTen = cursor.getString(cursor.getColumnIndexOrThrow("hoTen")),
                ngaySinh = cursor.getString(cursor.getColumnIndexOrThrow("ngaySinh")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                role = cursor.getString(cursor.getColumnIndexOrThrow("role"))
            )
        }
        cursor.close()
        db.close()
        return user
    }
    fun getUserByUsername(username: String): NguoiDung? {
        val db = readableDatabase
        val cursor = db.query(
            "NguoiDung",
            null,
            "tenDangNhap = ?",
            arrayOf(username),
            null,
            null,
            null
        )
        var user: NguoiDung? = null
        if (cursor.moveToFirst()) {
            user = NguoiDung(
                id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                tenDangNhap = cursor.getString(cursor.getColumnIndexOrThrow("tenDangNhap")),
                matKhau = cursor.getString(cursor.getColumnIndexOrThrow("matKhau")),
                hoTen = cursor.getString(cursor.getColumnIndexOrThrow("hoTen")),
                ngaySinh = cursor.getString(cursor.getColumnIndexOrThrow("ngaySinh")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                phone = cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                role = cursor.getString(cursor.getColumnIndexOrThrow("role"))
            )
        }
        cursor.close()
        db.close()
        return user
    }
    fun themChuyenBayYeuThich(userID: Int, chuyenBay: ChuyenBay): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("userID", userID)
            put("chuyenBayID", chuyenBay.id)
            put("ngayDi", chuyenBay.ngayDi)
            put("ngayVe", chuyenBay.ngayVe)
            put("tu", chuyenBay.tu)
            put("den", chuyenBay.den)
            put("hinhAnh", chuyenBay.hinhAnh)
            put("giaVe", chuyenBay.giaVe)
            put("thoiGianYeuThich", System.currentTimeMillis())
        }
        val result = db.insert("ChuyenBayYeuThich", null, values)
        db.close()
        return result
    }

    fun xoaChuyenBayYeuThich(userID: Int, chuyenBayID: Int): Int {
        val db = writableDatabase
        val result = db.delete(
            "ChuyenBayYeuThich",
            "userID = ? AND chuyenBayID = ?",
            arrayOf(userID.toString(), chuyenBayID.toString())
        )
        db.close()
        return result
    }

    fun getAllChuyenBayYeuThichTheoUser(userID: Int): List<ChuyenBayYeuThich> {
        val db = readableDatabase
        val list = mutableListOf<ChuyenBayYeuThich>()
        val cursor = db.rawQuery(
            "SELECT * FROM ChuyenBayYeuThich WHERE userID = ? ORDER BY thoiGianYeuThich DESC",
            arrayOf(userID.toString())
        )
        while (cursor.moveToNext()) {
            list.add(
                ChuyenBayYeuThich(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    userID = cursor.getInt(cursor.getColumnIndexOrThrow("userID")),
                    chuyenBayID = cursor.getInt(cursor.getColumnIndexOrThrow("chuyenBayID")),
                    ngayDi = cursor.getString(cursor.getColumnIndexOrThrow("ngayDi")),
                    ngayVe = cursor.getString(cursor.getColumnIndexOrThrow("ngayVe")),
                    tu = cursor.getString(cursor.getColumnIndexOrThrow("tu")),
                    den = cursor.getString(cursor.getColumnIndexOrThrow("den")),
                    hinhAnh = cursor.getString(cursor.getColumnIndexOrThrow("hinhAnh")),
                    giaVe = cursor.getDouble(cursor.getColumnIndexOrThrow("giaVe")),
                    thoiGianYeuThich = if (!cursor.isNull(cursor.getColumnIndexOrThrow("thoiGianYeuThich")))
                        cursor.getLong(cursor.getColumnIndexOrThrow("thoiGianYeuThich")) else null
                )
            )
        }
        cursor.close()
        db.close()
        return list
    }

    fun isChuyenBayYeuThich(userID: Int, chuyenBayID: Int): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id FROM ChuyenBayYeuThich WHERE userID = ? AND chuyenBayID = ?",
            arrayOf(userID.toString(), chuyenBayID.toString())
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        db.close()
        return exists
    }




}
