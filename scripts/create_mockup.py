import os
from PIL import Image, ImageDraw, ImageFont, ImageFilter

def create_phone_mockup(img_path, title, screen_w=480, screen_h=1067, font_path="/usr/share/fonts/TTF/Arialbd.TTF"):
    # Load and resize screenshot
    src_img = Image.open(img_path).convert("RGBA")
    src_img = src_img.resize((screen_w, screen_h), Image.Resampling.LANCZOS)

    # Bezel parameters
    bezel_h = 16
    bezel_v_top = 26
    bezel_v_bottom = 26
    phone_w = screen_w + (bezel_h * 2)
    phone_h = screen_h + bezel_v_top + bezel_v_bottom
    phone_corner_radius = 48
    screen_corner_radius = 32

    # Round the screenshot corners
    mask = Image.new("L", (screen_w, screen_h), 0)
    mask_draw = ImageDraw.Draw(mask)
    mask_draw.rounded_rectangle([0, 0, screen_w, screen_h], radius=screen_corner_radius, fill=255)
    rounded_screen = Image.new("RGBA", (screen_w, screen_h), (0, 0, 0, 0))
    rounded_screen.paste(src_img, (0, 0), mask=mask)

    # Draw phone body
    phone_img = Image.new("RGBA", (phone_w, phone_h), (0, 0, 0, 0))
    pdraw = ImageDraw.Draw(phone_img)

    # Outer frame (Device Body)
    pdraw.rounded_rectangle(
        [0, 0, phone_w, phone_h],
        radius=phone_corner_radius,
        fill="#14171A",
        outline="#2C3238",
        width=2
    )

    # Paste screen
    phone_img.paste(rounded_screen, (bezel_h, bezel_v_top), mask=rounded_screen)

    # Camera punch hole at top
    cam_x = phone_w // 2
    cam_y = bezel_v_top + 16
    cam_r = 7
    pdraw.ellipse([cam_x - cam_r, cam_y - cam_r, cam_x + cam_r, cam_y + cam_r], fill="#0A0B0D", outline="#1F2327", width=1)
    # Camera lens reflection
    pdraw.ellipse([cam_x - 2, cam_y - 2, cam_x, cam_y], fill="#2E4A62")

    # Ear speaker slit at very top
    pdraw.rounded_rectangle([cam_x - 28, 10, cam_x + 28, 13], radius=2, fill="#252A30")

    # Bottom gesture bar indicator
    bar_y = phone_h - 12
    pdraw.rounded_rectangle([cam_x - 36, bar_y, cam_x + 36, bar_y + 3], radius=2, fill="#68737D")

    # Create Title Tag / Pill
    tag_h = 42
    tag_w = phone_w
    tag_img = Image.new("RGBA", (tag_w, tag_h), (0, 0, 0, 0))
    tdraw = ImageDraw.Draw(tag_img)

    # Pill background
    tdraw.rounded_rectangle(
        [4, 2, tag_w - 4, tag_h - 2],
        radius=20,
        fill="#EEF4ED",
        outline="#BCC5AD",
        width=2
    )

    # Text font
    try:
        font = ImageFont.truetype(font_path, 19)
    except Exception:
        font = ImageFont.load_default()

    bbox = tdraw.textbbox((0, 0), title, font=font)
    text_w = bbox[2] - bbox[0]
    text_h = bbox[3] - bbox[1]
    text_x = (tag_w - text_w) // 2
    text_y = (tag_h - text_h) // 2 - 2
    tdraw.text((text_x, text_y), title, fill="#050504", font=font)

    # Combine tag + phone with shadow
    total_w = phone_w + 50
    total_h = tag_h + 16 + phone_h + 50
    combined = Image.new("RGBA", (total_w, total_h), (0, 0, 0, 0))

    # Shadow layer
    shadow = Image.new("RGBA", (total_w, total_h), (0, 0, 0, 0))
    sdraw = ImageDraw.Draw(shadow)
    shadow_x = 25
    shadow_y = tag_h + 16 + 12
    sdraw.rounded_rectangle(
        [shadow_x + 8, shadow_y + 12, shadow_x + phone_w - 8, shadow_y + phone_h - 4],
        radius=phone_corner_radius,
        fill=(0, 0, 0, 75)
    )
    shadow = shadow.filter(ImageFilter.GaussianBlur(16))

    combined.paste(shadow, (0, 0), mask=shadow)
    combined.paste(tag_img, (25, 0), mask=tag_img)
    combined.paste(phone_img, (25, tag_h + 16), mask=phone_img)

    return combined

