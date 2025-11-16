package com.example.asteroides;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.graphics.Path;

import androidx.appcompat.content.res.AppCompatResources;

import java.util.ArrayList;
import java.util.List;

public class VistaJuego extends View implements SensorEventListener {

    // ///// NAVE /////
    private Grafico nave;
    private int giroNave;

    // constantes públicas para UI (usadas por JuegoActivity)
    // reusamos las constantes privadas ya definidas abajo
    public static final int PASO_GIRO_PARA_UI;           // inicializadas en bloque estático abajo
    public static final float PASO_ACELERACION_PARA_UI;  // inicializadas en bloque estático abajo

    private boolean efectosActivados;
    private double aceleracionNave;
    private static final int MAX_VELOCIAD_NAVE = 20;
    private static final int PASO_GIRO_NAVE = 5;
    private static final float PASO_ACELERACION_NAVE = 0.5f;
    static {
        // inicializar las constantes públicas a partir de las privadas
        PASO_GIRO_PARA_UI = PASO_GIRO_NAVE;
        PASO_ACELERACION_PARA_UI = PASO_ACELERACION_NAVE;
    }

    private int puntuacion = 0;
    private Activity padre;
    public void setPadre(Activity padre){
        this.padre = padre;
    }

    // ASTEROIDES
    private List<Grafico> asteroides;
    private int numAsteroides = 5;
    private int numFragmentos;

    private Drawable drawableAsteroide[] = new Drawable[3];

    private static final Path pathAsteroide = new Path();
    static {
        pathAsteroide.moveTo(0.3f, 0.0f);
        pathAsteroide.lineTo(0.6f, 0.0f);
        pathAsteroide.lineTo(0.6f, 0.3f);
        pathAsteroide.lineTo(0.8f, 0.2f);
        pathAsteroide.lineTo(1.0f, 0.4f);
        pathAsteroide.lineTo(0.8f, 0.6f);
        pathAsteroide.lineTo(0.9f, 0.9f);
        pathAsteroide.lineTo(0.5f, 1.0f);
        pathAsteroide.lineTo(0.2f, 0.8f);
        pathAsteroide.lineTo(0.0f, 0.7f);
        pathAsteroide.lineTo(0.0f, 0.2f);
        pathAsteroide.close();
    }

    // THREAD Y TIEMPO
    private ThreadJuego thread = new ThreadJuego();
    private static int PERIODO_PROCESO = 50;
    private long ultimoProceso = 0;

    // //// MISILES //////
    private List<Grafico> misiles;
    private List<Integer> tiempoMisiles;
    private static int PASO_VELOCIDAD_MISIL = 20;
    private Drawable drawableMisil;

    private int tipoControl;

    // //// MULTIMEDIA //////
    SoundPool soundPool;
    int idDisparo, idExplosion;
    private SensorManager mSensorManager;
    private Sensor accelerometerSensor;

    public VistaJuego(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);

        soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC,0);

        idDisparo = soundPool.load(context, R.raw.disparo, 0);
        idExplosion = soundPool.load(context, R.raw.explosion, 0);

        Drawable drawableNave = AppCompatResources.getDrawable(context, R.drawable.nave);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());

        efectosActivados = pref.getBoolean("efectos", true);

        String tipoGraficos = pref.getString("graficos", "1");
        tipoControl = Integer.parseInt(pref.getString("controles", "0"));

        String sFragmentos = pref.getString("fragmentos", "3");
        try {
            numFragmentos = Integer.parseInt(sFragmentos);
        } catch (NumberFormatException e) {
            numFragmentos = 3;
        }

        this.setBackground(AppCompatResources.getDrawable(context, R.drawable.fondo));

        if (tipoGraficos.equals("0")) {
            drawableAsteroide[0] = AppCompatResources.getDrawable(context, R.drawable.asteroide_vectorial_grande);
            drawableAsteroide[1] = AppCompatResources.getDrawable(context, R.drawable.asteroide_vectorial_mediano);
            drawableAsteroide[2] = AppCompatResources.getDrawable(context, R.drawable.asteroide_vectorial_pequeno);

            drawableNave = AppCompatResources.getDrawable(context, R.drawable.nave_vectorial);
            drawableMisil = AppCompatResources.getDrawable(context, R.drawable.misil_vectorial);
            this.setBackground(AppCompatResources.getDrawable(context, R.drawable.fondo_vectorial_negro));

        } else if (tipoGraficos.equals("2")) {
            drawableAsteroide[0] = AppCompatResources.getDrawable(context, R.drawable.asteroide1);
            drawableAsteroide[1] = AppCompatResources.getDrawable(context, R.drawable.asteroide2);
            drawableAsteroide[2] = AppCompatResources.getDrawable(context, R.drawable.asteroide3);
            drawableNave = AppCompatResources.getDrawable(context, R.drawable.nave);
            drawableMisil = AppCompatResources.getDrawable(context, R.drawable.misil1);

        } else {
            drawableAsteroide[0] = AppCompatResources.getDrawable(context, R.drawable.asteroide1);
            drawableAsteroide[1] = AppCompatResources.getDrawable(context, R.drawable.asteroide2);
            drawableAsteroide[2] = AppCompatResources.getDrawable(context, R.drawable.asteroide3);
            drawableNave = AppCompatResources.getDrawable(context, R.drawable.nave);
            drawableMisil = AppCompatResources.getDrawable(context, R.drawable.misil1);

        }
        asteroides = new ArrayList<>();
        for (int i = 0; i < numAsteroides; i++) {
            Drawable dAsteroide = drawableAsteroide[(int) (Math.random() * drawableAsteroide.length)];
            Grafico asteroide = new Grafico(this, dAsteroide);
            asteroide.setIncY((int) (Math.random() * 4) - 2);
            asteroide.setIncX((int) (Math.random() * 4) - 2);
            asteroide.setAngulo((int) (Math.random() * 360));
            asteroide.setRotacion((int) (Math.random() * 8 - 4));
            asteroides.add(asteroide);
        }
        nave = new Grafico(this, drawableNave);
        misiles = new ArrayList<>();
        tiempoMisiles = new ArrayList<>();

        if (tipoControl == 1) {
            mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            List<Sensor> listSensors = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
            if (!listSensors.isEmpty()) {
                accelerometerSensor = listSensors.get(0);
            }
        }
    }

    public void activarSensores() {
        if (mSensorManager != null && accelerometerSensor != null) {
            mSensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    public void desactivarSensores() {
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(this);
        }
    }

    @Override
    protected void onSizeChanged(int ancho, int alto, int ancho_anter, int alto_anter) {
        super.onSizeChanged(ancho, alto, ancho_anter, alto_anter);
        nave.setCenX(ancho / 2);
        nave.setCenY(alto / 2);
        for (Grafico asteroide : asteroides) {
            do {
                asteroide.setCenX((int) (Math.random() * ancho));
                asteroide.setCenY((int) (Math.random() * alto));
            } while (asteroide.distancia(nave) < (ancho + alto) / 5);
        }
        ultimoProceso = System.currentTimeMillis();
        thread.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized (asteroides) {
            for (Grafico asteroide : asteroides) {
                asteroide.dibujaGrafico(canvas);
            }
        }
        nave.dibujaGrafico(canvas);
        synchronized (misiles) {
            for (Grafico misil : misiles) {
                misil.dibujaGrafico(canvas);
            }
        }
    }

    protected void actualizaFisica() {
        long ahora = System.currentTimeMillis();
        if (ultimoProceso + PERIODO_PROCESO > ahora) {
            return;
        }
        double factorMov = (ahora - ultimoProceso) / PERIODO_PROCESO;
        ultimoProceso = ahora;

        nave.setAngulo((int) (nave.getAngulo() + giroNave * factorMov));
        double nIncX = nave.getIncX() + aceleracionNave * Math.cos(Math.toRadians(nave.getAngulo())) * factorMov;
        double nIncY = nave.getIncY() + aceleracionNave * Math.sin(Math.toRadians(nave.getAngulo())) * factorMov;
        if (Math.hypot(nIncX, nIncY) <= MAX_VELOCIAD_NAVE) {
            nave.setIncX(nIncX);
            nave.setIncY(nIncY);
        }
        nave.incrementaPos(factorMov);
        synchronized (asteroides) {
            for (Grafico asteroide : asteroides) {
                asteroide.incrementaPos(factorMov);
            }
        }

        synchronized (misiles) {
            for (int m = misiles.size() - 1; m >= 0; m--) {
                Grafico misil = misiles.get(m);
                misil.incrementaPos(factorMov);
                int tiempoRestante = tiempoMisiles.get(m) - (int) factorMov;
                tiempoMisiles.set(m, tiempoRestante);

                if (tiempoRestante < 0) {
                    misiles.remove(m);
                    tiempoMisiles.remove(m);
                } else {
                    for (int i = asteroides.size() - 1; i >= 0; i--) {
                        if (misil.verificaColision(asteroides.get(i))) {
                            destruyeAsteroide(i);
                            misiles.remove(m);
                            tiempoMisiles.remove(m);
                            break;
                        }
                    }
                }
            }
        }

        for (Grafico asteroide: asteroides){
            if(asteroide.verificaColision(nave)){
                salir();
            }
        }
        // forzar redibujado tras actualizar física
        this.postInvalidate();

    }

    class ThreadJuego extends Thread {
        private boolean pausa, corriendo;

        public synchronized void pausar() {
            pausa = true;
        }

        public synchronized void reanudar() {
            pausa = false;
            notify();
        }

        public void detener() {
            corriendo = false;
            if (pausa) reanudar();
        }

        @Override
        public void run() {
            corriendo = true;
            while (corriendo) {
                actualizaFisica();
                synchronized (this) {
                    while (pausa) {
                        try {
                            wait();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }
    }

    private void destruyeAsteroide(int i) {
        if(asteroides.get(i).getDrawable()!=drawableAsteroide[2]){
            int tamaño;
            if(asteroides.get(i).getDrawable()==drawableAsteroide[1]){
                tamaño =2;
            } else {
                tamaño =1;
            }
            for(int n=0;n<numFragmentos;n++){
                Grafico asteroide = new Grafico(this,drawableAsteroide[tamaño]);
                asteroide.setCenX(asteroides.get(i).getCenX());
                asteroide.setCenY(asteroides.get(i).getCenY());
                asteroide.setIncX((Math.random()*4-2));
                asteroide.setIncY((Math.random()*4-2));
                asteroide.setAngulo((int)(Math.random()*360));
                asteroide.setRotacion((int)(Math.random()*8-4));
                asteroides.add(asteroide);
            }
        }

        if (efectosActivados) {
            soundPool.play(idExplosion, 1, 1, 0, 0, 1);
        }
        synchronized (asteroides) {
            asteroides.remove(i);
        }
        if (asteroides.isEmpty()){
            salir();
        }
        puntuacion += 1000;
        this.postInvalidate();
    }

    private void activaMisil() {
        if (efectosActivados) {
            soundPool.play(idDisparo, 1, 1, 1, 0, 1);
        }
        if (drawableMisil != null) {
            Grafico misil = new Grafico(this, drawableMisil);

            misil.setCenX(nave.getCenX());
            misil.setCenY(nave.getCenY());

            misil.setAngulo(nave.getAngulo());

            double velMisilX = Math.cos(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL;
            double velMisilY = Math.sin(Math.toRadians(misil.getAngulo())) * PASO_VELOCIDAD_MISIL;

            double velNaveX = nave.getIncX();
            double velNaveY = nave.getIncY();

            misil.setIncX(velMisilX + velNaveX);
            misil.setIncY(velMisilY + velNaveY);

            synchronized (misiles) {
                misiles.add(misil);
                tiempoMisiles.add(80);
            }
            this.postInvalidate();
        }
    }

    // wrapper público para que la UI pueda disparar
    public void activaMisilFromUI() {
        activaMisil();
    }

    @Override
    public boolean onKeyDown(int codigoTecla, KeyEvent evento) {
        super.onKeyDown(codigoTecla, evento);
        if (tipoControl != 2) {
            return false;
        }
        boolean procesada = true;
        switch (codigoTecla) {
            case KeyEvent.KEYCODE_DPAD_UP:
                aceleracionNave = +PASO_ACELERACION_NAVE;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                giroNave = -PASO_GIRO_NAVE;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                giroNave = +PASO_GIRO_NAVE;
                break;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                activaMisil();
                break;
            default:
                procesada = false;
                break;
        }
        return procesada;
    }

    @Override
    public boolean onKeyUp(int codigoTecla, KeyEvent evento) {
        super.onKeyUp(codigoTecla, evento);
        if (tipoControl != 2) {
            return false;
        }
        boolean procesada = true;
        switch (codigoTecla) {
            case KeyEvent.KEYCODE_DPAD_UP:
                aceleracionNave = 0;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                giroNave = 0;
                break;
            default:
                procesada = false;
                break;
        }
        return procesada;
    }

    // Métodos públicos usados por la UI (JuegoActivity)
    public void setGiroNave(int giro) {
        this.giroNave = giro;
    }

    public void setAceleracionNave(double aceleracion) {
        this.aceleracionNave = aceleracion;
    }

    private float mX = 0, mY = 0;
    private boolean disparo = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (tipoControl != 0) {
            return false;
        }
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                disparo = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - mX);
                float dy = Math.abs(y - mY);
                if (dy < 6 && dx > 6) {
                    giroNave = Math.round((x - mX) / 2);
                    disparo = false;
                } else if (dx < 6 && dy > 6) {
                    aceleracionNave = Math.round((mY - y) / 25);
                    disparo = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                giroNave = 0;
                aceleracionNave = 0;
                if (disparo) {
                    activaMisil();
                }
                break;
        }
        mX = x;
        mY = y;
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float valorGiro = event.values[1];
        giroNave = (int) (valorGiro * -2);
        float valorAceleracion = event.values[0];
        aceleracionNave = -valorAceleracion / 20.0f;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public ThreadJuego getThread() {
        return thread;
    }

    public void setThread(ThreadJuego thread) {
        this.thread = thread;
    }

    private void salir() {
        Bundle bundle = new Bundle();
        bundle.putInt("puntuacion", puntuacion);
        Intent intent = new Intent();
        intent.putExtras(bundle);
        padre.setResult(Activity.RESULT_OK, intent);
        padre.finish();
    }
}
