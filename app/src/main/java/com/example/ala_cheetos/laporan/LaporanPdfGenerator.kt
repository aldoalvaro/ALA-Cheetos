package com.example.ala_cheetos.laporan

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import com.example.ala_cheetos.R
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Hasil penyimpanan PDF. [uri] null jika file tidak bisa dibuka langsung (Android 9 ke bawah). */
class HasilPdf(val lokasi: String, val uri: Uri?, val diDownloads: Boolean)

/** Membuat laporan keuangan PDF memakai PdfDocument bawaan Android (tanpa library tambahan). */
object LaporanPdfGenerator {

    private const val PAGE_WIDTH = 595   // A4 @ 72 dpi
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 40f

    /** Menggambar halaman laporan dari data yang sama dengan Dashboard (strings.xml). */
    fun buatPdf(context: Context): PdfDocument {
        val green = ContextCompat.getColor(context, R.color.oscardo_green)
        val red = ContextCompat.getColor(context, R.color.oscardo_red)
        val gray = ContextCompat.getColor(context, R.color.oscardo_gray)
        val line = ContextCompat.getColor(context, R.color.oscardo_line)
        val ink = ContextCompat.getColor(context, R.color.oscardo_ink)

        val pdf = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = pdf.startPage(pageInfo)
        val canvas = page.canvas

        val right = PAGE_WIDTH - MARGIN
        var y = 60f

        // Header
        canvas.drawText(context.getString(R.string.app_name).uppercase(), MARGIN, y, paint(green, 22f, bold = true))
        y += 22f
        canvas.drawText(context.getString(R.string.pdf_title), MARGIN, y, paint(ink, 14f))
        y += 16f
        val dicetak = SimpleDateFormat("d MMMM yyyy, HH:mm", Locale("id", "ID")).format(Date())
        canvas.drawText("Dicetak: $dicetak", MARGIN, y, paint(gray, 10f))
        y += 14f
        drawLine(canvas, y, line)
        y += 28f

        // Ringkasan
        canvas.drawText(context.getString(R.string.pdf_summary), MARGIN, y, paint(ink, 13f, bold = true))
        y += 24f
        val ringkasan = listOf(
            R.string.dash_balance_label to R.string.dash_balance_value,
            R.string.dash_income to R.string.dash_income_value,
            R.string.dash_expense to R.string.dash_expense_value,
            R.string.dash_profit to R.string.dash_profit_value
        )
        for ((label, value) in ringkasan) {
            canvas.drawText(context.getString(label), MARGIN, y, paint(ink, 12f))
            canvas.drawText(context.getString(value), right, y, paint(ink, 12f, bold = true, alignRight = true))
            y += 22f
        }
        y += 4f
        drawLine(canvas, y, line)
        y += 28f

        // Transaksi terbaru
        canvas.drawText(context.getString(R.string.dash_recent), MARGIN, y, paint(ink, 13f, bold = true))
        y += 24f
        val transaksi = listOf(
            Triple(R.string.dash_trx1_title, R.string.dash_trx1_time, R.string.dash_trx1_amount) to green,
            Triple(R.string.dash_trx2_title, R.string.dash_trx2_time, R.string.dash_trx2_amount) to red
        )
        for ((trx, warna) in transaksi) {
            canvas.drawText(context.getString(trx.first), MARGIN, y, paint(ink, 12f))
            canvas.drawText(context.getString(trx.third), right, y, paint(warna, 12f, bold = true, alignRight = true))
            y += 15f
            canvas.drawText(context.getString(trx.second), MARGIN, y, paint(gray, 10f))
            y += 24f
        }

        // Footer
        canvas.drawText(
            context.getString(R.string.footer_copyright),
            PAGE_WIDTH / 2f, PAGE_HEIGHT - 40f, paint(gray, 9f, center = true)
        )

        pdf.finishPage(page)
        return pdf
    }

    /**
     * Simpan ke folder Downloads (Android 10+ lewat MediaStore, tanpa izin storage).
     * Android 9 ke bawah: simpan ke folder Documents milik aplikasi.
     */
    fun simpan(context: Context, pdf: PdfDocument, namaFile: String): HasilPdf? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, namaFile)
                    put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
                    put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    ?: return null
                context.contentResolver.openOutputStream(uri)?.use { pdf.writeTo(it) } ?: return null
                HasilPdf(namaFile, uri, diDownloads = true)
            } else {
                val dir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: return null
                val file = File(dir, namaFile)
                FileOutputStream(file).use { pdf.writeTo(it) }
                HasilPdf(file.absolutePath, null, diDownloads = false)
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun drawLine(canvas: Canvas, y: Float, color: Int) {
        val p = Paint().apply { this.color = color; strokeWidth = 1f }
        canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, p)
    }

    private fun paint(
        color: Int,
        size: Float,
        bold: Boolean = false,
        alignRight: Boolean = false,
        center: Boolean = false
    ) = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = color
        textSize = size
        typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        textAlign = when {
            alignRight -> Paint.Align.RIGHT
            center -> Paint.Align.CENTER
            else -> Paint.Align.LEFT
        }
    }
}