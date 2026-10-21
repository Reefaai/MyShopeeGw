import zipfile
import os

def generate_code_docx(output_path):
    content_types = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types">
  <Default Extension="rels" ContentType="application/vnd.openxmlformats-package.relationships+xml"/>
  <Default Extension="xml" ContentType="application/xml"/>
  <Override PartName="/word/document.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"/>
  <Override PartName="/word/styles.xml" ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"/>
</Types>"""

    rels = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/>
</Relationships>"""

    doc_rels = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">
  <Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>
</Relationships>"""

    styles = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:styles xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:docDefaults>
    <w:rPrDefault>
      <w:rPr>
        <w:rFonts w:ascii="Times New Roman" w:hAnsi="Times New Roman" w:cs="Times New Roman"/>
        <w:sz w:val="24"/>
        <w:szCs w:val="24"/>
      </w:rPr>
    </w:rPrDefault>
  </w:docDefaults>
</w:styles>"""

    # Code lines structured as (indent_level, [(text, color, is_bold)])
    # Colors: 
    # Keyword: CF222E (Red)
    # Annotation: 8250DF (Purple)
    # String: 0A3069 (Dark Blue)
    # Comment: 6E7781 (Gray italic)
    # Class/Type: 953800 (Orange/Brown)
    # Function/Default: 24292F (Dark Charcoal)

    code_data = [
        [("// Cuplikan Kode Tunggal: Model Data (DTO) & Layanan Jaringan Supabase PostgREST", "6E7781", False, True)],
        [("// File: ShopeeApiService.kt & DTO Model ShopiKu (MyShopeeGw)", "6E7781", False, True)],
        [("", "24292F", False, False)],
        [("// 1. DTO Model Produk & Keranjang Belanja", "6E7781", False, True)],
        [("data ", "CF222E", True, False), ("class ", "CF222E", True, False), ("Product", "953800", True, False), ("(", "24292F", False, False)],
        [("    ", "24292F", False, False), ("@SerializedName", "8250DF", False, False), ("(", "24292F", False, False), ('"id"', "0A3069", False, False), (") ", "24292F", False, False), ("val ", "CF222E", True, False), ("id: ", "24292F", False, False), ("String", "953800", False, False), (",", "24292F", False, False)],
        [("    ", "24292F", False, False), ("@SerializedName", "8250DF", False, False), ("(", "24292F", False, False), ('"name"', "0A3069", False, False), (") ", "24292F", False, False), ("val ", "CF222E", True, False), ("name: ", "24292F", False, False), ("String", "953800", False, False), (",", "24292F", False, False)],
        [("    ", "24292F", False, False), ("@SerializedName", "8250DF", False, False), ("(", "24292F", False, False), ('"category"', "0A3069", False, False), (") ", "24292F", False, False), ("val ", "CF222E", True, False), ("category: ", "24292F", False, False), ("String", "953800", False, False), (",", "24292F", False, False)],
        [("    ", "24292F", False, False), ("@SerializedName", "8250DF", False, False), ("(", "24292F", False, False), ('"price"', "0A3069", False, False), (") ", "24292F", False, False), ("val ", "CF222E", True, False), ("price: ", "24292F", False, False), ("Long", "953800", False, False), (",", "24292F", False, False)],
        [("    ", "24292F", False, False), ("@SerializedName", "8250DF", False, False), ("(", "24292F", False, False), ('"rating"', "0A3069", False, False), (") ", "24292F", False, False), ("val ", "CF222E", True, False), ("rating: ", "24292F", False, False), ("Double", "953800", False, False), (" = ", "24292F", False, False), ("5.0", "0550AE", False, False)],
        [(")", "24292F", False, False), (" : ", "24292F", False, False), ("Serializable", "953800", False, False)],
        [("", "24292F", False, False)],
        [("data ", "CF222E", True, False), ("class ", "CF222E", True, False), ("CartItem", "953800", True, False), ("(", "24292F", False, False)],
        [("    ", "24292F", False, False), ("@SerializedName", "8250DF", False, False), ("(", "24292F", False, False), ('"id"', "0A3069", False, False), (") ", "24292F", False, False), ("val ", "CF222E", True, False), ("id: ", "24292F", False, False), ("String", "953800", False, False), (",", "24292F", False, False)],
        [("    ", "24292F", False, False), ("@SerializedName", "8250DF", False, False), ("(", "24292F", False, False), ('"product_id"', "0A3069", False, False), (") ", "24292F", False, False), ("val ", "CF222E", True, False), ("productId: ", "24292F", False, False), ("String", "953800", False, False), (",", "24292F", False, False)],
        [("    ", "24292F", False, False), ("@SerializedName", "8250DF", False, False), ("(", "24292F", False, False), ('"product_name"', "0A3069", False, False), (") ", "24292F", False, False), ("val ", "CF222E", True, False), ("productName: ", "24292F", False, False), ("String", "953800", False, False), (",", "24292F", False, False)],
        [("    ", "24292F", False, False), ("@SerializedName", "8250DF", False, False), ("(", "24292F", False, False), ('"price"', "0A3069", False, False), (") ", "24292F", False, False), ("val ", "CF222E", True, False), ("price: ", "24292F", False, False), ("Long", "953800", False, False), (",", "24292F", False, False)],
        [("    ", "24292F", False, False), ("@SerializedName", "8250DF", False, False), ("(", "24292F", False, False), ('"quantity"', "0A3069", False, False), (") ", "24292F", False, False), ("var ", "CF222E", True, False), ("quantity: ", "24292F", False, False), ("Int", "953800", False, False), (" = ", "24292F", False, False), ("1", "0550AE", False, False), (",", "24292F", False, False)],
        [("    ", "24292F", False, False), ("@SerializedName", "8250DF", False, False), ("(", "24292F", False, False), ('"variant"', "0A3069", False, False), (") ", "24292F", False, False), ("val ", "CF222E", True, False), ("variant: ", "24292F", False, False), ("String", "953800", False, False), (" = ", "24292F", False, False), ('""', "0A3069", False, False), (",", "24292F", False, False)],
        [("    ", "24292F", False, False), ("@Transient ", "8250DF", False, False), ("var ", "CF222E", True, False), ("isSelected: ", "24292F", False, False), ("Boolean", "953800", False, False), (" = ", "24292F", False, False), ("true", "CF222E", True, False), (" // Local UI State", "6E7781", False, True)],
        [(")", "24292F", False, False), (" : ", "24292F", False, False), ("Serializable", "953800", False, False)],
        [("", "24292F", False, False)],
        [("// 2. Interface Retrofit Service Penghubung 7 Alur Transaksi ke Cloud Supabase", "6E7781", False, True)],
        [("interface ", "CF222E", True, False), ("SupabaseApiService", "953800", True, False), (" {", "24292F", False, False)],
        [("", "24292F", False, False)],
        [("    // Alur 1 & 2: Katalog Produk & Pencarian Cepat", "6E7781", False, True)],
        [("    ", "24292F", False, False), ("@GET", "8250DF", False, False), ("(", "24292F", False, False), ('"rest/v1/products?select=*"', "0A3069", False, False), (")", "24292F", False, False)],
        [("    ", "24292F", False, False), ("suspend fun ", "CF222E", True, False), ("getProducts", "8250DF", False, False), ("(", "24292F", False, False), ("@Query", "8250DF", False, False), ("(", "24292F", False, False), ('"name"', "0A3069", False, False), (") ", "24292F", False, False), ("searchFilter: ", "24292F", False, False), ("String", "953800", False, False), ("? = ", "24292F", False, False), ("null", "CF222E", True, False), ("): ", "24292F", False, False), ("Response", "953800", False, False), ("<", "24292F", False, False), ("List", "953800", False, False), ("<", "24292F", False, False), ("Product", "953800", False, False), (">>", "24292F", False, False)],
        [("", "24292F", False, False)],
        [("    // Alur 4: Mengambil & Menambahkan Produk ke Keranjang Belanja", "6E7781", False, True)],
        [("    ", "24292F", False, False), ("@GET", "8250DF", False, False), ("(", "24292F", False, False), ('"rest/v1/cart?select=*"', "0A3069", False, False), (")", "24292F", False, False)],
        [("    ", "24292F", False, False), ("suspend fun ", "CF222E", True, False), ("getCart", "8250DF", False, False), ("(): ", "24292F", False, False), ("Response", "953800", False, False), ("<", "24292F", False, False), ("List", "953800", False, False), ("<", "24292F", False, False), ("CartItem", "953800", False, False), (">>", "24292F", False, False)],
        [("", "24292F", False, False)],
        [("    ", "24292F", False, False), ("@Headers", "8250DF", False, False), ("(", "24292F", False, False), ('"Prefer: return=representation"', "0A3069", False, False), (")", "24292F", False, False)],
        [("    ", "24292F", False, False), ("@POST", "8250DF", False, False), ("(", "24292F", False, False), ('"rest/v1/cart"', "0A3069", False, False), (")", "24292F", False, False)],
        [("    ", "24292F", False, False), ("suspend fun ", "CF222E", True, False), ("addToCart", "8250DF", False, False), ("(", "24292F", False, False), ("@Body ", "8250DF", False, False), ("item: ", "24292F", False, False), ("CartItem", "953800", False, False), ("): ", "24292F", False, False), ("Response", "953800", False, False), ("<", "24292F", False, False), ("List", "953800", False, False), ("<", "24292F", False, False), ("CartItem", "953800", False, False), (">>", "24292F", False, False)],
        [("", "24292F", False, False)],
        [("    // Alur 5: Mengubah Kuantitas (+/-) & Menghapus Item Keranjang (CRUD)", "6E7781", False, True)],
        [("    ", "24292F", False, False), ("@Headers", "8250DF", False, False), ("(", "24292F", False, False), ('"Prefer: return=representation"', "0A3069", False, False), (")", "24292F", False, False)],
        [("    ", "24292F", False, False), ("@PATCH", "8250DF", False, False), ("(", "24292F", False, False), ('"rest/v1/cart"', "0A3069", False, False), (")", "24292F", False, False)],
        [("    ", "24292F", False, False), ("suspend fun ", "CF222E", True, False), ("updateCartQuantity", "8250DF", False, False), ("(", "24292F", False, False), ("@Query", "8250DF", False, False), ("(", "24292F", False, False), ('"id"', "0A3069", False, False), (") ", "24292F", False, False), ("idFilter: ", "24292F", False, False), ("String", "953800", False, False), (", ", "24292F", False, False), ("@Body ", "8250DF", False, False), ("body: ", "24292F", False, False), ("Map", "953800", False, False), ("<", "24292F", False, False), ("String", "953800", False, False), (", ", "24292F", False, False), ("Int", "953800", False, False), (">): ", "24292F", False, False), ("Response", "953800", False, False), ("<", "24292F", False, False), ("List", "953800", False, False), ("<", "24292F", False, False), ("CartItem", "953800", False, False), (">>", "24292F", False, False)],
        [("", "24292F", False, False)],
        [("    ", "24292F", False, False), ("@DELETE", "8250DF", False, False), ("(", "24292F", False, False), ('"rest/v1/cart"', "0A3069", False, False), (")", "24292F", False, False)],
        [("    ", "24292F", False, False), ("suspend fun ", "CF222E", True, False), ("deleteCartItem", "8250DF", False, False), ("(", "24292F", False, False), ("@Query", "8250DF", False, False), ("(", "24292F", False, False), ('"id"', "0A3069", False, False), (") ", "24292F", False, False), ("idFilter: ", "24292F", False, False), ("String", "953800", False, False), ("): ", "24292F", False, False), ("Response", "953800", False, False), ("<", "24292F", False, False), ("Void", "953800", False, False), (">", "24292F", False, False)],
        [("", "24292F", False, False)],
        [("    // Alur 6 & 7: Menerbitkan Pesanan Checkout & Verifikasi Status Pembayaran", "6E7781", False, True)],
        [("    ", "24292F", False, False), ("@Headers", "8250DF", False, False), ("(", "24292F", False, False), ('"Prefer: return=representation"', "0A3069", False, False), (")", "24292F", False, False)],
        [("    ", "24292F", False, False), ("@POST", "8250DF", False, False), ("(", "24292F", False, False), ('"rest/v1/orders"', "0A3069", False, False), (")", "24292F", False, False)],
        [("    ", "24292F", False, False), ("suspend fun ", "CF222E", True, False), ("createOrder", "8250DF", False, False), ("(", "24292F", False, False), ("@Body ", "8250DF", False, False), ("order: ", "24292F", False, False), ("Map", "953800", False, False), ("<", "24292F", False, False), ("String", "953800", False, False), (", ", "24292F", False, False), ("Any", "953800", False, False), (">): ", "24292F", False, False), ("Response", "953800", False, False), ("<", "24292F", False, False), ("List", "953800", False, False), ("<", "24292F", False, False), ("Map", "953800", False, False), ("<", "24292F", False, False), ("String", "953800", False, False), (", ", "24292F", False, False), ("Any", "953800", False, False), (">>>", "24292F", False, False)],
        [("", "24292F", False, False)],
        [("    ", "24292F", False, False), ("@Headers", "8250DF", False, False), ("(", "24292F", False, False), ('"Prefer: return=representation"', "0A3069", False, False), (")", "24292F", False, False)],
        [("    ", "24292F", False, False), ("@PATCH", "8250DF", False, False), ("(", "24292F", False, False), ('"rest/v1/orders"', "0A3069", False, False), (")", "24292F", False, False)],
        [("    ", "24292F", False, False), ("suspend fun ", "CF222E", True, False), ("updateOrder", "8250DF", False, False), ("(", "24292F", False, False), ("@Query", "8250DF", False, False), ("(", "24292F", False, False), ('"id"', "0A3069", False, False), (") ", "24292F", False, False), ("idFilter: ", "24292F", False, False), ("String", "953800", False, False), (", ", "24292F", False, False), ("@Body ", "8250DF", False, False), ("body: ", "24292F", False, False), ("Map", "953800", False, False), ("<", "24292F", False, False), ("String", "953800", False, False), (", ", "24292F", False, False), ("Any", "953800", False, False), (">): ", "24292F", False, False), ("Response", "953800", False, False), ("<", "24292F", False, False), ("List", "953800", False, False), ("<", "24292F", False, False), ("Map", "953800", False, False), ("<", "24292F", False, False), ("String", "953800", False, False), (", ", "24292F", False, False), ("Any", "953800", False, False), (">>>", "24292F", False, False)],
        [("}", "24292F", False, False)]
    ]

    doc_xml_parts = []
    doc_xml_parts.append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:body>
    <w:p>
      <w:pPr>
        <w:spacing w:before="100" w:after="100"/>
      </w:pPr>
      <w:r>
        <w:rPr>
          <w:b/>
          <w:sz w:val="24"/>
        </w:rPr>
        <w:t>Cuplikan Kode Model &amp; Retrofit API Service (Kotlin):</w:t>
      </w:r>
    </w:p>
    <w:tbl>
      <w:tblPr>
        <w:tblW w:w="9600" w:type="dxa"/>
        <w:jc w:val="center"/>
        <w:tblBorders>
          <w:top w:val="single" w:sz="6" w:space="0" w:color="D0D7DE"/>
          <w:left w:val="single" w:sz="24" w:space="0" w:color="2EA44F"/>
          <w:bottom w:val="single" w:sz="6" w:space="0" w:color="D0D7DE"/>
          <w:right w:val="single" w:sz="6" w:space="0" w:color="D0D7DE"/>
          <w:insideH w:val="none"/>
          <w:insideV w:val="none"/>
        </w:tblBorders>
      </w:tblPr>
      <w:tblGrid>
        <w:gridCol w:w="9600"/>
      </w:tblGrid>
      <w:tr>
        <w:tc>
          <w:tcPr>
            <w:tcW w:w="9600" w:type="dxa"/>
            <w:shd w:val="clear" w:color="auto" w:fill="F6F8FA"/>
            <w:tcMar>
              <w:top w:w="140" w:type="dxa"/>
              <w:left w:w="180" w:type="dxa"/>
              <w:bottom w:w="140" w:type="dxa"/>
              <w:right w:w="180" w:type="dxa"/>
            </w:tcMar>
          </w:tcPr>
