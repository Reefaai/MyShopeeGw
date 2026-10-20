package com.project.myshopeegw.data.api

import com.project.myshopeegw.R
import com.project.myshopeegw.data.model.CartItem
import com.project.myshopeegw.data.model.Category
import com.project.myshopeegw.data.model.Product
import com.project.myshopeegw.data.model.Review

object MockDataProvider {

    val categories = listOf(
        Category("all", "Semua", R.drawable.ic_category, 10),
        Category("elektronik", "Elektronik", R.drawable.ic_gadget, 4),
        Category("fashion", "Fashion", R.drawable.ic_fashion, 1),
        Category("sepatu", "Sepatu", R.drawable.ic_shoes, 1),
        Category("cirebon", "Khas Cirebon", R.drawable.ic_cirebon, 3),
        Category("hobi", "Hobi & Rumah", R.drawable.ic_hobby, 1)
    )

    private val initialProducts = listOf(
        Product(
            id = "prod-1",
            name = "JBL TWS Bluetooth Wave 100 Deep Bass Wireless",
            category = "elektronik",
            price = 199000L,
            originalPrice = 399000L,
            rating = 4.8f,
            soldCount = 3420,
            imageUrl = "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=600&auto=format&fit=crop&q=80",
            description = "Earphone TWS JBL Wave 100 dengan teknologi JBL Deep Bass Sound. Daya tahan baterai hingga 20 jam (5 jam earbud + 15 jam charging case). Desain ergonomis pas dan nyaman di telinga, konektivitas dual connect, dan mikrofon jernih untuk panggilan telepon.",
            variants = listOf("Hitam Doff", "Putih Ivory", "Sage Green"),
            stock = 45
        ),
        Product(
            id = "prod-2",
            name = "Samsung Galaxy A54 5G 8/256GB Layar Super AMOLED 120Hz",
            category = "elektronik",
            price = 5213000L,
            originalPrice = 5999000L,
            rating = 4.9f,
            soldCount = 1250,
            imageUrl = "https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=600&auto=format&fit=crop&q=80",
            description = "Smartphone premium kelas menengah Samsung Galaxy A54 5G dengan kamera utama 50MP OIS, baterai 5000mAh, rating IP67 tahan air & debu. Desain kaca premium dan performa kencang untuk gaming & multitasking.",
            variants = listOf("Awesome Graphite", "Awesome Lime", "Awesome White"),
            stock = 20
        ),
        Product(
            id = "prod-3",
            name = "Batik Tulis Sutra Mega Mendung Asli Khas Trusmi Cirebon",
            category = "cirebon",
            price = 185000L,
            originalPrice = 250000L,
            rating = 4.9f,
            soldCount = 890,
            imageUrl = "https://images.unsplash.com/photo-1607344645866-009c320b5ab8?w=600&auto=format&fit=crop&q=80",
            description = "Kain Batik motif Megamendung asli buatan pengrajin Trusmi Plered Cirebon. Bahan katun primissima super halus, adem, tidak luntur, dan cocok untuk seragam formal, kebaya, maupun kemeja kantor.",
            variants = listOf("Merah Marun", "Biru Klasik", "Sage Gold"),
            stock = 35
        ),
        Product(
            id = "prod-4",
            name = "Sirup Tjampolay Khas Cirebon Botol Kaca 630ml (Rasa Pisang Susu)",
            category = "cirebon",
            price = 35000L,
            originalPrice = 42000L,
            rating = 5.0f,
            soldCount = 5600,
            imageUrl = "https://images.unsplash.com/photo-1546173159-315724a31696?w=600&auto=format&fit=crop&q=80",
            description = "Sirup legendaris kebanggaan Kota Cirebon sejak 1936! Terbuat dari gula tebu murni dengan aroma Pisang Susu khas yang menyegarkan. Wajib dicoba disajikan dingin bersama es batu atau kelapa muda.",
            variants = listOf("Pisang Susu", "Rozen Mawar", "Jeruk Nipis"),
            stock = 120
        ),
        Product(
            id = "prod-5",
            name = "Sneakers Canvas Low-Top Vulcanized Casual All-Match",
            category = "sepatu",
            price = 149000L,
            originalPrice = 229000L,
            rating = 4.7f,
            soldCount = 2100,
            imageUrl = "https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?w=600&auto=format&fit=crop&q=80",
            description = "Sepatu sneakers kanvas sol karet mentah anti-selip. Insole empuk foam latex tahan seharian. Cocok untuk kuliah, nongkrong santai, dan gaya streetwear minimalis.",
            variants = listOf("Ukuran 40", "Ukuran 41", "Ukuran 42", "Ukuran 43"),
            stock = 60
        ),
        Product(
            id = "prod-6",
            name = "Hoodie Boxy Oversize Heavyweight French Terry 330gsm",
            category = "fashion",
            price = 129000L,
            originalPrice = 199000L,
            rating = 4.8f,
            soldCount = 4320,
            imageUrl = "https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=600&auto=format&fit=crop&q=80",
            description = "Hoodie potongan boxy modern dengan bahan katun French Terry tebal dan lembut. Tali hoodie tanpa logam minimalis, saku kangaroo luas, dan jahitan rantai rapi berstandar ekspor.",
            variants = listOf("Sage Green", "Charcoal Black", "Oatmeal Beige"),
            stock = 50
        ),
        Product(
            id = "prod-7",
            name = "Kerupuk Melarat Gurih Renyah Khas Plered Cirebon 250gr",
            category = "cirebon",
            price = 22000L,
            originalPrice = 28000L,
            rating = 4.9f,
            soldCount = 1800,
            imageUrl = "https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?w=600&auto=format&fit=crop&q=80",
            description = "Kerupuk khas Cirebon yang digoreng menggunakan pasir kali pilihan yang dipanaskan (non-kolesterol). Disajikan dengan sambal asam pedas manis terasi khas pesisir Cirebon.",
            variants = listOf("Sambal Asam Pedas", "Sambal Terasi", "Original"),
            stock = 85
        ),
        Product(
            id = "prod-8",
            name = "Smartwatch Pro AMOLED Display Bluetooth Call & SpO2 Tracker",
            category = "elektronik",
            price = 329000L,
            originalPrice = 549000L,
            rating = 4.7f,
            soldCount = 980,
            imageUrl = "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=600&auto=format&fit=crop&q=80",
            description = "Smartwatch pintar dengan layar lengkung HD AMOLED, lebih dari 100 mode olahraga, sensor detak jantung 24 jam, dan fitur Always On Display (AOD). Baterai tahan hingga 7 hari pemakaian normal.",
            variants = listOf("Midnight Black", "Sage Leather", "Silver Steel"),
            stock = 25
        ),
        Product(
            id = "prod-9",
            name = "Tumbler Stainless Steel 500ml Insulated Double-Wall",
            category = "hobi",
            price = 79000L,
            originalPrice = 120000L,
            rating = 4.8f,
            soldCount = 2750,
            imageUrl = "https://images.unsplash.com/photo-1577937927133-66ef06acdf18?w=600&auto=format&fit=crop&q=80",
            description = "Tumbler termos kopi bahan Food Grade SUS 304, tahan panas hingga 8 jam dan tahan dingin es batu hingga 14 jam. Tutup anti-bocor dengan sedotan silikon tersembunyi.",
            variants = listOf("Matte Matcha", "Matte Charcoal", "Pure Cream"),
            stock = 70
        ),
        Product(
            id = "prod-10",
            name = "Mechanical Keyboard 65% Wireless Tri-Mode Hot-Swappable",
            category = "elektronik",
            price = 299000L,
            originalPrice = 450000L,
            rating = 4.9f,
            soldCount = 640,
            imageUrl = "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=600&auto=format&fit=crop&q=80",
            description = "Keyboard mekanikal kompak 68 tombol dengan koneksi Bluetooth 5.0, Dongle 2.4Ghz, dan kabel Type-C. Switch Red Linear yang lembut dan empuk untuk mengetik tugas kuliah maupun gaming santai.",
            variants = listOf("Red Switch (Silent)", "Brown Switch (Tactile)", "Blue Switch (Clicky)"),
            stock = 30
        )
    )

