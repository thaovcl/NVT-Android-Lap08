package com.example.lab08;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    String dbName = "contactDB.db";
    String dbPath = "/databases/";
    SQLiteDatabase db = null;
    ArrayAdapter<Contact> adapter;
    ListView lvContact;

    Button btnThem;
    Contact ct;
    int posUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        xulyCopy();
        addView();
        hienthiSP();
        addEvent();
    }

    private void hienthiSP() {
        db = openOrCreateDatabase(dbName, MODE_PRIVATE, null);
        Cursor cursor = db.rawQuery("SELECT * FROM contact", null);
        while (cursor.moveToNext()) {
            int ma = cursor.getInt(0);
            String ten = cursor.getString(1);
            String dt = cursor.getString(2);
            adapter.add(new Contact(ma, ten, dt));
        }
        cursor.close();
    }

    private void addView() {
        lvContact = findViewById(R.id.lvContact);
        adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1);
        lvContact.setAdapter(adapter);
        registerForContextMenu(lvContact);
    }

    private void addEvent() {
        lvContact.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                ct = adapter.getItem(i);
                posUpdate = i;
                return false;
            }
        });
        btnThem = findViewById(R.id.btnThem);
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ThemSuaActivity.class);
                intent.putExtra("TRANGTHAI", "THEM");
                startActivityForResult(intent, 113);
            }
        });
    }

    private void xulyCopy() {
        try {
            File dbFile = new File(getDatabasePath(dbName).getPath());
            if (!dbFile.exists()) {
                copyDataFromAsset();
                Toast.makeText(MainActivity.this, "Copy thành công", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "File tồn tại rồi", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("Lỗi", e.toString());
        }
    }

    private void copyDataFromAsset() {
        try {
            InputStream myInput = getAssets().open(dbName);
            String outFileName = getDatabasePath(dbName).getPath();
            File outFile = new File(outFileName);

            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
            }

            OutputStream myOutput = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
            Log.d("DB", "Database copied successfully");
        } catch (Exception e) {
            Log.e("CopyError", "Error copying database", e);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.lvContact) {
            getMenuInflater().inflate(R.menu.context, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mnuSua) {
            Toast.makeText(MainActivity.this, ct.getMa() + "", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, ThemSuaActivity.class);
            intent.putExtra("TRANGTHAI", "SUA");
            intent.putExtra("CONTACT", ct); // Đảm bảo chuyển đối tượng Contact
            startActivityForResult(intent, 113);
        } else if (item.getItemId() == R.id.mnuXoa) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Xác nhận xóa");
            builder.setMessage("Bạn thật sự muốn xóa?");
            builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    db.delete("Contact", "Ma=?", new String[]{String.valueOf(ct.getMa())});
                    adapter.remove(ct);
                    adapter.notifyDataSetChanged();
                }
            });
            builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 113 && data != null) {
            Contact ctNew = (Contact) data.getSerializableExtra("CONTACT");
            if (resultCode == 114) { // thêm mới
                adapter.add(ctNew);
                try {
                    ContentValues values = new ContentValues();
                    values.put("Ma", ctNew.getMa());
                    values.put("Ten", ctNew.getTen());
                    values.put("Dienthoai", ctNew.getDt());
                    if (db.insert("Contact", null, values) > 0) {
                        Toast.makeText(MainActivity.this, "Thêm mới thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Thêm mới thất bại", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("Lỗi:", e.toString());
                }
            } else if (resultCode == 115) { // cập nhật
                try {
                    ContentValues values = new ContentValues();
                    values.put("Ten", ctNew.getTen());
                    values.put("Dienthoai", ctNew.getDt());
                    db.update("Contact", values, "Ma=?", new String[]{ctNew.getMa() + ""});
                    adapter.getItem(posUpdate).setTen(ctNew.getTen());
                    adapter.getItem(posUpdate).setDt(ctNew.getDt());
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.e("Lỗi:", e.toString());
                }
            }
        }
    }
}
