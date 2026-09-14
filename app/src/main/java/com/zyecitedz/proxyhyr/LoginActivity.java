package com.zyecitedz.proxyhyr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    // ==== KONFIGURASI LOGIN ====
    private static final String VALID_KEY = "KEY-ZYE-INJECT-HYR";
    private static final String PREFS     = "PROXY_HYR_PREFS";
    private static final String KEY_AUTH  = "auth_ok";

    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Auto-login jika sudah pernah sukses
        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        if (sp.getBoolean(KEY_AUTH, false)) {
            gotoMain();
            return;
        }

        setContentView(R.layout.activity_login);

        etPassword = findViewById(R.id.et_password);
        Button btnLogin = findViewById(R.id.btn_login);
        TextView tvDev = findViewById(R.id.tv_dev);
        TextView tvChannel = findViewById(R.id.tv_channel);

        tvDev.setText("© ZyeCitedz");
        tvChannel.setText("PROXY UPDATE");

        btnLogin.setOnClickListener(v -> {
            String input = etPassword.getText().toString().trim();

            if (TextUtils.isEmpty(input)) {
                Toast.makeText(this, "Masukkan key akses", Toast.LENGTH_SHORT).show();
                return;
            }

            if (input.equals(VALID_KEY)) {
                sp.edit().putBoolean(KEY_AUTH, true).apply();
                Toast.makeText(this, "Akses diterima", Toast.LENGTH_SHORT).show();
                gotoMain();
            } else {
                etPassword.startAnimation(
                    AnimationUtils.loadAnimation(this, R.anim.shake));
                etPassword.setText("");
                Toast.makeText(this, "Key salah — akses ditolak", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void gotoMain() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
