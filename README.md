# ShopiKu — Mobile Marketplace (Shopee Cloning & Redesign)
**Projek Ujian Akhir Semester (UAS) — Pemrograman Mobile Dasar**  
**Program Studi Rekayasa Perangkat Lunak (S1) — STMIK IKMI Cirebon**  
*Tahun Akademik: Semester Ganjil 2026/2027*  
*Dosen Pengampu: Rosidin, S. Kom., M. Kom.*

---

## 📱 Tentang Projek

**ShopiKu** adalah aplikasi mobile e-commerce modern berbasis Android Kotlin yang mengadopsi alur fungsional toko online terkemuka (Shopee). Sesuai dengan ketentuan umum pada Panduan UAS, aplikasi ini **bukan sekadar salinan visual mentah**, melainkan dirancang ulang (*UI/UX Redesign & Enhancement*) mengusung tema **Neo-Minimalist Clean Commerce** dengan palet warna **Sage Green, Matcha Mint & Deep Black Metal**.

Aplikasi telah memenuhi seluruh **12 Layar Antarmuka**, **7 Alur Fungsional Wajib**, **Arsitektur MVVM (Model-View-ViewModel)**, dan **Integrasi Live Cloud Backend Supabase (PostgREST API)**.

---

## 🎨 Filosofi Desain & Inovasi UI/UX (*Redesign*)

* **Menghilangkan Visual Clutter**: Shopee asli memiliki tata letak yang padat, penuh banner bertumpuk, dan warna oranye neon yang cepat membuat lelah mata. ShopiKu mendesain ulang antarmuka menjadi bersih, lega (*comfortable whitespace*), dan elegan.
* **Palet Warna Neo-Minimalist**:
  * **Primary CTA & Typography**: `#050504` (*Deep Black Metal*)
  * **Interactive Accent**: `#CEE9BD` (*Fresh Matcha*) & `#BCC5AD` (*Sage*)
  * **Screen Surface**: `#F9F7F6` (*Lynx White*) & `#FFFFFF`
  * **Promo / Urgent Badge**: `#FF5722` (*Warm Coral*)
* **Sentuhan Lokal Cirebon**: Alur checkout dan tracking pesanan diintegrasikan dengan konteks lokal Kota Cirebon (Pengiriman dari Hub Logistik Cirebon, kurir JNE Cirebon, komoditas khas seperti Batik Trusmi dan Sirup Tjampolay).

---

## 🌟 12 Layar Antarmuka (*Blueprint ShopiKu*)

1. **Layar 1: Splash Screen** (`SplashActivity`) — Branding modern ShopiKu, animasi pembuka, dan validasi sesi user.
2. **Layar 2: Login / Register** (`AuthActivity`) — Segmented tab Masuk/Daftar, validasi form, dan social login (Google/Facebook).
3. **Layar 3: Beranda (Home)** (`HomeFragment`) — Search bar, saldo ShopiPay Cirebon, promo banner UAS diskon 70%, kategori cepat, flash sale timer, dan rekomendasi produk.
4. **Layar 4: Kategori Produk** (`CategoryFragment`) — Eksplorasi 2-kolom kategori (Elektronik, Fashion, Sepatu, Khas Cirebon, Hobi & Rumah).
5. **Layar 5: Daftar Produk (Katalog)** (`ProductListActivity`) — Grid 2-kolom responsif dengan pencarian instan (*real-time search*) tanpa reload layar.
6. **Layar 6: Detail Produk** (`ProductDetailActivity`) — Gambar produk, harga diskon, pemilih varian (warna/ukuran), deskripsi, ulasan ringkas, tombol *+ Keranjang* dan *Beli Sekarang*.
7. **Layar 7: Keranjang Belanja** (`CartActivity`) — Full CRUD keranjang (update kuantitas `[-]`/`[+]`, hapus item, centang checkout, subtotal dinamis).
8. **Layar 8: Checkout** (`CheckoutActivity`) — Pilihan alamat Kota Cirebon, opsi pengiriman Reguler (JNE) / Express (SiCepat), rincian tagihan lengkap.
9. **Layar 9: Pembayaran Sandbox** (`PaymentSandboxActivity`) — Nomor Virtual Account BCA (`123 456 7890`), countdown timer mundur 24 jam, tombol *Cek Status Pembayaran* (simulasi pelunasan berhasil).
10. **Layar 10: Tracking Pesanan** (`OrderTrackingActivity`) — Visual Stepper timeline pelacakan paket (*Pesanan Dibuat ➔ Lunas ➔ Dikemas ➔ Dikirim JNE Hub Cirebon*).
11. **Layar 11: Profil / Akun Saya** (`ProfileFragment`) — Data diri user, ringkasan status belanja, dialog konfigurasi Supabase REST API live, dan tombol Logout.
12. **Layar 12: Ulasan Produk** (`ReviewListActivity`) — Ringkasan rating bintang agregat 4.8 / 5.0, breakdown bintang, dan feedback pelanggan beserta foto.

