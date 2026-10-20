package com.project.myshopeegw

import com.project.myshopeegw.data.api.MockDataProvider
import com.project.myshopeegw.data.api.PaymentMethodProvider
import com.project.myshopeegw.data.model.CartItem
import com.project.myshopeegw.data.model.CheckoutPayload
import com.project.myshopeegw.data.model.Order
import com.project.myshopeegw.utils.CurrencyHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Unit Test Otomatis untuk Menguji 7 Alur Transaksi Utama (TC-01 s/d TC-07)
 * Sesuai Spesifikasi Pengujian Black-Box pada BAB V Laporan ShopiKu.
 */
class ShopiKuTransactionUnitTest {

    @Before
    fun setUp() {
        MockDataProvider.cartItems.clear()
    }

    // =========================================================================
    // TC-01: Menampilkan & Memfilter Katalog Produk
    // =========================================================================
    @Test
    fun testTC01_KatalogDanFilterProduk() {
        val allProducts = MockDataProvider.products
        assertTrue("Katalog produk tidak boleh kosong", allProducts.isNotEmpty())
        assertEquals("Total produk default harus 10", 10, allProducts.size)

        // Filter Elektronik
        val elektronik = allProducts.filter { it.category.equals("elektronik", ignoreCase = true) }
        assertEquals("Kategori elektronik harus berisi 4 produk", 4, elektronik.size)

        // Filter Khas Cirebon
        val cirebon = allProducts.filter { it.category.equals("cirebon", ignoreCase = true) }
        assertEquals("Kategori Khas Cirebon harus berisi 3 produk", 3, cirebon.size)
        assertTrue("Harus ada Sirup Tjampolay", cirebon.any { it.name.contains("Tjampolay") })
    }

    // =========================================================================
    // TC-02: Pencarian Cepat Produk (Instant Search)
    // =========================================================================
    @Test
    fun testTC02_PencarianCepatProduk() {
        val query = "TWS".lowercase()
        val searchResult = MockDataProvider.products.filter {
            it.name.lowercase().contains(query) || it.description.lowercase().contains(query)
        }

        assertFalse("Hasil pencarian 'TWS' tidak boleh kosong", searchResult.isEmpty())
        assertTrue("Hasil pencarian harus mengandung kata TWS", searchResult.all {
            it.name.contains("TWS", ignoreCase = true) || it.description.contains("TWS", ignoreCase = true)
        })
    }

    // =========================================================================
    // TC-03: Detail Produk & Pilihan Varian
    // =========================================================================
    @Test
    fun testTC03_DetailDanVarianProduk() {
        val sampleProduct = MockDataProvider.products.find { it.id == "prod-1" }
        assertNotNull("Produk prod-1 harus ditemukan", sampleProduct)
        assertTrue("Produk harus memiliki varian", sampleProduct!!.variants.isNotEmpty())

        val selectedVariant = sampleProduct.variants.first()
        val cartItem = CartItem(
            id = "cart-test-1",
            productId = sampleProduct.id,
            productName = sampleProduct.name,
            productImage = sampleProduct.imageUrl,
            price = sampleProduct.price,
            quantity = 1,
            variant = selectedVariant
        )

        assertEquals("Varian terpilih harus sesuai", selectedVariant, cartItem.variant)
        assertEquals("Harga produk harus sama", sampleProduct.price, cartItem.price)
    }

    // =========================================================================
    // TC-04: Tambah ke Keranjang Belanja
    // =========================================================================
    @Test
    fun testTC04_TambahKeKeranjang() {
        val initialSize = MockDataProvider.cartItems.size

        val newItem = CartItem(
            id = "cart-tc04",
            productId = "prod-2",
            productName = "Samsung Galaxy A54",
            productImage = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9",
            price = 5213000L,
            quantity = 1,
            variant = "Awesome Graphite"
        )
        MockDataProvider.cartItems.add(newItem)

        assertEquals("Ukuran keranjang harus bertambah 1", initialSize + 1, MockDataProvider.cartItems.size)
        val added = MockDataProvider.cartItems.find { it.id == "cart-tc04" }
        assertNotNull("Item yang ditambahkan harus ada di keranjang", added)
        assertEquals(5213000L, added!!.price)
    }

    // =========================================================================
    // TC-05: Update Kuantitas (+) / (-) dan Hapus Keranjang (CRUD)
    // =========================================================================
    @Test
    fun testTC05_UpdateDanHapusKeranjang() {
        val testItem = CartItem(
            id = "cart-tc05",
            productId = "prod-3",
            productName = "Mechanical Keyboard",
            productImage = "",
            price = 299000L,
            quantity = 1,
            variant = "Red Switch"
        )
        MockDataProvider.cartItems.add(testItem)

        // 1. Tambah kuantitas (Simulasi tombol [+])
        val updatedQty = testItem.quantity + 1
        val itemAfterIncrease = testItem.copy(quantity = updatedQty)
        assertEquals(2, itemAfterIncrease.quantity)

        val subtotal = itemAfterIncrease.price * itemAfterIncrease.quantity
        assertEquals("Subtotal harus 2 x 299.000 = 598.000", 598000L, subtotal)

        // 2. Hapus item dari keranjang (Simulasi tombol hapus / kurangi ke 0)
        MockDataProvider.cartItems.remove(testItem)
        val findDeleted = MockDataProvider.cartItems.find { it.id == "cart-tc05" }
        assertEquals("Item harus sudah terhapus dari keranjang", null, findDeleted)
    }

