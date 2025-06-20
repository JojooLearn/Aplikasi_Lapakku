package com.example.pemrogramanfrontend.sales;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.pemrogramanfrontend.R;
import com.example.pemrogramanfrontend.adapters.SaleItemAdapterForDialog;
import com.example.pemrogramanfrontend.api.db_connect;
import com.example.pemrogramanfrontend.models.SaleItem;
import com.example.pemrogramanfrontend.utils.SessionManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaleDetailDialogFragment extends DialogFragment {

    private static final String ARG_SALE_ID = "sale_id";
    private int saleId;

    public static SaleDetailDialogFragment newInstance(int saleId) {
        SaleDetailDialogFragment frag = new SaleDetailDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SALE_ID, saleId);
        frag.setArguments(args);
        return frag;
    }

    private TextView tvInvoice, tvCustomer, tvDate, tvTotal;
    private RecyclerView recyclerView;
    private SaleItemAdapterForDialog adapter;
    private List<SaleItem> itemList;
    private SessionManager session;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_sale_detail, null);

        saleId = getArguments().getInt(ARG_SALE_ID, -1);
        session = new SessionManager(requireContext());

        tvInvoice = view.findViewById(R.id.tvInvoice);
        tvCustomer = view.findViewById(R.id.tvCustomer);
        tvDate = view.findViewById(R.id.tvDate);
        tvTotal = view.findViewById(R.id.tvTotal);
        Button btnCetak = view.findViewById(R.id.btnCetak);
        recyclerView = view.findViewById(R.id.recyclerViewDetailItems);

        itemList = new ArrayList<>();
        adapter = new SaleItemAdapterForDialog(itemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        loadDetail();

        btnCetak.setOnClickListener(v -> {
            try {
                generatePdfInvoice();
            } catch (Exception e) {
                Toast.makeText(getContext(), "Gagal cetak: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        return new AlertDialog.Builder(requireActivity())
                .setView(view)
                .setTitle("Detail Transaksi")
                .setNegativeButton("Tutup", null)
                .create();
    }

    private void generatePdfInvoice() {
        PdfDocument pdfDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint titlePaint = new Paint();
        Paint footerPaint = new Paint();
        Paint boldPaint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(300, 700, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        int x = 10;
        int pageWidth = pageInfo.getPageWidth();
        int y = 30;

        // === Font Setup ===
        paint.setTextSize(10f);
        paint.setTypeface(Typeface.MONOSPACE);

        boldPaint.setTextSize(10f);
        boldPaint.setFakeBoldText(true);
        boldPaint.setTypeface(Typeface.MONOSPACE);

        titlePaint.setTextSize(16f);
        titlePaint.setFakeBoldText(true);

        footerPaint.setTextSize(9f);
        footerPaint.setTypeface(Typeface.MONOSPACE);

        // === Logo (centered) ===
        Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.ic_store_logo);
        Bitmap scaledLogo = Bitmap.createScaledBitmap(logo, 60, 60, false);
        canvas.drawBitmap(scaledLogo, (pageWidth - scaledLogo.getWidth()) / 2f, y, paint);
        y += 70;

        // === Nama Toko (centered) ===
        String storeName = "LAPAK JOHAN";
        float storeNameWidth = titlePaint.measureText(storeName);
        canvas.drawText(storeName, (pageWidth - storeNameWidth) / 2, y, titlePaint);
        y += 20;

//      === Alamat dan Kontak (wrapped + centered) ===
        String address1 = "Jl. Glagahsari No.63, Warungboto, Kec. Umbulharjo, Kota Yogyakarta, Daerah Istimewa Yogyakarta 55164";
        String contact1 = "Telp: 08981234XXXX | IG: @lapaku.id";

//      Margin horizontal dan max width teks
        int margin = 16;
        int maxTextWidth = pageWidth - (2 * margin);

//      Gambar alamat dan kontak dalam center & wrap
        y = drawWrappedCenteredText(canvas, address1, paint, y, maxTextWidth, pageWidth, 12);
        y = drawWrappedCenteredText(canvas, contact1, paint, y, maxTextWidth, pageWidth, 15);
        y += 10;


        // === Garis Pembatas ===
        canvas.drawText("----------------------------------------", x, y, paint); y += 15;

        // === Info Transaksi ===
        canvas.drawText(tvInvoice.getText().toString(), x, y, boldPaint); y += 15;
        canvas.drawText(tvCustomer.getText().toString(), x, y, paint); y += 15;
        canvas.drawText(tvDate.getText().toString(), x, y, paint); y += 15;

        canvas.drawText("----------------------------------------", x, y, paint); y += 15;

        int colProductX = x;              // Produk mulai dari kiri
        int colQtyX = x + 110;            // Kolom Qty
        int colPriceX = x + 140;          // Harga
        int colSubtotalX = x + 190;       // Subtotal

//      === Judul Kolom ===
        canvas.drawText("Produk", colProductX, y, boldPaint);
        canvas.drawText("Qty", colQtyX, y, boldPaint);
        canvas.drawText("Harga", colPriceX, y, boldPaint);
        canvas.drawText("Subtotal", colSubtotalX, y, boldPaint);
        y += 15;

//      === Daftar Produk ===
        for (SaleItem item : itemList) {
            String name = item.getProductName();
            if (name.length() > 15) name = name.substring(0, 15); // Potong agar tidak kepanjangan

            String qty = String.valueOf(item.getQuantity());
            String price = String.format("Rp%d", item.getPrice());
            String subtotal = String.format("Rp%d", item.getSubtotal());

            canvas.drawText(name, colProductX, y, paint);
            canvas.drawText(qty, colQtyX, y, paint);
            canvas.drawText(price, colPriceX, y, paint);
            canvas.drawText(subtotal, colSubtotalX, y, paint);
            y += 15;
        }


        y += 10;
        canvas.drawText("----------------------------------------", x, y, paint); y += 15;

        // === Total Harga ===
        canvas.drawText(tvTotal.getText().toString(), x, y, boldPaint); y += 30;

        // === Footer Ucapan Terima Kasih (centered) ===
        String thanks = "Terima kasih telah berbelanja di LAPAK JOHAN!";
        String contactFooter = "Kritik & Saran : 08981234XXXX";
        String ending = "Semoga puas dengan pelayanan kami";

        float thanksWidth = footerPaint.measureText(thanks);
        float contactWidth2 = footerPaint.measureText(contactFooter);
        float endingWidth = footerPaint.measureText(ending);

        canvas.drawText(thanks, (pageWidth - thanksWidth) / 2, y, footerPaint); y += 15;
        canvas.drawText(contactFooter, (pageWidth - contactWidth2) / 2, y, footerPaint); y += 15;
        canvas.drawText(ending, (pageWidth - endingWidth) / 2, y, footerPaint);

        pdfDocument.finishPage(page);

        // === Simpan ke Downloads ===
        String fileName = "Invoice_" + System.currentTimeMillis() + ".pdf";
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
        contentValues.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/LapakKu");

        ContentResolver resolver = requireContext().getContentResolver();
        Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);

        if (uri != null) {
            try (OutputStream os = resolver.openOutputStream(uri)) {
                pdfDocument.writeTo(os);
                Toast.makeText(getContext(), "Invoice disimpan di folder Downloads/LapakKu", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(getContext(), "Gagal menyimpan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        pdfDocument.close();
    }

    private int drawWrappedCenteredText(Canvas canvas, String text, Paint paint, int y, int maxWidth, int pageWidth, int lineHeight) {
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            String testLine = line + word + " ";
            float testWidth = paint.measureText(testLine);
            if (testWidth > maxWidth) {
                float textWidth = paint.measureText(line.toString());
                float x = (pageWidth - textWidth) / 2f;
                canvas.drawText(line.toString(), x, y, paint);
                y += lineHeight;
                line = new StringBuilder(word).append(" ");
            } else {
                line.append(word).append(" ");
            }
        }

        // Sisa teks terakhir
        if (!line.toString().isEmpty()) {
            float textWidth = paint.measureText(line.toString());
            float x = (pageWidth - textWidth) / 2f;
            canvas.drawText(line.toString(), x, y, paint);
            y += lineHeight;
        }

        return y;
    }



    private void loadDetail() {
        String url = db_connect.apiSalesDetail + "?id=" + saleId;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getBoolean("status")) {
                            JSONObject data = response.getJSONObject("data");
                            JSONObject header = data.getJSONObject("header");

                            tvInvoice.setText("Invoice: " + header.getString("invoice_number"));
                            tvCustomer.setText("Customer: " + header.getString("customer_name"));
                            tvDate.setText("Tanggal: " + header.getString("date"));
                            tvTotal.setText("Total: Rp" + header.getInt("total_price"));

                            JSONArray items = data.getJSONArray("items");
                            itemList.clear();
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject obj = items.getJSONObject(i);
                                SaleItem item = new SaleItem();
                                item.setProductName(obj.getString("product_name"));
                                item.setQuantity(obj.getInt("quantity"));
                                item.setPrice(obj.getInt("price"));
                                item.setSubtotal(obj.getInt("subtotal"));
                                itemList.add(item);
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Gagal parsing data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Log.e("SALE_DETAIL_ERROR", error.toString());
                    Toast.makeText(getContext(), "Gagal koneksi ke server", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> h = new HashMap<>();
                h.put("Authorization", "Bearer " + session.getToken());
                return h;
            }
        };

        Volley.newRequestQueue(getContext()).add(request);
    }
}
