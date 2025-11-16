package com.example.asteroides;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class Juego extends Activity {

    private VistaJuego vistaJuego;
    private int tipoControl;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.juego);
        vistaJuego = findViewById(R.id.VistaJuego);
        Button btnLeft = findViewById(R.id.btnLeft);
        Button btnRight = findViewById(R.id.btnRight);
        Button btnUp = findViewById(R.id.btnUp);
        Button btnShoot = findViewById(R.id.btnShoot);

        // Izquierda
             btnLeft.setOnTouchListener((v, e) -> {
                  switch (e.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                           vistaJuego.setGiroNave(-VistaJuego.PASO_GIRO_PARA_UI);
                            break;
                            case MotionEvent.ACTION_UP:
                             case MotionEvent.ACTION_CANCEL:
                            vistaJuego.setGiroNave(0);
                            break;
                             }
                               return true;
                             });

// Derecha
        btnRight.setOnTouchListener((v, e) -> {
            switch (e.getAction()) {
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

// Acelerar
        btnUp.setOnTouchListener((v, e) -> {
            switch (e.getAction()) {
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

// Disparo
        btnShoot.setOnClickListener(v -> {
            vistaJuego.activaMisilFromUI();
        });
        vistaJuego.setPadre(this);

        RelativeLayout keyboardControls = findViewById(R.id.keyboardControls);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String control = pref.getString("controles", "1"); // 1 = sensores, 2 = teclado

        if (control.equals("2")) {
            keyboardControls.setVisibility(View.VISIBLE);
        } else {
            keyboardControls.setVisibility(View.INVISIBLE);
        }

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        tipoControl = Integer.parseInt(pref.getString("controles", "1"));

    }

    @Override
    protected void onResume() {
        super.onResume();
        vistaJuego.getThread().reanudar();

        // Sólo activar sensores si NO es teclado
        if (tipoControl != 2) {
            vistaJuego.activarSensores();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        vistaJuego.getThread().pausar();

        // Sólo desactivar sensores si no es teclado
        if (tipoControl != 2) {
            vistaJuego.desactivarSensores();
        }
    }



    @Override
    protected void onDestroy() {
        vistaJuego.getThread().detener();
        super.onDestroy();
    }

}