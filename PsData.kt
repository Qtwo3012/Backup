package com.example.rgbilling

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PsData {

    // ==========================
    // STATUS PS
    // ==========================

    val status = Array(4) { "READY" }
    val berjalan = BooleanArray(4)

    // ==========================
    // TIMER
    // ==========================

    val detik = IntArray(4)
    val sisaDetik = IntArray(4)

    val waktuMulai = LongArray(4)
    val waktuTerakhirUpdate = LongArray(4)

    // ==========================
    // BIAYA
    // ==========================

    val biaya = IntArray(4)
    val biayaKantin = IntArray(4)

    var tarifPerJam = 10000

    // ==========================
    // PELANGGAN
    // ==========================

    val namaPelanggan = Array(4) { "" }

    // ==========================
    // BILLING
    // ==========================

    val modeBilling = Array(4) { "REGULER" }

    val jamMulai = Array(4) { "-" }
    val jamSelesai = Array(4) { "-" }

    val paketJam = IntArray(4)
    val paketDetik = IntArray(4)
    val durasiPaket = IntArray(4)

    // ==========================
    // NOTIFIKASI
    // ==========================

    val notif5Menit = BooleanArray(4)
    val notif1Menit = BooleanArray(4)
    val notifHabis = BooleanArray(4)
    val alarmAktif = BooleanArray(4)

    // ==========================
    // PEMBAYARAN
    // ==========================

    val uangBayar = IntArray(4)
    val kembalian = IntArray(4)

    // ==========================
    // AUTO SAVE (SPRINT 2)
    // ==========================

    var lastSaveTime = 0L

    // ==========================
    // START REGULER
    // ==========================

    fun mulai(index: Int) {

        berjalan[index] = true
        status[index] = "BERMAIN"

        waktuMulai[index] =
            System.currentTimeMillis()

        val sdf = SimpleDateFormat(
            "HH:mm:ss",
            Locale.getDefault()
        )

        jamMulai[index] =
            sdf.format(Date())
    }


    // ==========================
    // START PAKET
    // ==========================

    fun mulaiPaket(
        index: Int,
        jam: Int
    ) {

        paketJam[index] = jam

        sisaDetik[index] =
            jam * 10

        paketDetik[index] =
            jam * 10

        waktuMulai[index] =
            System.currentTimeMillis()

        val sdf = SimpleDateFormat(
            "HH:mm:ss",
            Locale.getDefault()
        )

        jamMulai[index] =
            sdf.format(Date())

        val selesai = Date(
            System.currentTimeMillis()
                    + jam * 3600 * 1000L
        )

        jamSelesai[index] =
            sdf.format(selesai)

        notif5Menit[index] = false
        notif1Menit[index] = false
        notifHabis[index] = false

        berjalan[index] = true
        status[index] = "BERMAIN"
    }

    fun tambahPaket(index: Int, jam: Int) {

        paketJam[index] += jam
        paketDetik[index] += jam * 3600
        sisaDetik[index] += jam * 3600
        alarmAktif[index] = false

        val sdf = SimpleDateFormat(
            "HH:mm:ss",
            Locale.getDefault()
        )

        val selesai = Date(
            waktuMulai[index] +
                    paketDetik[index] * 1000L
        )

        jamSelesai[index] = sdf.format(selesai)
    }

    fun berhenti(index: Int) {

        berjalan[index] = false
        status[index] = "READY"
    }


    // ==========================
    // HITUNG REAL TIME
    // ==========================

    fun hitung(index: Int) {

        if (modeBilling[index] == "PAKET") {

            val sekarang =
                System.currentTimeMillis()

            val lewat =
                ((sekarang - waktuMulai[index]) / 1000).toInt()

            sisaDetik[index] =
                paketDetik[index] - lewat


            if (sisaDetik[index] <= 0) {

                sisaDetik[index] = 0

                berjalan[index] = false

                status[index] = "SELESAI"
                alarmAktif[index] = true

                jamSelesai[index] =
                    SimpleDateFormat(
                        "HH:mm:ss",
                        Locale.getDefault()
                    ).format(Date())
            }

            detik[index] =
                paketDetik[index] - sisaDetik[index]

        } else {

            val sekarang =
                System.currentTimeMillis()

            detik[index] =
                ((sekarang - waktuMulai[index]) / 1000).toInt()

        }


        biaya[index] =
            detik[index] * tarifPerJam / 3600
    }


    // ==========================
    // SELESAI TRANSAKSI
    // ==========================

    fun selesai(index: Int) {

        status[index] = "READY"
        berjalan[index] = false

        detik[index] = 0
        biaya[index] = 0
        biayaKantin[index] = 0

        uangBayar[index] = 0
        kembalian[index] = 0

        paketJam[index] = 0
        paketDetik[index] = 0
        durasiPaket[index] = 0

        waktuMulai[index] = 0

        jamMulai[index] = "-"
        jamSelesai[index] = "-"

        namaPelanggan[index] = ""
        modeBilling[index] = "REGULER"
        
        sisaDetik[index] = 0
        
        notif5Menit[index] = false
        notif1Menit[index] = false
        notifHabis[index] = false
    }


    // ==========================
    // DASHBOARD HELPER
    // ==========================

    fun jumlahPsAktif(): Int {

        var aktif = 0

        for (i in berjalan.indices) {
            if (berjalan[i]) aktif++
        }

        return aktif
    }


    fun totalBiayaPsBerjalan(): Int {

        var total = 0

        for (i in biaya.indices) {
            total += biaya[i]
        }

        return total
    }


    fun totalBiayaKantin(): Int {

        var total = 0

        for (i in biayaKantin.indices) {
            total += biayaKantin[i]
        }

        return total
    }


    fun totalTagihan(): Int {

        return totalBiayaPsBerjalan()
                + totalBiayaKantin()
    }
}