package com.example.asteroides;

import android.os.StrictMode;
import android.util.Log;

import com.example.asteroides.AlmacenPuntuaciones;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class AlmacenPuntuacionesSW_PHP implements AlmacenPuntuaciones {

    private static final String URL_SERVIDOR = "http://192.168.0.8/puntuaciones";

    public AlmacenPuntuacionesSW_PHP() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .permitNetwork().build());
    }

    @Override
    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        HttpURLConnection conexion = null;
        try {
            String urlStr = URL_SERVIDOR + "/nueva.php?"
                    + "puntos=" + puntos
                    + "&nombre=" + URLEncoder.encode(nombre, "UTF-8")
                    + "&fecha=" + fecha;

            URL url = new URL(urlStr);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setConnectTimeout(5000);
            conexion.setReadTimeout(5000);

            if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                String linea = reader.readLine();
                if (linea == null || !linea.equals("OK")) {
                    Log.e("AsteroidesPHP", "Error en servicio Web nueva.php: " + linea);
                }
            } else {
                Log.e("AsteroidesPHP", "Error al conectar con nueva.php: " + conexion.getResponseMessage());
            }
        } catch (Exception e) {
            Log.e("AsteroidesPHP", "Error en guardarPuntuacion (PHP): " + e.getMessage(), e);
        } finally {
            if (conexion != null) {
                conexion.disconnect();
            }
        }
    }

    @Override
    public List<String> listaPuntuaciones(int cantidad) {
        List<String> result = new ArrayList<>();
        HttpURLConnection conexion = null;
        try {
            URL url = new URL(URL_SERVIDOR + "/lista.php?max=" + cantidad);
            conexion = (HttpURLConnection) url.openConnection();
            conexion.setConnectTimeout(5000);
            conexion.setReadTimeout(5000);

            if (conexion.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
                String linea = reader.readLine();
                while (linea != null && !linea.isEmpty() && result.size() < cantidad) {
                    result.add(linea);
                    linea = reader.readLine();
                }
                reader.close();
            } else {
                Log.e("AsteroidesPHP", "Error al conectar con lista.php: " + conexion.getResponseMessage());
            }
        } catch (Exception e) {
            Log.e("AsteroidesPHP", "Error en listaPuntuaciones (PHP): " + e.getMessage(), e);
        } finally {
            if (conexion != null) {
                conexion.disconnect();
            }
        }
        return result;
    }
}
