package com.example.lab2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

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

        // Botones
        Button btnSoftware = findViewById(R.id.btnSoftware);
        Button btnCiberseguridad = findViewById(R.id.btnCiberseguridad);
        Button btnOpticas = findViewById(R.id.btnOpticas);

        // Listeners
        btnSoftware.setOnClickListener(view -> abrirJuego("Software"));
        btnCiberseguridad.setOnClickListener(view -> abrirJuego("Ciberseguridad"));
        btnOpticas.setOnClickListener(view -> abrirJuego("Ópticas"));
    }

    private void abrirJuego(String tema) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("tema", tema);
        startActivity(intent);
    }
}