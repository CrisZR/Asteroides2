package com.example.asteroides;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import android.os.Environment;
import android.widget.Toast;

public class AlmacenPuntuacionesFicheroExterno implements
        AlmacenPuntuaciones {

    private String FICHERO;
    private Context context;

    public AlmacenPuntuacionesFicheroExterno(Context context) {
        this.context = context;
        File dir = context.getExternalFilesDir(null);
        if (dir != null) {
            FICHERO = new File(dir, "puntuaciones.txt").getAbsolutePath();
        } else {
            Log.e("Asteroides", "No se pudo acceder al almacenamiento externo");
            FICHERO = new File(context.getFilesDir(), "puntuaciones.txt").getAbsolutePath();
        }
    }

    public void guardarPuntuacion(int puntos, String nombre, long fecha){
        String estado = Environment.getExternalStorageState();
        if (!estado.equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, "No puedo escribir en la memoria externa", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            FileOutputStream f = new FileOutputStream(FICHERO, true);
            String fechaFormateada = Utils.formatFecha(fecha);
            String texto = puntos + " " + nombre + "|" + fechaFormateada + "\n";
            f.write(texto.getBytes());
            f.close();
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
    }

    public List<String> listaPuntuaciones(int cantidad) {
        List<String> result = new ArrayList<String>();

        String estado = Environment.getExternalStorageState();
        if (!estado.equals(Environment.MEDIA_MOUNTED) &&
                !estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            Toast.makeText(context, "No puedo leer en la memoria externa", Toast.LENGTH_LONG).show();
            return result;
        }

        try {
            FileInputStream f = new FileInputStream(FICHERO);
            BufferedReader entrada = new BufferedReader(
                    new InputStreamReader(f));
            int n = 0;
            String linea;
            do {
                linea = entrada.readLine();
                if (linea != null) {
                    result.add(linea);
                    n++;
                }
            } while (n < cantidad && linea != null);
            f.close();
        } catch (Exception e) {
            Log.e("Asteroides", e.getMessage(), e);
        }
        return result;
    }
}