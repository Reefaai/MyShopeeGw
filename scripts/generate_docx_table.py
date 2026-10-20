import zipfile
import os

def create_docx(filename):
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

    table_data = [
        ("Metode", "Endpoint URL", "Perilaku Operasi pada Aplikasi", "HTTP Status"),
        ("GET", "/rest/v1/products?select=*", "Mengambil seluruh daftar produk dan filter kategori", "200 OK"),
        ("GET", "/rest/v1/products?name=ilike.*{q}*", "Pencarian cepat produk berdasarkan kata kunci nama", "200 OK"),
        ("GET", "/rest/v1/cart?select=*", "Mengambil seluruh daftar produk yang ada di keranjang belanja", "200 OK"),
        ("POST", "/rest/v1/cart", "Menambahkan produk baru beserta varian ke keranjang belanja", "201 Created"),
        ("PATCH", "/rest/v1/cart?id=eq.{id}", "Memperbarui jumlah kuantitas produk (+/-) pada keranjang", "200 OK"),
        ("DELETE", "/rest/v1/cart?id=eq.{id}", "Menghapus item dari keranjang atau membersihkan produk yang telah dibayar", "200 OK / 204 No Content"),
        ("POST", "/rest/v1/orders", "Membuat pesanan baru, mengunci ongkos kirim, dan menerbitkan VA Sandbox", "201 Created"),
        ("PATCH", "/rest/v1/orders?id=eq.{id}", "Simulasi pelunasan instan: verifikasi status pesanan menjadi PAID", "200 OK")
    ]

    # Generate document.xml with a formatted table
    doc_xml_parts = []
    doc_xml_parts.append("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
  <w:body>
    <w:p>
      <w:pPr>
        <w:jc w:val="center"/>
      </w:pPr>
      <w:r>
        <w:rPr>
          <w:b/>
          <w:sz w:val="24"/>
        </w:rPr>
        <w:t>Tabel 3.1: Kontrak Antarmuka Layanan Jaringan REST API ShopiKu (Supabase PostgREST)</w:t>
      </w:r>
    </w:p>
    <w:tbl>
      <w:tblPr>
        <w:tblW w:w="9600" w:type="dxa"/>
        <w:jc w:val="center"/>
        <w:tblBorders>
          <w:top w:val="single" w:sz="6" w:space="0" w:color="000000"/>
          <w:left w:val="single" w:sz="6" w:space="0" w:color="000000"/>
          <w:bottom w:val="single" w:sz="6" w:space="0" w:color="000000"/>
          <w:right w:val="single" w:sz="6" w:space="0" w:color="000000"/>
          <w:insideH w:val="single" w:sz="4" w:space="0" w:color="000000"/>
          <w:insideV w:val="single" w:sz="4" w:space="0" w:color="000000"/>
        </w:tblBorders>
      </w:tblPr>
      <w:tblGrid>
        <w:gridCol w:w="1200"/>
        <w:gridCol w:w="3000"/>
        <w:gridCol w:w="3800"/>
        <w:gridCol w:w="1600"/>
      </w:tblGrid>
""")

    for r_idx, row in enumerate(table_data):
        is_header = (r_idx == 0)
        doc_xml_parts.append("      <w:tr>\n")
        if is_header:
            doc_xml_parts.append("        <w:trPr><w:tblHeader/></w:trPr>\n")

        for c_idx, cell_text in enumerate(row):
            # cell width
            widths = [1200, 3000, 3800, 1600]
            w_val = widths[c_idx]
            align = "center" if (c_idx == 0 or c_idx == 3 or is_header) else "left"
            bg_color = "1B2A4A" if is_header else ("FFFFFF" if r_idx % 2 != 0 else "F4F6F9")
            text_color = "FFFFFF" if is_header else "000000"
            is_bold = is_header or (c_idx == 0)

            doc_xml_parts.append(f"""        <w:tc>
          <w:tcPr>
            <w:tcW w:w="{w_val}" w:type="dxa"/>
            <w:shd w:val="clear" w:color="auto" w:fill="{bg_color}"/>
            <w:tcMar>
              <w:top w:w="120" w:type="dxa"/>
              <w:left w:w="140" w:type="dxa"/>
              <w:bottom w:w="120" w:type="dxa"/>
              <w:right w:w="140" w:type="dxa"/>
            </w:tcMar>
            <w:vAlign w:val="center"/>
          </w:tcPr>
          <w:p>
            <w:pPr>
              <w:jc w:val="{align}"/>
              <w:spacing w:line="240" w:lineRule="auto" w:before="60" w:after="60"/>
            </w:pPr>
            <w:r>
              <w:rPr>
                {"<w:b/>" if is_bold else ""}
                <w:color w:val="{text_color}"/>
                <w:sz w:val="21"/>
                <w:szCs w:val="21"/>
              </w:rPr>
              <w:t>{cell_text}</w:t>
            </w:r>
          </w:p>
        </w:tc>
""")
        doc_xml_parts.append("      </w:tr>\n")

    doc_xml_parts.append("""    </w:tbl>
    <w:p/>
  </w:body>
</w:document>""")

    doc_xml = "".join(doc_xml_parts)

    with zipfile.ZipFile(filename, "w", zipfile.ZIP_DEFLATED) as docx:
        docx.writestr("[Content_Types].xml", content_types)
        docx.writestr("_rels/.rels", rels)
        docx.writestr("word/_rels/document.xml.rels", doc_rels)
        docx.writestr("word/styles.xml", styles)
        docx.writestr("word/document.xml", doc_xml)

    print(f"Generated docx: {filename} ({os.path.getsize(filename)} bytes)")

if __name__ == "__main__":
    create_docx("dokumentasi/Tabel_3.1_REST_API_ShopiKu.docx")
