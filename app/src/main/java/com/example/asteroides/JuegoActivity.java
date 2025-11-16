package com.example.asteroides;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

public class JuegoActivity extends AppCompatActivity {

    private VistaJuego vistaJuego;
    private RelativeLayout keyboardControls;
    private int tipoControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // obtener referencias
        vistaJuego = findViewById(R.id.vistaJuego);
       // keyboardControls = findViewById(R.id.keyboardControls);

        // importante: para que VistaJuego use padre.finish() en salir()
        vistaJuego.setPadre(this);

        // leer preferencias (mismo pref que usas en VistaJuego)
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        tipoControl = Integer.parseInt(pref.getString("controles", "0"));


        if (tipoControl == 2) { // modo teclado
            keyboardControls.setVisibility(RelativeLayout.VISIBLE);
            setupKeyboardButtons();
        } else {
            keyboardControls.setVisibility(RelativeLayout.GONE);
        }
    }

    private void setupKeyboardButtons() {
        Button btnLeft = findViewById(R.id.btnLeft);
        Button btnRight = findViewById(R.id.btnRight);
        Button btnUp = findViewById(R.id.btnUp);
        Button btnShoot = findViewById(R.id.btnShoot);

        // Pulsación continua para izquierda
        btnLeft.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // giro hacia la izquierda (valor negativo)
                    vistaJuego.setGiroNave(-VistaJuego.PASO_GIRO_PARA_UI);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    vistaJuego.setGiroNave(0);
                    break;
            }
            return true;
        });

        // Pulsación continua para derecha
        btnRight.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    vistaJuego.setGiroNave(VistaJuego.PASO_GIRO_PARA_UI);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    vistaJuego.setGiroNave(0);
                    break;
            }
            return true;
        });

        // Pulsación continua para acelerar (arriba)
        btnUp.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    vistaJuego.setAceleracionNave(VistaJuego.PASO_ACELERACION_PARA_UI);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    vistaJuego.setAceleracionNave(0);
                    break;
            }
            return true;
        });

        // Disparo (click simple)
        btnShoot.setOnClickListener(v -> vistaJuego.activaMisilFromUI());
    }
}
