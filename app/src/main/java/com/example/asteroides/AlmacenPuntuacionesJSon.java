package com.example.asteroides;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.SharedPreferences;

public class AlmacenPuntuacionesJSon implements AlmacenPuntuaciones {

    private Context context;
    private static final String PREF_FILE = "puntuaciones_json_org";
    private static final String PREF_KEY = "puntuaciones_json";
    public AlmacenPuntuacionesJSon(Context context) {
        this.context = context;
    }

    private void guardarString(String jsonString) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_KEY, jsonString);
        editor.apply();
    }

    @Override
    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        String string = leerString();
        List<Puntuacion> puntuaciones = leerJson(string);
        puntuaciones.add(new Puntuacion(puntos, nombre, fecha));
        string = guardarJson(puntuaciones);
        guardarString(string);
    }
    private String leerString() {
        SharedPreferences prefs = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        return prefs.getString(PREF_KEY, null);
    }

    private List<Puntuacion> leerJson(String string) {
        List<Puntuacion> puntuaciones = new ArrayList<>();
        if (string == null) {
            return puntuaciones;
        }
        try {
            JSONArray json_array = new JSONArray(string);
            for (int i = 0; i < json_array.length(); i++) {
                JSONObject objeto = json_array.getJSONObject(i);
                puntuaciones.add(new Puntuacion(objeto.getInt("puntos"),
                        objeto.getString("nombre"), objeto.getLong("fecha")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return puntuaciones;
    }

    private String guardarJson(List<Puntuacion> puntuaciones) {
        String string = "";
        try {
            JSONArray jsonArray = new JSONArray();
            for (Puntuacion puntuacion : puntuaciones) {
                JSONObject objeto = new JSONObject();
                objeto.put("puntos", puntuacion.getPuntos());
                objeto.put("nombre", puntuacion.getNombre());
                objeto.put("fecha", puntuacion.getFecha());
                jsonArray.put(objeto);
            }
            string = jsonArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return string;
    }

    @Override
    public List<String> listaPuntuaciones(int cantidad) {
        String string = leerString();
        List<Puntuacion> puntuaciones = leerJson(string);

        List<String> salida = new ArrayList<>();
        int count = 0;
        for (Puntuacion puntuacion : puntuaciones) {
            salida.add(puntuacion.getPuntos() + " " + puntuacion.getNombre());
            count++;
            if (count >= cantidad) {
                break;
            }
        }
        return salida;
    }


}