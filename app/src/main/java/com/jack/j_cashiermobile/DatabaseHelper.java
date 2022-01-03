package com.jack.j_cashiermobile;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "JCashierMobile.db";

    //Tabel Produk
    private static final String TABLE_P = "Produk";
    private static final String P_ID = "id_produk";
    private static final String P_NAME = "nama_produk";
    private static final String P_PRICE = "harga_produk";

    private static final String CREATE_P_TABLE = "CREATE TABLE Produk (id_produk INTEGER "
            + "PRIMARY KEY, nama_produk TEXT, harga_produk INTEGER)";

    //Tabel Transaksi
    private static final String TABLE_T = "Transaksi";
    private static final String T_ID = "id_transaksi";
    private static final String T_STATUS = "status";
    private static final String T_PRICE = "total_harga";
    private static final String T_PAY = "total_bayar";
    private static final String T_CHANGE = "uang_kembalian";
    private static final String T_DATETIME = "tanggal_waktu";

    private static final String CREATE_T_TABLE = "CREATE TABLE Transaksi (id_transaksi "
            + "INTEGER PRIMARY KEY AUTOINCREMENT, tanggal_waktu DATETIME DEFAULT (datetime('now','localtime')), "
            + "total_harga INTEGER, "
            + "total_bayar INTEGER, uang_kembalian INTEGER, status TEXT)";

    //Tabel Keranjang_Belanja
    private static final String TABLE_C = "Keranjang_Belanja";
    private static final String C_VALUE = "jumlah";
    private static final String C_TOT = "sub_total";

    private static final String CREATE_C_TABLE = "CREATE TABLE Keranjang_Belanja (id_transaksi "
            + "INTEGER, id_produk INTEGER, jumlah INTEGER, sub_total INTEGER, "
            + "FOREIGN KEY(id_transaksi) REFERENCES Transaksi(id_transaksi), FOREIGN KEY"
            + "(id_produk) REFERENCES Produk(id_produk))";

    //Tabel Detail_Transaksi
    private static final String TABLE_D = "Detail_Transaksi";
    private static final String D_VALUE = "jumlah";
    private static final String D_TOT = "sub_total";
    private static final String CREATE_D_TABLE = "CREATE TABLE Detail_Transaksi (id_transaksi "
            + "INTEGER, id_produk INTEGER, jumlah INTEGER, sub_total INTEGER, "
            + "FOREIGN KEY(id_transaksi) REFERENCES Transaksi(id_transaksi), FOREIGN KEY"
            + "(id_produk) REFERENCES Detail_Produk(id_produk))";


    int transactionId;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_P_TABLE);
        sqLiteDatabase.execSQL(CREATE_T_TABLE);
        sqLiteDatabase.execSQL(CREATE_C_TABLE);
        sqLiteDatabase.execSQL(CREATE_D_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int j) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DB_NAME);

        onCreate(sqLiteDatabase);
    }

    //Function for Product Table

    public Cursor viewProduct() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_P;
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    public boolean deleteAllProduct() {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_P, null, null);
        return result != -1;
    }

    public boolean deleteItemProduct(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_P, P_ID + "=" + id, null);
        return result != -1;
    }

    public Cursor addProduct(int id, String pName, int pPrice) {
        int getNewId = 0;
        Cursor cursor = viewProduct();
        if(cursor.getCount() == 0) {
            insertIntoProduct(id, pName, pPrice);
        } else {
            boolean i = false;
            while(cursor.moveToNext()) {
                if(cursor.getString(1).equals(pName)) {
                    i = true;
                    getNewId = cursor.getInt(0);
                }
            }
            if(i == true) {
                updateIntoProduct(getNewId, pName, pPrice);
            }
            else {
                insertIntoProduct(id, pName, pPrice);
            }
        }
        return cursor;
    }

    public boolean insertIntoProduct(int id, String name, int price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(P_ID, id);
        contentValues.put(P_NAME, name);
        contentValues.put(P_PRICE, price);

        long result = db.insert(TABLE_P, null, contentValues);
        return result != -1;
    }

    private boolean updateIntoProduct(int id, String name, int price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(P_ID, id);
        contentValues.put(P_NAME, name);
        contentValues.put(P_PRICE, price);
        String[] whereArgs = new String[] {String.valueOf(id)};
        long result = db.update(TABLE_P, contentValues, "id_produk=?", whereArgs);
        return result != -1;
    }

    //Function for Cart Table

    public Cursor viewCart() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT Keranjang_Belanja.id_produk, Produk.nama_produk, "
                + "Keranjang_Belanja.sub_total, "
                + "Keranjang_Belanja.jumlah FROM "
                + "Keranjang_Belanja INNER JOIN Produk ON "
                + "Keranjang_Belanja.id_produk = Produk.id_produk";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    public Cursor viewAllCart() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Keranjang_Belanja";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    public boolean deleteAllCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_C, null, null);
        return result != -1;
    }

    public Cursor addCart(int product, int price, int value) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + P_ID + " FROM " + TABLE_C;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() == 0) {
            insertIntoCart(product, price, value);
        } else {
            boolean i = false;
            while(cursor.moveToNext()) {
                if(cursor.getInt(0) == product) {
                    i = true;
                }
            }
            if(i == true) {
                updateIntoCart(product, price, value);
            }
            else {
                insertIntoCart(product, price, value);
            }
        }
        return cursor;
    }

    private boolean updateIntoCart(int product, int price, int value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(T_ID, transactionId);
        contentValues.put(P_ID, product);
        contentValues.put(C_VALUE, value);
        contentValues.put(C_TOT, price);
        String[] whereArgs = new String[] {String.valueOf(product)};
        long result = db.update(TABLE_C, contentValues, "id_produk=?", whereArgs);
        return result != -1;
    }

    private boolean insertIntoCart(int product, int price, int value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(T_ID, transactionId);
        contentValues.put(P_ID, product);
        contentValues.put(C_VALUE, value);
        contentValues.put(C_TOT, price);
        long result = db.insert(TABLE_C, null, contentValues);
        return result != -1;
    }

    public boolean deleteItemCart(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_C, P_ID + "=" + id, null);
        return result != -1;
    }

    //Function for Detail Table

    public boolean deleteAllDetail() {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_D, null, null);
        return result != -1;
    }

    public Cursor viewDetail() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + P_ID + " FROM " + TABLE_D;
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public Cursor addDetail(int product, int price, int value) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + P_ID + " FROM " + TABLE_C;
        Cursor cursor = db.rawQuery(query, null);
        insertIntoDetail(product, price, value);
        return cursor;
    }

    private boolean insertIntoDetail(int product, int price, int value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(T_ID, transactionId);
        contentValues.put(P_ID, product);
        contentValues.put(D_VALUE, value);
        contentValues.put(D_TOT, price);
        long result = db.insert(TABLE_D, null, contentValues);
        return result != -1;
    }

    //Function for Transaction Table

    public boolean addTransaction(int total_harga, int total_bayar, int uang_kembalian) {
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        int id = transactionId;
        ContentValues contentValues = new ContentValues();
        contentValues.put(T_ID,id);
        contentValues.put(T_STATUS, "transaksi dibuat");
        contentValues.put(T_PRICE, total_harga);
        contentValues.put(T_DATETIME, dateFormat.format(date));
        contentValues.put(T_PAY, total_bayar);
        contentValues.put(T_CHANGE, uang_kembalian);

        long result = db.insert(TABLE_T, null, contentValues);
        return result != -1;
    }

    public Cursor viewTransaction() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT sub_total FROM Keranjang_Belanja";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    public Cursor viewAllTransaction() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Transaksi";
        Cursor cursor = db.rawQuery(query, null);

        return cursor;
    }

    public boolean updateTransaction(int price, int pay, int change) {
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        int id = transactionId;
        ContentValues contentValues = new ContentValues();
        contentValues.put(T_ID,id);
        contentValues.put(T_PRICE, price);
        contentValues.put(T_PAY, pay);
        contentValues.put(T_DATETIME, dateFormat.format(date));
        contentValues.put(T_STATUS, "lunas");
        contentValues.put(T_CHANGE, change);
        String[] whereArgs = new String[] {String.valueOf(id)};

        long result = db.update(TABLE_T, contentValues, "id_transaksi=?", whereArgs);
        return result != -1;
    }

    public boolean deleteAllTransaction() {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(TABLE_T, null, null);
        return result != -1;
    }

    public boolean TransactionExit() {
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        ContentValues contentValues = new ContentValues();
        contentValues.put(T_DATETIME, dateFormat.format(date));
        String[] whereArgs = new String[] {"transaksi dibuat"};
        long result = db.update(TABLE_T, contentValues, "status=?", whereArgs);
        return result != -1;
    }

    //Getting string function for report feature

    String TAG = "DbHelper";

    public String getTableAsString(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "getTableAsString called");
        String tableString = String.format("Laporan %s:\n", "Transaksi");
        tableString += "---------------------------\n";
        Cursor allRows  = sqLiteDatabase.rawQuery("SELECT id_transaksi, tanggal_waktu," +
                " total_harga, total_bayar, uang_kembalian, status FROM Transaksi WHERE status = 'lunas';", null);

        if (allRows.moveToFirst() ){
            do {
                tableString += String.format("%s: %s\n", "ID Transaksi",
                        allRows.getString(allRows.getColumnIndex("id_transaksi")));
                tableString += String.format("%s: %s\n", "Tanggal & Waktu",
                        allRows.getString(allRows.getColumnIndex("tanggal_waktu")));
                tableString += String.format("%s: %s\n", "Total Harga Barang (Rp)",
                        allRows.getString(allRows.getColumnIndex("total_harga")));
                tableString += String.format("%s: %s\n", "Total Bayar (Rp)",
                        allRows.getString(allRows.getColumnIndex("total_bayar")));
                tableString += String.format("%s: %s\n", "Uang Kembalian (Rp)",
                        allRows.getString(allRows.getColumnIndex("uang_kembalian")));
                tableString += String.format("%s: %s\n", "Status Transaksi",
                        allRows.getString(allRows.getColumnIndex("status")));
                tableString += "\n";
                tableString += "---------------------------\n";
            } while (allRows.moveToNext());
        }

        Cursor total = sqLiteDatabase.rawQuery("SELECT COUNT(id_transaksi) AS" +
                " TOTAL_TRANSAKSI, SUM(total_harga) AS TOTAL_PENDAPATAN FROM Transaksi" +
                " WHERE status = 'lunas';", null);
        total.moveToFirst();

        tableString += "---------------------------\n";
        tableString += String.format("%s: %s\n", "TOTAL Seluruh Transaksi",
                total.getString(total.getColumnIndex("TOTAL_TRANSAKSI")));
        tableString += String.format("%s: %s\n", "TOTAL Seluruh Pendapatan (Rp)",
                total.getString(total.getColumnIndex("TOTAL_PENDAPATAN")));

        return tableString;
    }
}