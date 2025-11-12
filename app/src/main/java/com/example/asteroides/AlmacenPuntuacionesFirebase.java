package com.example.asteroides;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AlmacenPuntuacionesFirebase implements AlmacenPuntuaciones {

    private DatabaseReference database;

    private static List<String> listaPuntuaciones = new ArrayList<>();

    public static class Puntuacion {
        public int puntos;
        public String nombre;
        public long fecha;

        public Puntuacion() {}

        public Puntuacion(int puntos, String nombre, long fecha) {
            this.puntos = puntos;
            this.nombre = nombre;
            this.fecha = fecha;
        }
    }

    public AlmacenPuntuacionesFirebase(Context context) {
        database = FirebaseDatabase.getInstance("https://asteroides-fe545-default-rtdb.firebaseio.com/").getReference();

        activarOyenteListaPuntuaciones(10);
    }

    @Override
    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        Puntuacion puntuacion = new Puntuacion(puntos, nombre, fecha);

        database.child("puntuaciones").push().setValue(puntuacion);

        Log.d("Firebase", "Puntuación guardada: " + puntos + " " + nombre);
    }

    @Override
    public List<String> listaPuntuaciones(int cantidad) {

        int max = Math.min(listaPuntuaciones.size(), cantidad);
        return listaPuntuaciones.subList(0, max);
    }

    private void activarOyenteListaPuntuaciones(int cantidad) {
        Query consulta = database.child("puntuaciones")
                .orderByChild("puntos")
                .limitToLast(cantidad);

        consulta.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaPuntuaciones.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Puntuacion puntuacion = snapshot.getValue(Puntuacion.class);
                    if (puntuacion != null) {
                        String fechaFormateada = formatFecha(puntuacion.fecha);
                        listaPuntuaciones.add(0, puntuacion.puntos + " " + puntuacion.nombre + "|" + fechaFormateada);
                    }
                }
                Log.d("Firebase", "Lista local actualizada. " + listaPuntuaciones.size() + " puntuaciones.");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Error al leer puntuaciones", databaseError.toException());
            }
        });
    }

    private String formatFecha(long fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(new Date(fecha));
    }
}
