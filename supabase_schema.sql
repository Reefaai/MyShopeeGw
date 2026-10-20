-- =========================================================================
-- SKRIP SQL SUPABASE UNTUK PROJEK UAS SHOPIKU (STMIK IKMI CIREBON)
-- Jalankan skrip ini di: Supabase Dashboard -> SQL Editor -> New Query -> Run
-- =========================================================================

-- 1. Buat Tabel Produk (products)
CREATE TABLE IF NOT EXISTS products (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    category TEXT NOT NULL,
    price BIGINT NOT NULL,
    original_price BIGINT NOT NULL,
    rating NUMERIC(3, 1) DEFAULT 5.0,
    sold_count INT DEFAULT 0,
    image_url TEXT,
    description TEXT,
    variants JSONB DEFAULT '[]'::jsonb,
    stock INT DEFAULT 100,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 2. Buat Tabel Keranjang Belanja (cart)
CREATE TABLE IF NOT EXISTS cart (
    id TEXT PRIMARY KEY,
    product_id TEXT NOT NULL,
    product_name TEXT NOT NULL,
    product_image TEXT,
    price BIGINT NOT NULL,
    quantity INT DEFAULT 1,
    variant TEXT DEFAULT '',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 3. Buat Tabel Pesanan & Checkout (orders)
CREATE TABLE IF NOT EXISTS orders (
    id TEXT PRIMARY KEY,
    order_number TEXT NOT NULL,
    items JSONB DEFAULT '[]'::jsonb,
    shipping_address TEXT,
    shipping_city TEXT DEFAULT 'Kota Cirebon',
    shipping_type TEXT DEFAULT 'Reguler (JNE Cirebon)',
    shipping_cost BIGINT DEFAULT 15000,
    subtotal BIGINT NOT NULL,
    total_amount BIGINT NOT NULL,
    va_number TEXT DEFAULT '123 456 7890',
    payment_status TEXT DEFAULT 'PENDING',
    tracking_status TEXT DEFAULT 'DIKEMAS',
    courier_resi TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT timezone('utc'::text, now()) NOT NULL
);

-- 4. Aktifkan Row Level Security (RLS) & Berikan Akses Anonim (Full CRUD untuk Demo Android)
ALTER TABLE products ENABLE ROW LEVEL SECURITY;
ALTER TABLE cart ENABLE ROW LEVEL SECURITY;
ALTER TABLE orders ENABLE ROW LEVEL SECURITY;

CREATE POLICY "Public Read Products" ON products FOR SELECT USING (true);
CREATE POLICY "Public Manage Cart" ON cart FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "Public Manage Orders" ON orders FOR ALL USING (true) WITH CHECK (true);

-- 5. Seed Data Katalog Multi-Kategori & Khas Cirebon
INSERT INTO products (id, name, category, price, original_price, rating, sold_count, image_url, description, variants, stock)
VALUES
('prod-1', 'JBL TWS Bluetooth Wave 100 Deep Bass Wireless', 'elektronik', 199000, 399000, 4.8, 3420,
 'https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=600&auto=format&fit=crop&q=80',
 'Earphone TWS JBL Wave 100 dengan teknologi JBL Deep Bass Sound. Daya tahan baterai hingga 20 jam, desain ergonomis pas dan nyaman di telinga.',
 '["Hitam Doff", "Putih Ivory", "Sage Green"]'::jsonb, 45),

('prod-2', 'Samsung Galaxy A54 5G 8/256GB Layar Super AMOLED 120Hz', 'elektronik', 5213000, 5999000, 4.9, 1250,
 'https://images.unsplash.com/photo-1610945415295-d9bbf067e59c?w=600&auto=format&fit=crop&q=80',
 'Smartphone premium kelas menengah Samsung Galaxy A54 5G dengan kamera utama 50MP OIS, baterai 5000mAh, rating IP67 tahan air & debu.',
 '["Awesome Graphite", "Awesome Lime", "Awesome White"]'::jsonb, 20),

('prod-3', 'Batik Tulis Sutra Mega Mendung Asli Khas Trusmi Cirebon', 'cirebon', 185000, 250000, 4.9, 890,
 'https://images.unsplash.com/photo-1607344645866-009c320b5ab8?w=600&auto=format&fit=crop&q=80',
 'Kain Batik motif Megamendung asli buatan pengrajin Trusmi Plered Cirebon. Bahan katun primissima super halus, adem, dan tidak luntur.',
 '["Merah Marun", "Biru Klasik", "Sage Gold"]'::jsonb, 35),

('prod-4', 'Sirup Tjampolay Khas Cirebon Botol Kaca 630ml (Rasa Pisang Susu)', 'cirebon', 35000, 42000, 5.0, 5600,
 'https://images.unsplash.com/photo-1546173159-315724a31696?w=600&auto=format&fit=crop&q=80',
 'Sirup legendaris kebanggaan Kota Cirebon sejak 1936! Terbuat dari gula tebu murni dengan aroma Pisang Susu khas yang menyegarkan.',
 '["Pisang Susu", "Rozen Mawar", "Jeruk Nipis"]'::jsonb, 120),

('prod-5', 'Sneakers Canvas Low-Top Vulcanized Casual All-Match', 'sepatu', 149000, 229000, 4.7, 2100,
 'https://images.unsplash.com/photo-1525966222134-fcfa99b8ae77?w=600&auto=format&fit=crop&q=80',
 'Sepatu sneakers kanvas sol karet mentah anti-selip. Insole empuk foam latex tahan seharian. Cocok untuk kuliah dan nongkrong.',
 '["Ukuran 40", "Ukuran 41", "Ukuran 42", "Ukuran 43"]'::jsonb, 60),

('prod-6', 'Hoodie Boxy Oversize Heavyweight French Terry 330gsm', 'fashion', 129000, 199000, 4.8, 4320,
 'https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=600&auto=format&fit=crop&q=80',
 'Hoodie potongan boxy modern dengan bahan katun French Terry tebal dan lembut. Tali hoodie tanpa logam, saku kangaroo luas.',
 '["Sage Green", "Charcoal Black", "Oatmeal Beige"]'::jsonb, 50),

('prod-7', 'Kerupuk Melarat Gurih Renyah Khas Plered Cirebon 250gr', 'cirebon', 22000, 28000, 4.9, 1800,
 'https://images.unsplash.com/photo-1599488615731-7e5c2823ff28?w=600&auto=format&fit=crop&q=80',
 'Kerupuk khas Cirebon digoreng dengan pasir kali panas (non-kolesterol). Disajikan dengan sambal asam pedas terasi pesisir Cirebon.',
 '["Sambal Asam Pedas", "Sambal Terasi", "Original"]'::jsonb, 85),

('prod-8', 'Smartwatch Pro AMOLED Display Bluetooth Call & SpO2 Tracker', 'elektronik', 329000, 549000, 4.7, 980,
 'https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=600&auto=format&fit=crop&q=80',
 'Smartwatch pintar dengan layar lengkung HD AMOLED, lebih dari 100 mode olahraga, sensor detak jantung 24 jam.',
 '["Midnight Black", "Sage Leather", "Silver Steel"]'::jsonb, 25),

('prod-9', 'Tumbler Stainless Steel 500ml Insulated Double-Wall', 'hobi', 79000, 120000, 4.8, 2750,
 'https://images.unsplash.com/photo-1577937927133-66ef06acdf18?w=600&auto=format&fit=crop&q=80',
 'Tumbler termos kopi bahan Food Grade SUS 304, tahan panas hingga 8 jam dan tahan dingin es batu hingga 14 jam.',
 '["Matte Matcha", "Matte Charcoal", "Pure Cream"]'::jsonb, 70),

('prod-10', 'Mechanical Keyboard 65% Wireless Tri-Mode Hot-Swappable', 'elektronik', 299000, 450000, 4.9, 640,
 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=600&auto=format&fit=crop&q=80',
 'Keyboard mekanikal kompak 68 tombol dengan koneksi Bluetooth 5.0, Dongle 2.4Ghz, dan kabel Type-C. Switch Red Linear yang lembut.',
 '["Red Switch (Silent)", "Brown Switch (Tactile)", "Blue Switch (Clicky)"]'::jsonb, 30)
ON CONFLICT (id) DO NOTHING;
