package com.example.lab08;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ThemSuaActivity extends AppCompatActivity {
    Intent intent;
    EditText edtMa, edtTen, edtDienThoai;
    Button btnThemSua, btnThoat;
    String trangthai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_them_sua);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        addView();
        addEvent();
    }

    private void addView() {
        intent = getIntent();
        trangthai = intent.getStringExtra("TRANGTHAI");
        edtMa = findViewById(R.id.edtMa);
        edtTen = findViewById(R.id.edtTen);
        edtDienThoai = findViewById(R.id.edtDT);
        btnThemSua = findViewById(R.id.btnThemSua);
        btnThoat = findViewById(R.id.btnThoat);
        if (trangthai.equals("THEM")) {
            btnThemSua.setText("Thêm");
        } else {
            btnThemSua.setText("Sửa");
            Contact ct = (Contact) intent.getSerializableExtra("CONTACT");
            edtMa.setText(ct.getMa() + "");
            edtMa.setEnabled(false);
            edtTen.setText(ct.getTen());
            edtDienThoai.setText(ct.getDt());
        }
    }

    private void addEvent() {
        btnThemSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Contact ct = new Contact();
                ct.setMa(Integer.parseInt(edtMa.getText().toString()));
                ct.setTen(edtTen.getText().toString());
                ct.setDt(edtDienThoai.getText().toString());
                intent.putExtra("CONTACT", ct);
                if (trangthai.equals("THEM")) {
                    setResult(114, intent);
                } else {
                    setResult(115, intent);
                }
                finish();
            }
        });
        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                 finish(); }
            });
    }
}
