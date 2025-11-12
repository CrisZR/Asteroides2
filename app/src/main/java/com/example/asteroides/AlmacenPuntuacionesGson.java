package com.example.asteroides;

import android.content.Context;
import android.content.SharedPreferences;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AlmacenPuntuacionesGson implements AlmacenPuntuaciones {
    private Context context;
    private Gson gson = new Gson();
    private Type type = new TypeToken<Clase>() {}.getType();
    private static final String PREF_FILE = "puntuaciones_gson";
    private static final String PREF_KEY = "puntuaciones_json";



    public AlmacenPuntuacionesGson(Context context) {
        this.context = context;
    }

    private void guardarString(String jsonString) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_KEY, jsonString);
        editor.apply();
    }

    private String leerString() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return prefs.getString(PREF_KEY, null);
    }

    @Override
    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        String string = leerString();

        Clase objeto;
        if (string == null) {
            objeto = new Clase();
        } else {
            objeto = gson.fromJson(string, type);
        }


        objeto.puntuaciones.add(new Puntuacion(puntos, nombre, fecha));
        string = gson.toJson(objeto, type);
        guardarString(string);
    }

    @Override
    public List<String> listaPuntuaciones(int cantidad) {
        String string = leerString();
        Clase objeto;
        if (string == null) {
            objeto = new Clase();
        } else {
            objeto = gson.fromJson(string, type);
        }

        List<String> salida = new ArrayList<>();
        for (Puntuacion puntuacion : objeto.puntuaciones) {
            salida.add(puntuacion.getPuntos()+ " " +puntuacion.getNombre());
        }
        return salida;
    }

    public class Clase {
        private ArrayList<Puntuacion> puntuaciones = new ArrayList<>();
        private boolean guardado;
    }
}