    // In-memory data store for live CRUD during execution
    val products = initialProducts.toMutableList()

    val cartItems = mutableListOf(
        CartItem(
            id = "cart-1",
            productId = "prod-1",
            productName = "JBL TWS Bluetooth Wave 100",
            productImage = "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=600&auto=format&fit=crop&q=80",
            price = 199000L,
            quantity = 1,
            variant = "Hitam Doff",
            isSelected = true
        ),
        CartItem(
            id = "cart-2",
            productId = "prod-2",
            productName = "Samsung Galaxy A54 5G 8/256GB",
            productImage = "https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=600&auto=format&fit=crop&q=80",
            price = 5213000L,
            quantity = 1,
            variant = "Awesome Lime",
            isSelected = true
        )
    )

    val reviews = listOf(
        Review(
            id = "rev-1",
            productId = "prod-1",
            userName = "Andi Pratama (Mahasiswa IKMI)",
            userAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100&auto=format&fit=crop&q=80",
            rating = 5.0f,
            date = "18 Okt 2026",
            variant = "Hitam Doff",
            comment = "Barang original 100%! Bass-nya mantap bulet, suara vocal jernih. Pengiriman ke Kesambi Cirebon cuma 1 hari sampai, packing rapi pakai bubble tebal.",
            reviewImage = "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=400&auto=format&fit=crop&q=80"
        ),
        Review(
            id = "rev-2",
            productId = "prod-1",
            userName = "Rina Kusuma Wardhani",
            userAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100&auto=format&fit=crop&q=80",
            rating = 5.0f,
            date = "15 Okt 2026",
            variant = "Sage Green",
            comment = "Warna sage green-nya cantik banget sesuai selera! Baterainya awet dipakai nonton drakor seharian belum habis. Recommended seller!",
            reviewImage = ""
        ),
        Review(
            id = "rev-3",
            productId = "prod-1",
            userName = "Budi Santoso",
            userAvatar = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=100&auto=format&fit=crop&q=80",
            rating = 4.0f,
            date = "12 Okt 2026",
            variant = "Hitam Doff",
            comment = "Kualitas audio bagus banget di harga 199rb, ada sedikit delay kalau buat game kompetitif tapi untuk musik & zoom meeting sangat memuaskan.",
            reviewImage = ""
        )
    )
}
