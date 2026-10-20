package com.project.myshopeegw.data.api

import com.project.myshopeegw.data.model.PaymentMethod

object PaymentMethodProvider {

    val methods = mutableListOf(
        PaymentMethod(
            id = "bca_va",
            name = "BCA Virtual Account",
            category = "Virtual Account",
            codeOrVa = "123 456 7890",
            description = "Verifikasi otomatis 24 jam (Standar IKMI)",
            badgeText = "BCA",
            instructions = listOf(
                "Buka aplikasi BCA Mobile / m-BCA atau kunjungi ATM BCA terdekat.",
                "Pilih menu m-Transfer > BCA Virtual Account.",
                "Masukkan nomor Virtual Account: 123 456 7890.",
                "Periksa nominal tagihan dan konfirmasi transaksi menggunakan PIN Anda.",
                "Tekan tombol 'Cek Status Pembayaran' untuk simulasi verifikasi lunas."
            )
        ),
        PaymentMethod(
            id = "mandiri_va",
            name = "Mandiri Virtual Account",
            category = "Virtual Account",
            codeOrVa = "8950 1234 5678",
            description = "Transfer instan via Livin' by Mandiri / ATM",
            badgeText = "MANDIRI",
            instructions = listOf(
                "Buka aplikasi Livin' by Mandiri atau mesin ATM Mandiri.",
                "Pilih menu Bayar > Multipayment / Virtual Account.",
                "Masukkan nomor Virtual Account: 8950 1234 5678.",
                "Pastikan merchant terdaftar adalah ShopiKu IKMI.",
                "Konfirmasi pembayaran dengan PIN Anda, lalu tekan 'Cek Status Pembayaran'."
            )
        ),
        PaymentMethod(
            id = "bri_va",
            name = "BRI Virtual Account (BRIVA)",
            category = "Virtual Account",
            codeOrVa = "1280 0123 4567",
            description = "Transfer instan via BRImo / ATM BRI",
            badgeText = "BRI",
            instructions = listOf(
                "Buka aplikasi BRImo atau mesin ATM BRI terdekat.",
                "Pilih menu Pembayaran > BRIVA.",
                "Masukkan nomor BRIVA: 1280 0123 4567.",
                "Periksa total belanja Anda dan masukkan PIN BRImo.",
                "Tekan tombol 'Cek Status Pembayaran' untuk verifikasi."
            )
        ),
        PaymentMethod(
            id = "bni_va",
            name = "BNI Virtual Account",
            category = "Virtual Account",
            codeOrVa = "9880 1234 5678",
            description = "Transfer via BNI Mobile Banking / ATM BNI",
            badgeText = "BNI",
            instructions = listOf(
                "Buka aplikasi BNI Mobile Banking atau ATM BNI.",
                "Pilih menu Transfer > Virtual Account Billing.",
                "Masukkan nomor tagihan VA: 9880 1234 5678.",
                "Konfirmasi tagihan dan masukkan Password Transaksi.",
                "Tekan tombol 'Cek Status Pembayaran' untuk menyelesaikan."
            )
        ),
        PaymentMethod(
            id = "qris",
            name = "QRIS (ShopeePay, GoPay, OVO, Dana)",
            category = "E-Wallet & QRIS",
            codeOrVa = "ID1020088234190",
            description = "Scan kode QR instan dari semua e-wallet & m-banking",
            badgeText = "QRIS",
            instructions = listOf(
                "Buka aplikasi m-Banking atau E-Wallet pilihan Anda (BCA, GoPay, OVO, ShopeePay, Dana).",
                "Pilih menu 'Scan QR / QRIS'.",
                "Arahkan kamera smartphone ke QRIS Sandbox ShopiKu.",
                "Periksa nominal tagihan dan konfirmasi pembayaran dengan PIN.",
                "Tekan tombol 'Cek Status Pembayaran' untuk konfirmasi pelunasan."
            )
        ),
        PaymentMethod(
            id = "shopeepay",
            name = "ShopeePay / SPayLater",
            category = "E-Wallet & QRIS",
            codeOrVa = "0812-3456-7890",
            description = "Pembayaran instan langsung dari saldo dompet",
            badgeText = "SPAY",
            instructions = listOf(
                "Pastikan saldo ShopeePay Anda mencukupi total pembayaran.",
                "Nomor akun ShopeePay terdaftar: 0812-3456-7890.",
                "Masukkan 6 digit PIN ShopeePay Anda saat otorisasi.",
                "Pembayaran diproses seketika tanpa biaya admin.",
                "Tekan 'Cek Status Pembayaran' untuk melanjutkan ke pengiriman."
            )
        ),
        PaymentMethod(
            id = "indomaret",
            name = "Indomaret / Alfamart",
            category = "Mitra & Tunai",
            codeOrVa = "IKMI-SHOP-9912",
            description = "Bayar tunai di gerai ritel Kota Cirebon",
            badgeText = "RITEL",
            instructions = listOf(
                "Kunjungi gerai Indomaret atau Alfamart terdekat di Kota Cirebon.",
                "Sampaikan ke kasir ingin membayar pesanan ShopiKu.",
                "Tunjukkan kode pembayaran: IKMI-SHOP-9912.",
                "Bayar sesuai jumlah tagihan dan simpan struk tanda terima.",
                "Tekan tombol 'Cek Status Pembayaran' di aplikasi."
            )
        ),
        PaymentMethod(
            id = "cod",
            name = "COD (Bayar di Tempat)",
            category = "Mitra & Tunai",
            codeOrVa = "TUNAI SAAT KURIR TIBA",
            description = "Bayar tunai saat kurir Cirebon mengantar paket",
            badgeText = "COD",
            instructions = listOf(
                "Siapkan uang tunai pas sesuai total tagihan saat kurir tiba.",
                "Kurir Logistik Cirebon akan menyerahkan paket pesanan ke alamat Anda.",
                "Periksa keutuhan paket dan berikan uang tunai kepada kurir.",
                "Tekan tombol 'Cek Status Pembayaran' untuk konfirmasi penyelesaian."
            )
        )
    )

    fun getDefault(): PaymentMethod = methods.first()

    fun getById(id: String): PaymentMethod {
        return methods.find { it.id == id } ?: getDefault()
    }

    fun getByNameOrVa(name: String?, va: String?): PaymentMethod {
        if (!name.isNullOrBlank()) {
            val byName = methods.find { it.name.equals(name, ignoreCase = true) }
            if (byName != null) return byName
        }
        if (!va.isNullOrBlank()) {
            val byVa = methods.find { it.codeOrVa.replace(" ", "") == va.replace(" ", "") }
            if (byVa != null) return byVa
        }
        return getDefault()
    }

    fun addCustomMethod(name: String, codeOrVa: String, category: String, badge: String? = null): PaymentMethod {
        val badgeStr = if (!badge.isNullOrBlank()) badge.uppercase() else name.take(4).uppercase()
        val newMethod = PaymentMethod(
            id = "custom_" + System.currentTimeMillis(),
            name = name,
            category = category,
            codeOrVa = codeOrVa,
            description = "Metode kustom yang ditambahkan pengguna",
            badgeText = badgeStr,
            instructions = listOf(
                "Buka aplikasi atau channel pembayaran $name.",
                "Lakukan transfer ke nomor / ID: $codeOrVa.",
                "Pastikan nominal transfer sesuai dengan total tagihan pesanan.",
                "Simpan bukti transaksi Anda.",
                "Tekan tombol 'Cek Status Pembayaran' untuk simulasi verifikasi pelunasan."
            ),
            isCustom = true
        )
        methods.add(newMethod)
        return newMethod
    }
}