""")

    for line_tokens in code_data:
        doc_xml_parts.append("""          <w:p>
            <w:pPr>
              <w:spacing w:line="240" w:lineRule="auto" w:before="0" w:after="0"/>
            </w:pPr>
""")
        if not line_tokens or (len(line_tokens) == 1 and line_tokens[0][0] == ""):
            doc_xml_parts.append("""            <w:r>
              <w:rPr>
                <w:rFonts w:ascii="Consolas" w:hAnsi="Consolas"/>
                <w:sz w:val="19"/>
              </w:rPr>
              <w:t> </w:t>
            </w:r>
""")
        else:
            for text, color, is_bold, is_italic in line_tokens:
                bold_tag = "<w:b/>" if is_bold else ""
                italic_tag = "<w:i/>" if is_italic else ""
                # Escape XML special characters
                safe_text = text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace('"', "&quot;")
                doc_xml_parts.append(f"""            <w:r>
              <w:rPr>
                <w:rFonts w:ascii="Consolas" w:hAnsi="Consolas"/>
                {bold_tag}
                {italic_tag}
                <w:color w:val="{color}"/>
                <w:sz w:val="19"/>
              </w:rPr>
              <w:t xml:space="preserve">{safe_text}</w:t>
            </w:r>
""")
        doc_xml_parts.append("          </w:p>\n")

    doc_xml_parts.append("""        </w:tc>
      </w:tr>
    </w:tbl>
    <w:p/>
  </w:body>
</w:document>""")

    doc_xml = "".join(doc_xml_parts)

    with zipfile.ZipFile(output_path, "w", zipfile.ZIP_DEFLATED) as docx:
        docx.writestr("[Content_Types].xml", content_types)
        docx.writestr("_rels/.rels", rels)
        docx.writestr("word/_rels/document.xml.rels", doc_rels)
        docx.writestr("word/styles.xml", styles)
        docx.writestr("word/document.xml", doc_xml)

    print(f"Generated code docx: {output_path} ({os.path.getsize(output_path)} bytes)")

if __name__ == "__main__":
    generate_code_docx("dokumentasi/Cuplikan_Kode_ShopiKu.docx")
