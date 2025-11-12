package com.example.asteroides;

import android.app.Activity;
import android.os.Bundle;

public class Juego extends Activity {

    private VistaJuego vistaJuego;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.juego);
        vistaJuego = findViewById(R.id.VistaJuego);
        vistaJuego.setPadre(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        vistaJuego.getThread().pausar();
        vistaJuego.desactivarSensores();
    }

    @Override
    protected void onResume() {
        super.onResume();
        vistaJuego.getThread().reanudar();
        vistaJuego.activarSensores();
    }


    @Override
    protected void onDestroy() {
        vistaJuego.getThread().detener();
        super.onDestroy();
    }

}