def main():
    items = [
        ("dokumentasi/Splash_Screen.png", "1. Splash Screen"),
        ("dokumentasi/Login_Register.png", "2. Login / Register"),
        ("dokumentasi/Beranda.png", "3. Beranda (Home)"),
        ("dokumentasi/Kategori_Produk.png", "4. Kategori Produk"),
        ("dokumentasi/Daftar_Produk.png", "5. Daftar Produk"),
        ("dokumentasi/Detail_produk.png", "6. Detail Produk"),
        ("dokumentasi/Keranjang_Belanja.png", "7. Keranjang Belanja"),
        ("dokumentasi/Checkout.png", "8. Checkout"),
        ("dokumentasi/Pembayaran.png", "9. Pembayaran"),
        ("dokumentasi/Tracking_Pesanan.png", "10. Tracking Pesanan"),
        ("dokumentasi/Profil_Akun.png", "11. Profil / Akun"),
        ("dokumentasi/Ulasan_Produk.png", "12. Ulasan Produk"),
    ]

    os.makedirs("dokumentasi/mockups", exist_ok=True)
    phone_widgets = []

    print("Rendering individual phone mockups...")
    for idx, (path, title) in enumerate(items):
        phone_widget = create_phone_mockup(path, title)
        phone_widgets.append(phone_widget)
        # Also save individual standalone mockup
        clean_name = f"{idx+1:02d}_" + os.path.basename(path).replace(".png", "_mockup.png")
        phone_widget.save(os.path.join("dokumentasi/mockups", clean_name), "PNG")
        print(f"  ✓ Saved: {clean_name}")

    # Build 2 Rows x 6 Columns Composite Canvas
    cols = 6
    rows = 2
    single_w, single_h = phone_widgets[0].size

    pad_x = 40
    pad_y = 60
    col_gap = 24
    row_gap = 50

    canvas_w = (pad_x * 2) + (cols * single_w) + ((cols - 1) * col_gap)
    canvas_h = (pad_y * 2) + (rows * single_h) + ((rows - 1) * row_gap) + 120 # extra space for bottom caption

    print(f"Canvas Size: {canvas_w} x {canvas_h}")
    canvas = Image.new("RGBA", (canvas_w, canvas_h), "#FFFFFF")

    # Optional subtle gradient or soft top bar background
    cdraw = ImageDraw.Draw(canvas)
    # Background soft card frame
    cdraw.rounded_rectangle([16, 16, canvas_w - 16, canvas_h - 16], radius=24, fill="#FBFBFD", outline="#E4E7EC", width=2)

    for i, widget in enumerate(phone_widgets):
        r = i // cols
        c = i % cols
        x = pad_x + c * (single_w + col_gap)
        y = pad_y + r * (single_h + row_gap)
        canvas.paste(widget, (x, y), mask=widget)

    # Draw bottom title / caption
    try:
        font_caption = ImageFont.truetype("/usr/share/fonts/TTF/Arialbd.TTF", 34)
        font_sub = ImageFont.truetype("/usr/share/fonts/TTF/Arial.TTF", 24)
    except Exception:
        font_caption = ImageFont.load_default()
        font_sub = ImageFont.load_default()

    caption_text = "Gambar 4.1: Bagan Alur 12 Layar Antarmuka Desain Mobile Marketplace \"ShopiKu\""
    bbox = cdraw.textbbox((0, 0), caption_text, font=font_caption)
    cap_w = bbox[2] - bbox[0]
    cap_x = (canvas_w - cap_w) // 2
    cap_y = canvas_h - 90
    cdraw.text((cap_x, cap_y), caption_text, fill="#1D2939", font=font_caption)

    sub_text = "Implementasi Arsitektur MVVM & Integrasi Live Backend Cloud Supabase — 100% Native Android Kotlin"
    bbox_sub = cdraw.textbbox((0, 0), sub_text, font=font_sub)
    sub_w = bbox_sub[2] - bbox_sub[0]
    sub_x = (canvas_w - sub_w) // 2
    sub_y = cap_y + 44
    cdraw.text((sub_x, sub_y), sub_text, fill="#667085", font=font_sub)

    # Save High-Resolution Composite
    highres_path = "dokumentasi/mockup_12_layar_shopiku_highres.png"
    canvas.save(highres_path, "PNG", optimize=True)
    print(f"✓ High-Res Mockup saved: {highres_path} ({os.path.getsize(highres_path) // 1024} KB)")

    # Save Web / README Optimized version (width = 1920)
    web_w = 2000
    web_h = int(canvas_h * (web_w / canvas_w))
    web_canvas = canvas.resize((web_w, web_h), Image.Resampling.LANCZOS)
    web_path = "dokumentasi/mockup_12_layar_shopiku.png"
    web_canvas.save(web_path, "PNG", optimize=True)
    print(f"✓ Web Mockup saved: {web_path} ({os.path.getsize(web_path) // 1024} KB)")

if __name__ == "__main__":
    main()
