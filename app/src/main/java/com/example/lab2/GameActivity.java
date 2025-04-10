package com.example.lab2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

public class GameActivity extends AppCompatActivity {

    // Bases de datos de oraciones por tema
    private final String[] oracionesOpticas = {
            "La fibra óptica transmite datos mediante pulsos de luz",
            "Los amplificadores EDFA mejoran señales en fibras ópticas"
    };

    private final String[] oracionesCiber = {
            "Un firewall protege redes contra accesos no autorizados",
            "El phishing busca robar credenciales mediante engaños"
    };

    private final String[] oracionesSoftware = {
            "Android Studio es el IDE oficial para desarrollo Android",
            "Kotlin es el lenguaje preferido para Android desarrollo"
    };

    // Variables de juego
    private String[] palabrasCorrectas;
    private final ArrayList<String> palabrasSeleccionadas = new ArrayList<>();
    private int intentoActual = 0;
    private long tiempoInicio;
    private boolean cronometroIniciado = false;
    private Handler cronometroHandler = new Handler();
    private GridLayout gridLayout;
    private TextView txtTema, txtIntentos, txtResultado;
    private static final int INTENTOS_MAX = 3;

    // Configuración visual
    private final int COLOR_OCULTO = Color.parseColor("#FF6200EE");
    private final int COLOR_VISIBLE = Color.BLACK;
    private final long TIEMPO_VISUALIZACION = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Inicializar vistas
        gridLayout = findViewById(R.id.gridWords);
        txtTema = findViewById(R.id.txtTema);
        txtIntentos = findViewById(R.id.txtIntentos);
        txtResultado = findViewById(R.id.txtResultado);
        Button btnNuevoJuego = findViewById(R.id.btnNuevoJuego);
        btnNuevoJuego.setVisibility(View.GONE);

        // Configurar botones de acción
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnReset).setOnClickListener(v -> reiniciarJuego());

        // Iniciar juego con el tema seleccionado
        String tema = getIntent().getStringExtra("tema");
        if (tema == null) {
            mostrarError("Tema no especificado");
            return;
        }

        iniciarJuego(tema);
    }

    private String seleccionarOracion(String tema) {
        String[] oraciones;
        switch (tema) {
            case "Ópticas":
                oraciones = oracionesOpticas;
                break;
            case "Ciberseguridad":
                oraciones = oracionesCiber;
                break;
            case "Software":
                oraciones = oracionesSoftware;
                break;
            default:
                oraciones = oracionesSoftware; // Por defecto usa Software
        }
        return oraciones[(int) (Math.random() * oraciones.length)].trim();
    }

    private void iniciarJuego(String tema) {
        txtResultado.setVisibility(View.GONE);
        txtTema.setText("Tema: " + tema);
        String oracion = seleccionarOracion(tema);

        // Validar y preparar palabras
        palabrasCorrectas = oracion.split("\\s+");
        if (palabrasCorrectas.length < 2) {
            mostrarError("Error: Oración muy corta");
            return;
        }

        tiempoInicio = 0;
        cronometroIniciado = false;
        intentoActual = 0;
        palabrasSeleccionadas.clear();
        txtIntentos.setText("Intentos: " + intentoActual + "/" + INTENTOS_MAX);

        generarBotonesDesordenados();
    }

    private void generarBotonesDesordenados() {
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(3);

        ArrayList<String> palabrasDesordenadas = new ArrayList<>();
        Collections.addAll(palabrasDesordenadas, palabrasCorrectas);
        Collections.shuffle(palabrasDesordenadas);

        for (String palabra : palabrasDesordenadas) {
            Button btn = new Button(this);
            btn.setText(palabra);
            btn.setTextColor(COLOR_OCULTO);
            btn.setBackgroundColor(COLOR_OCULTO);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.setMargins(8, 8, 8, 8);
            btn.setLayoutParams(params);

            btn.setOnClickListener(v -> {
                if (!cronometroIniciado) {
                    tiempoInicio = SystemClock.elapsedRealtime();
                    cronometroIniciado = true;
                }
                revelarPalabra((Button) v);
            });

            gridLayout.addView(btn);
        }
    }

    private void revelarPalabra(Button btn) {
        if (btn.getCurrentTextColor() == COLOR_OCULTO) {
            btn.setTextColor(COLOR_VISIBLE);
            btn.setBackgroundColor(Color.WHITE);
            verificarPalabra(btn);
        }
    }

    private void verificarPalabra(Button btn) {
        String palabra = btn.getText().toString();
        int indexEsperado = palabrasSeleccionadas.size();

        if (palabra.equals(palabrasCorrectas[indexEsperado])) {
            btn.setEnabled(false);
            palabrasSeleccionadas.add(palabra);

            if (palabrasSeleccionadas.size() == palabrasCorrectas.length) {
                long tiempoTranscurrido = (SystemClock.elapsedRealtime() - tiempoInicio) / 1000;
                mostrarResultado(true, tiempoTranscurrido);
            }
        } else {
            new Handler().postDelayed(() -> {
                if (btn.isEnabled()) {
                    btn.setTextColor(COLOR_OCULTO);
                    btn.setBackgroundColor(COLOR_OCULTO);
                }
            }, TIEMPO_VISUALIZACION);

            manejarError();
        }
    }

    private void manejarError() {
        intentoActual++;
        txtIntentos.setText("Intentos: " + intentoActual + "/" + INTENTOS_MAX);

        if (intentoActual >= INTENTOS_MAX) {
            long tiempoTranscurrido = (SystemClock.elapsedRealtime() - tiempoInicio) / 1000;
            bloquearBotones();
            mostrarResultado(false, tiempoTranscurrido); // Pasa el tiempo calculado
        } else {
            Toast.makeText(this,
                    "Incorrecto! Intentos restantes: " + (INTENTOS_MAX - intentoActual),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void bloquearBotones() {
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            Button btn = (Button) gridLayout.getChildAt(i);
            btn.setEnabled(false);
        }
    }

    private void mostrarResultado(boolean gano, long tiempoSegundos) {
        cronometroHandler.removeCallbacksAndMessages(null);

        String mensaje;
        if (gano) {
            mensaje = "¡Ganaste!\nTiempo: " + tiempoSegundos + "s\nIntentos: " + intentoActual;
            txtResultado.setTextColor(Color.GREEN);
        } else {
            mensaje = "¡Perdiste!\nTiempo: " + tiempoSegundos + "s\nRespuesta: " +
                    String.join(" ", palabrasCorrectas);
            txtResultado.setTextColor(Color.RED);
        }

        txtResultado.setText(mensaje);
        txtResultado.setVisibility(View.VISIBLE);

        Button btnNuevoJuego = findViewById(R.id.btnNuevoJuego);
        btnNuevoJuego.setVisibility(View.VISIBLE);
        btnNuevoJuego.setOnClickListener(v -> {
            Intent intent = new Intent(GameActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void mostrarError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void reiniciarJuego() {
        cronometroHandler.removeCallbacksAndMessages(null);
        String tema = getIntent().getStringExtra("tema");
        iniciarJuego(tema);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cronometroHandler.removeCallbacksAndMessages(null);
    }
}