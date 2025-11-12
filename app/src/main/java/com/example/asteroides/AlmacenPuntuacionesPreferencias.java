package com.example.asteroides;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class AlmacenPuntuacionesPreferencias implements
        AlmacenPuntuaciones {
    private static String PREFERENCIAS = "puntuaciones";
    private Context context;
    public AlmacenPuntuacionesPreferencias(Context context) {
        this.context = context;
    }

    public void guardarPuntuacion(int puntos, String nombre,
                                  long fecha) {
        SharedPreferences preferencias =context.getSharedPreferences(
                PREFERENCIAS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferencias.edit();
        for (int n = 9; n >= 1; n--) {
            editor.putString("puntuacion" + n,
                    preferencias.getString("puntuacion" + (n - 1), ""));
        }
        editor.putString("puntuacion0", puntos + " " + nombre);
        editor.apply();
    }

    public List<String> listaPuntuaciones(int cantidad) {
        List<String> result = new ArrayList<String>();
        SharedPreferences preferencias =context.getSharedPreferences(
                PREFERENCIAS, Context.MODE_PRIVATE);
        for (int n = 0; n < 9; n++) {
            String s = preferencias.getString("puntuacion" + n, "");
            if (!s.isEmpty()) {
                result.add(s);
            }
        }
        return result;
    }
}