package com.example.asteroides;

import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlmacenPuntuacionesList implements AlmacenPuntuaciones {
    private List<String> puntuaciones;

    public AlmacenPuntuacionesList() {
        puntuaciones = new ArrayList<String>();
        puntuaciones.add("123000 Pepito DomingeZ|10/11/2025");
        puntuaciones.add("111000 Pedro Martinez|09/11/2025");
        puntuaciones.add("011000 Paco Pérez|08/11/2025");
    }

    @Override
    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        String fechaFormateada = formatFecha(fecha);
        puntuaciones.add(0, puntos + " " + nombre + "|" + fechaFormateada);
    }

    @Override
    public List<String> listaPuntuaciones(int cantidad) {
        return puntuaciones;
    }

    private String formatFecha(long fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(fecha));
    }
}