---

## ⚙️ Alur 7 Operasi Wajib (Katalog ➔ Cart ➔ Sandbox)

| No | Spesifikasi Operasi | Implementasi di ShopiKu |
|:--:|:---|:---|
| 1 | **Menampilkan Produk** | Data multi-kategori tampil dalam Grid 2 Kolom lengkap dengan gambar, nama, format harga Rp, dan rating bintang. |
| 2 | **Pencarian Produk Instan** | Search bar menggunakan `TextWatcher` untuk menyaring produk seketika tanpa refresh layar. |
| 3 | **Memilih Varian Produk** | Pemilihan opsi warna/ukuran menggunakan `ChipGroup` interaktif di layar detail. |
| 4 | **Masukkan ke Keranjang** | Tombol *+ Keranjang* mengeksekusi operasi `POST /api/cart` ke database keranjang. |
| 5 | **Ubah Jumlah di Keranjang (CRUD)** | Tombol `[-]` dan `[+]` mengupdate kuantitas (`PUT`), tombol tong sampah menghapus item (`DELETE`), checkbox memilih item belanja. |
| 6 | **Checkout Pesanan** | Menentukan alamat pengiriman Kota Cirebon, kurir Reguler/Express, dan menghitung otomatis total tagihan. |
| 7 | **Pembayaran Sandbox** | Menampilkan Virtual Account BCA (`123 456 7890`), timer mundur, dan konfirmasi pelunasan sukses via tombol verifikasi. |

---

## 🏗️ Arsitektur & Teknologi

* **Bahasa**: Kotlin (Coroutines, Flow/LiveData)
* **Pola Desain**: Clean MVVM (Model - View - ViewModel) + Repository Pattern
* **Networking**: Retrofit 2 + Gson Converter + OkHttp Logging Interceptor
* **Image Loading**: Glide
* **UI Framework**: Android Material Components 3, ViewBinding, ShapeableImageView, NestedScrollView
* **Backend Cloud**: Supabase (PostgreSQL + PostgREST) dengan skrip inisialisasi `supabase_schema.sql`

---

## 🚀 Panduan Setup Database Supabase

1. Buka dashboard [supabase.com](https://supabase.com) dan buat project baru.
2. Buka menu **SQL Editor** pada sidebar kiri.
3. Buka berkas `supabase_schema.sql` yang ada di root direktori projek ini.
4. Salin seluruh isi SQL ke editor Supabase, lalu tekan tombol **Run**.
5. Tabel `products`, `cart`, dan `orders` beserta seed data multi-kategori otomatis terbentuk dengan akses REST API aktif!
6. Salin **Project URL** dan **Anon Key** dari menu *Project Settings > API*, lalu masukkan melalui menu **Profil > Integrasi Supabase Cloud** pada aplikasi ShopiKu.

---

## 📦 Berkas Installer APK

File APK siap install untuk diuji di smartphone fisik Android:
* Lokasi: `app/build/outputs/apk/debug/app-debug.apk`
* Ukuran: ~6.9 MB (Ringan & Cepat)
