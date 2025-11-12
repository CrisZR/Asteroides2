package com.example.asteroides;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {

    public static String formatFecha(long fecha) {
        if (fecha == 0) {
            return "Sin fecha";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(fecha));
    }
}