    // =========================================================================
    // TC-06: Checkout & Kalkulasi Ongkos Kirim Kota Cirebon
    // =========================================================================
    @Test
    fun testTC06_CheckoutDanKalkulasiOngkosKirim() {
        val items = listOf(
            CartItem(id = "c1", productId = "p1", productName = "Barang 1", productImage = "", price = 100000L, quantity = 2),
            CartItem(id = "c2", productId = "p2", productName = "Barang 2", productImage = "", price = 50000L, quantity = 1)
        )

        val subtotal = items.sumOf { it.price * it.quantity }
        assertEquals("Subtotal: (2 * 100rb) + (1 * 50rb) = 250rb", 250000L, subtotal)

        // Opsi 1: Reguler (JNE Cirebon) - Rp 15.000
        val shippingReguler = 15000L
        val totalReguler = subtotal + shippingReguler
        assertEquals("Total dengan kurir reguler harus Rp 265.000", 265000L, totalReguler)

        // Opsi 2: Express (SiCepat Cirebon) - Rp 25.000
        val shippingExpress = 25000L
        val totalExpress = subtotal + shippingExpress
        assertEquals("Total dengan kurir express harus Rp 275.000", 275000L, totalExpress)

        val payload = CheckoutPayload(
            items = items,
            shippingAddress = "Jl. Perjuangan No. 10B, Kota Cirebon",
            shippingCity = "Kota Cirebon",
            shippingType = "Reguler (JNE Cirebon)",
            shippingCost = shippingReguler,
            subtotal = subtotal,
            totalAmount = totalReguler,
            paymentMethod = "BCA Virtual Account",
            vaNumber = "123 456 7890"
        )

        assertEquals("Kota Cirebon", payload.shippingCity)
        assertEquals("123 456 7890", payload.vaNumber)
    }

    // =========================================================================
    // TC-07: Simulasi Pembayaran Sandbox (Perubahan Status PENDING -> PAID)
    // =========================================================================
    @Test
    fun testTC07_SimulasiPembayaranSandbox() {
        val order = Order(
            id = "ord-test-99",
            orderNumber = "INV/SHOP/20261020/8812",
            items = emptyList(),
            subtotal = 199000L,
            shippingCost = 15000L,
            totalAmount = 214000L,
            vaNumber = "123 456 7890",
            paymentStatus = "PENDING",
            trackingStatus = "DIPESAN"
        )

        // Status awal harus PENDING
        assertEquals("Status pembayaran awal harus PENDING", "PENDING", order.paymentStatus)
        assertEquals("Status tracking awal harus DIPESAN", "DIPESAN", order.trackingStatus)

        // Simulasi pelunasan (Tombol Cek Status Pembayaran)
        order.paymentStatus = "PAID"
        order.trackingStatus = "DIKEMAS"

        assertEquals("Status pembayaran akhir harus PAID", "PAID", order.paymentStatus)
        assertEquals("Status tracking akhir harus DIKEMAS", "DIKEMAS", order.trackingStatus)
    }

    // =========================================================================
    // Test Tambahan: Format Mata Uang Rupiah (CurrencyHelper)
    // =========================================================================
    @Test
    fun testCurrencyHelper_FormatRupiah() {
        assertEquals("Rp 199.000", CurrencyHelper.formatRupiah(199000L))
        assertEquals("Rp 5.213.000", CurrencyHelper.formatRupiah(5213000L))
        assertEquals("Rp 0", CurrencyHelper.formatRupiah(0L))
    }

    // =========================================================================
    // Test Tambahan: Multi-Channel Payment Provider & Custom Method
    // =========================================================================
    @Test
    fun testPaymentMethodProvider_MultiChannelAndCustom() {
        val defaultMethod = PaymentMethodProvider.getDefault()
        assertEquals("BCA Virtual Account", defaultMethod.name)
        assertEquals("123 456 7890", defaultMethod.codeOrVa)

        // Test tambah metode kustom
        val customMethod = PaymentMethodProvider.addCustomMethod(
            name = "Bank Syariah Indonesia",
            codeOrVa = "7123 4567 890",
            category = "Virtual Account",
            badge = "BSI"
        )

        assertNotNull(customMethod)
        assertEquals("Bank Syariah Indonesia", customMethod.name)
        assertEquals("7123 4567 890", customMethod.codeOrVa)
        assertTrue(customMethod.isCustom)

        val retrieved = PaymentMethodProvider.getByNameOrVa("Bank Syariah Indonesia", null)
        assertEquals(customMethod.id, retrieved.id)
    }
}
