package com.example.asteroides; // O tu paquete

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.asteroides.AlmacenPuntuaciones;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AlmacenPuntuacionesSW_PHP_AsyncTask implements AlmacenPuntuaciones {

    private Context contexto;

    private static final String URL_SERVIDOR = "http://192.168.0.8/puntuaciones";

    public AlmacenPuntuacionesSW_PHP_AsyncTask(Context contexto) {
        this.contexto = contexto;
    }

    @Override
    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        try {
            TareaGuardar tarea = new TareaGuardar();
            tarea.execute(String.valueOf(puntos), nombre, String.valueOf(fecha));
            tarea.get(4, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            Toast.makeText(contexto, "Tiempo excedido al conectar (Guardar)", Toast.LENGTH_LONG).show();
        } catch (CancellationException e) {
            Toast.makeText(contexto, "Error al conectar con servidor (Guardar)", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(contexto, "Error con tarea asíncrona (Guardar)", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public List<String> listaPuntuaciones(int cantidad) {
        try {
            Tarealista tarea = new Tarealista();
            tarea.execute(cantidad);
            return tarea.get(4, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            Toast.makeText(contexto, "Tiempo excedido al conectar (Listar)", Toast.LENGTH_LONG).show();
        } catch (CancellationException e) {
            Toast.makeText(contexto, "Error al conectar con servidor (Listar)", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(contexto, "Error con tarea asíncrona (Listar)", Toast.LENGTH_LONG).show();
        }
        return new ArrayList<>();
    }

    private class TareaGuardar extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... param) {
            HttpURLConnection conexion = null;
            try {
                String urlStr = URL_SERVIDOR + "/nueva.php?"
                        + "puntos=" + param[0]
                        + "&nombre=" + URLEncoder.encode(param[1], "UTF-8")
                        + "&fecha=" + param[2];
                URL url = new URL(urlStr);
                conexion = (HttpURLConnection) url.openConnection();
                conexion.setConnectTimeout(5000);
                conexion.setReadTimeout(5000);

                if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                    String linea = reader.readLine();
                    if (linea == null || !linea.equals("OK")) {
                        Log.e("AsteroidesPHP_Async", "Error en servicio Web nueva.php: " + linea);
                        cancel(true);
                    }
                } else {
                    Log.e("AsteroidesPHP_Async", "Error al conectar con nueva.php: " + conexion.getResponseMessage());
                    cancel(true);
                }
            } catch (Exception e) {
                Log.e("AsteroidesPHP_Async", "Error en TareaGuardar: " + e.getMessage(), e);
                cancel(true);
            } finally {
                if (conexion != null) {
                    conexion.disconnect();
                }
            }
            return null; // [cite: 1881]
        }
    }

    // =============================================================
    // === Tarea Asíncrona para LISTAR Puntuaciones ===
    // =============================================================
    private class Tarealista extends AsyncTask<Integer, Void, List<String>> { // [cite: 1845]
        @Override
        protected List<String> doInBackground(Integer... cantidad) { // [cite: 1847]
            List<String> result = new ArrayList<>();
            HttpURLConnection conexion = null;
            try {
                // Construir la URL
                URL url = new URL(URL_SERVIDOR + "/lista.php?max=" + cantidad[0]); // [cite: 1849]
                conexion = (HttpURLConnection) url.openConnection();
                conexion.setConnectTimeout(5000);
                conexion.setReadTimeout(5000);

                if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                    String linea = reader.readLine();
                    while (linea != null && !linea.isEmpty() && result.size() < cantidad[0]) {
                        result.add(linea);
                        linea = reader.readLine();
                    }
                    reader.close();
                } else {
                    Log.e("AsteroidesPHP_Async", "Error al conectar con lista.php: " + conexion.getResponseMessage());
                    cancel(true); // Cancelar la tarea si hay error HTTP [cite: 1887]
                }
            } catch (Exception e) {
                Log.e("AsteroidesPHP_Async", "Error en Tarealista: " + e.getMessage(), e);
                cancel(true); // Cancelar la tarea si hay una excepción [cite: 1887]
            } finally {
                if (conexion != null) {
                    conexion.disconnect();
                }
            }
            return result; // [cite: 1830]
        }
    }
}