package com.example.pemrogramanfrontend.Utama;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.pemrogramanfrontend.R;
import com.example.pemrogramanfrontend.api.db_connect;
import com.example.pemrogramanfrontend.auth.login;
import com.example.pemrogramanfrontend.customers.CustomerListActivity;
import com.example.pemrogramanfrontend.expenses.ExpenseListActivity;
import com.example.pemrogramanfrontend.products.ProductListActivity;
import com.example.pemrogramanfrontend.sales.SaleListActivity;
import com.example.pemrogramanfrontend.stockin.StockInListActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Dashboard extends AppCompatActivity {
    private static final String TAG = "DashboardActivity";
    private TextView tvPenjualan, tvPengeluaran, tvProfitValue, tvStok;
    private MaterialButton btnCetakLaporan;
    private RecyclerView rvMenu;
    private DashboardMenuAdapter menuAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        // Check session
        SharedPreferences sharedPreferences = getSharedPreferences("user_session", MODE_PRIVATE);
        if (!sharedPreferences.contains("token")) {
            startActivity(new Intent(this, login.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main); // Pastikan layout yang benar

        // Initialize Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Dashboard");
        }

        // Initialize Views
        initViews();

        // Load data
        loadSummary();
        setupMenu();
        setupBottomNavigation();
    }

    private void initViews() {
        tvPenjualan = findViewById(R.id.tvPenjualan);
        tvPengeluaran = findViewById(R.id.tvPengeluaran);
        tvProfitValue = findViewById(R.id.tvProfitValue);
        tvStok = findViewById(R.id.tvStok);
        btnCetakLaporan = findViewById(R.id.btnCetakLaporan);
        rvMenu = findViewById(R.id.rvMenu);

        btnCetakLaporan.setOnClickListener(v -> {
            generateReport();
        });
    }

    private void loadSummary() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                db_connect.apiSummary,
                null,
                this::handleSummaryResponse,
                error -> Toast.makeText(this, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
                String token = sp.getString("token", "");
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void handleSummaryResponse(JSONObject response) {
        try {
            if (response.getBoolean("status")) {
                JSONObject data = response.getJSONObject("data");

                NumberFormat format = NumberFormat.getInstance(Locale.getDefault());

                int penjualan = data.getInt("penjualan");
                int pengeluaran = data.getInt("pengeluaran");
                int profit = data.getInt("profit");
                int stok = data.getInt("stok");

                tvPenjualan.setText(getString(R.string.currency_format, format.format(penjualan)));
                tvPengeluaran.setText(getString(R.string.currency_format, format.format(pengeluaran)));
                tvProfitValue.setText(getString(R.string.currency_format, format.format(profit)));
                tvStok.setText(getString(R.string.stock_format, stok));
            } else {
                Toast.makeText(this, "Gagal mengambil data summary", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error parsing data", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void setupMenu() {
        List<DashboardMenuItem> menuItems = new ArrayList<>();
        menuItems.add(new DashboardMenuItem("Kasir", R.drawable.ic_kasir2, CustomerListActivity.class));
        menuItems.add(new DashboardMenuItem("Modal Awal", R.drawable.ic_modal1, ExpenseListActivity.class));
        menuItems.add(new DashboardMenuItem("Pelanggan", R.drawable.ic_pelanggan2, CustomerListActivity.class));
        menuItems.add(new DashboardMenuItem("Penjualan", R.drawable.ic_penjualan2, SaleListActivity.class));
        menuItems.add(new DashboardMenuItem("Stok", R.drawable.ic_stok, StockInListActivity.class));
        menuItems.add(new DashboardMenuItem("Laporan", R.drawable.ic_laporan, LaporanActivity.class));

        menuAdapter = new DashboardMenuAdapter(menuItems, item -> {
            startActivityWithAnimation(new Intent(this, item.getTargetActivity()));
        });
        rvMenu.setLayoutManager(new GridLayoutManager(this, 3));
        rvMenu.setAdapter(menuAdapter);

    }

    private void startActivityWithAnimation(Intent intent) {
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private void onMenuItemClicked(DashboardMenuItem item) {
        startActivity(new Intent(this, item.getTargetActivity()));
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.button_navigation_menu);
        bottomNav.setSelectedItemId(R.id.dashboard);
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Log.d(TAG, "BottomNavigation clicked: " + item.getItemId());

            if (item.getItemId() == R.id.dashboard) {
                Log.d(TAG, "Navigating to Dashboard");
                startActivityWithAnimation(new Intent(this, Dashboard.class));
                return true;
            } else if (item.getItemId() == R.id.Produk) {
                Log.d(TAG, "Navigating to ProductListActivity");
                startActivityWithAnimation(new Intent(this, ProductListActivity.class));
                return true;
            } else if (item.getItemId() == R.id.Riwayat) {
                Log.d(TAG, "Navigating to RiwayatActivity");
                startActivityWithAnimation(new Intent(this, RiwayatActivity.class));
                return true;
            } else if (item.getItemId() == R.id.profile) {
                Log.d(TAG, "Navigating to ProfileActivity");
                startActivityWithAnimation(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });

    }

    private void generateReport() {
        String penjualan = tvPenjualan.getText().toString().replaceAll("[^0-9]", "");
        String pengeluaran = tvPengeluaran.getText().toString().replaceAll("[^0-9]", "");
        String profit = tvProfitValue.getText().toString().replaceAll("[^0-9]", "");
        String stok = tvStok.getText().toString().replaceAll("[^0-9]", "");

        createPDFReport(penjualan, pengeluaran, profit, stok);
    }

    private void createPDFReport(String penjualan, String pengeluaran, String profit, String stok) {
        PdfDocument pdfDocument = new PdfDocument();

        // Setup paints
        Paint titlePaint = new Paint();
        titlePaint.setTextSize(22);
        titlePaint.setFakeBoldText(true);
        titlePaint.setColor(Color.BLACK);
        titlePaint.setTextAlign(Paint.Align.CENTER);

        Paint headerPaint = new Paint();
        headerPaint.setTextSize(12);
        headerPaint.setFakeBoldText(true);
        headerPaint.setColor(Color.WHITE);

        Paint textPaint = new Paint();
        textPaint.setTextSize(10);
        textPaint.setColor(Color.BLACK);

        Paint borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(0.8f);
        borderPaint.setColor(Color.DKGRAY);

        // Create A4 page (595 x 842 points)
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Margin and measurements
        int marginLeft = 40;
        int marginRight = 40;
        int tableWidth = 595 - marginLeft - marginRight;
        int y = 60; // Start position

        // 1. HEADER
        canvas.drawText("LAPORAN KEUANGAN", 595/2, y, titlePaint);
        y += 30;

        // Business info (aligned left and right)
        textPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Lapak Johan", marginLeft, y, textPaint);

        textPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Tanggal: " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()),
                595 - marginRight, y, textPaint);
        y += 15;

        textPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Jl. Merdeka No. 123, Yogyakarta", marginLeft, y, textPaint);

        textPaint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Periode: " + new SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(new Date()),
                595 - marginRight, y, textPaint);
        y += 30;

        // 2. TABLE HEADER
        float[] columnWidths = {180, 120, 120, 95}; // 4 columns
        float tableLeft = marginLeft;
        float tableRight = marginLeft + tableWidth;

        // Draw header background
        Paint headerBgPaint = new Paint();
        headerBgPaint.setColor(Color.parseColor("#3F51B5"));
        headerBgPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(tableLeft, y, tableRight, y + 25, headerBgPaint);

        // Draw header borders
        canvas.drawRect(tableLeft, y, tableRight, y + 25, borderPaint);

        // Draw vertical lines
        float currentX = tableLeft;
        for (int i = 0; i < columnWidths.length; i++) {
            currentX += columnWidths[i];
            canvas.drawLine(currentX, y, currentX, y + 25, borderPaint);
        }

        // Header text
        float textY = y + 16;
        float textX = tableLeft + 10;

        headerPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("KETERANGAN", textX, textY, headerPaint);
        textX += columnWidths[0];

        canvas.drawText("JUMLAH", textX + 10, textY, headerPaint);
        textX += columnWidths[1];

        canvas.drawText("KATEGORI", textX + 10, textY, headerPaint);
        textX += columnWidths[2];

        canvas.drawText("STATUS", textX + 10, textY, headerPaint);
        y += 25;

        // 3. TABLE ROWS
        String[][] tableData = {
                {"Total Penjualan", formatCurrency(penjualan), "Penjualan", "Bersih"},
                {"Total Pengeluaran", formatCurrency(pengeluaran), "Operasional", "Keluar"},
                {"Profit Bersih", formatCurrency(profit), "Laba", "Bersih"},
                {"Stok Barang", stok + " item", "Inventory", "Tersedia"}
        };

        for (String[] row : tableData) {
            // Draw row background (alternating colors)
            if (y % 50 == 0) {
                Paint rowBgPaint = new Paint();
                rowBgPaint.setColor(Color.parseColor("#F5F5F5"));
                rowBgPaint.setStyle(Paint.Style.FILL);
                canvas.drawRect(tableLeft, y, tableRight, y + 20, rowBgPaint);
            }

            // Draw cell borders
            canvas.drawRect(tableLeft, y, tableRight, y + 20, borderPaint);

            // Draw vertical lines
            currentX = tableLeft;
            for (int i = 0; i < columnWidths.length; i++) {
                currentX += columnWidths[i];
                canvas.drawLine(currentX, y, currentX, y + 20, borderPaint);
            }

            // Draw cell text
            textX = tableLeft + 10;
            textY = y + 14;

            for (int i = 0; i < row.length; i++) {
                textPaint.setTextAlign(i == 1 ? Paint.Align.RIGHT : Paint.Align.LEFT);
                canvas.drawText(row[i],
                        i == 1 ? textX + columnWidths[i] - 10 : textX,
                        textY,
                        textPaint);
                textX += columnWidths[i];
            }

            y += 20;
        }
        y += 30;

        // 4. FOOTER
        textPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("Catatan: Laporan ini dicetak otomatis oleh sistem", marginLeft, y, textPaint);
        y += 15;
        canvas.drawText("Tanggal Cetak: " + new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date()),
                marginLeft, y, textPaint);

        // Signature area
        float signatureX = tableRight - 150;
        y += 40;
        canvas.drawText("Mengetahui,", signatureX, y, textPaint);
        y += 30;
        canvas.drawLine(signatureX, y, signatureX + 100, y, borderPaint);
        y += 20;
        canvas.drawText("(__________________)", signatureX + 15, y, textPaint);

        pdfDocument.finishPage(page);
        savePdfDocument(pdfDocument);
    }

    private Paint getHeaderFillPaint() {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#3F51B5")); // Warna biru material
        return paint;
    }

    private void drawTableRow(Canvas canvas, int x, int y, int colWidth,
                              String col1, String col2, String col3,
                              Paint textPaint, Paint borderPaint) {
        // Draw cell borders
        canvas.drawRect(x, y, x + colWidth, y + 30, borderPaint);
        canvas.drawRect(x + colWidth, y, x + colWidth*2, y + 30, borderPaint);
        canvas.drawRect(x + colWidth*2, y, x + colWidth*3, y + 30, borderPaint);

        // Draw text
        canvas.drawText(col1, x + 10, y + 20, textPaint);
        canvas.drawText(col2, x + colWidth + 10, y + 20, textPaint);
        canvas.drawText(col3, x + colWidth*2 + 10, y + 20, textPaint);
    }

    private String formatCurrency(String value) {
        try {
            long amount = Long.parseLong(value);
            DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
            formatter.applyPattern("#,###");
            return "Rp" + formatter.format(amount);
        } catch (NumberFormatException e) {
            return "Rp0";
        }
    }

    private void drawTableRow(Canvas canvas, int x, int y, String label, String value, Paint paint) {
        canvas.drawText(label, x, y, paint);
        canvas.drawText(value, x + 200, y, paint);
    }

    private void savePdfDocument(PdfDocument pdfDocument) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, "laporan_" + System.currentTimeMillis() + ".pdf");
        values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
        values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

        try {
            Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                OutputStream out = getContentResolver().openOutputStream(uri);
                pdfDocument.writeTo(out);
                Toast.makeText(this, "Laporan disimpan di Download", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Toast.makeText(this, "Gagal menyimpan: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            pdfDocument.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString styledTitle = new SpannableString(item.getTitle());
            styledTitle.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.primary)), 0, styledTitle.length(), 0);
            item.setTitle(styledTitle);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                db_connect.apilogout,
                response -> {
                    clearSession();
                    navigateToLogin();
                },
                error -> Toast.makeText(this, "Logout gagal", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                SharedPreferences sp = getSharedPreferences("user_session", MODE_PRIVATE);
                Map<String, String> params = new HashMap<>();
                params.put("token", sp.getString("token", ""));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

    private void clearSession() {
        getSharedPreferences("user_session", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }

    private void navigateToLogin() {
        startActivity(new Intent(this, login.class));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    // Menu Item Model
    private static class DashboardMenuItem {
        private final String title;
        private final int iconRes;
        private final Class<?> targetActivity;

        public DashboardMenuItem(String title, int iconRes, Class<?> targetActivity) {
            this.title = title;
            this.iconRes = iconRes;
            this.targetActivity = targetActivity;
        }

        public String getTitle() { return title; }
        public int getIconRes() { return iconRes; }
        public Class<?> getTargetActivity() { return targetActivity; }
    }

    // Menu Adapter
    private static class DashboardMenuAdapter extends RecyclerView.Adapter<DashboardMenuAdapter.ViewHolder> {
        private final List<DashboardMenuItem> items;
        private final OnMenuItemClickListener listener;

        public DashboardMenuAdapter(List<DashboardMenuItem> items, OnMenuItemClickListener listener) {
            this.items = items;
            this.listener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            MaterialCardView view = (MaterialCardView) android.view.LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_menu, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            DashboardMenuItem item = items.get(position);
            holder.textView.setText(item.getTitle());
            holder.imageView.setImageResource(item.getIconRes());

            // Animasi untuk setiap item
            Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(),
                    android.R.anim.fade_in);
            animation.setDuration(300);
            animation.setStartOffset(position * 100); // Staggered animation
            holder.itemView.startAnimation(animation);

            holder.itemView.setOnClickListener(v -> {
                v.startAnimation(AnimationUtils.loadAnimation(v.getContext(), R.anim.fade_out));
                listener.onItemClick(item);
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textView;
            ImageView imageView;

            public ViewHolder(MaterialCardView itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.tvTitle);
                imageView = itemView.findViewById(R.id.ivIcon);
            }
        }

        interface OnMenuItemClickListener {
            void onItemClick(DashboardMenuItem item);
        }
    }
}