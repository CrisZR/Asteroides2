package com.example.asteroides; // Adjust package name if needed

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class AlmacenPuntuacionesSQLiteRel extends SQLiteOpenHelper
        implements AlmacenPuntuaciones {

    public AlmacenPuntuacionesSQLiteRel(Context context) {
        super(context, "puntuaciones", null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE usuarios ("+
                "usu_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "nombre TEXT UNIQUE, correo TEXT)"); // Added UNIQUE constraint for nombre
        db.execSQL("CREATE TABLE puntuaciones2 ("+
                "pun_id INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "puntos INTEGER, fecha BIGINT, usuario INTEGER, "+
                "FOREIGN KEY (usuario) REFERENCES usuarios (usu_id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {
            onCreate(db);
            Cursor cursor = db.rawQuery("SELECT puntos, nombre, fecha " +
                    "FROM puntuaciones", null);
            while (cursor.moveToNext()) {
                guardarPuntuacion(db, cursor.getInt(0), cursor.getString(1),
                        cursor.getLong(2));
            }
            cursor.close();
            db.execSQL("DROP TABLE puntuaciones");
        }
    }

    @Override
    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        SQLiteDatabase db = getWritableDatabase();
        guardarPuntuacion(db, puntos, nombre, fecha);
    }


    private void guardarPuntuacion(SQLiteDatabase db, int puntos,
                                   String nombre, long fecha) {
        int usuario = buscaInsertaUsuario(db, nombre);
        db.execSQL("PRAGMA foreign_keys = ON");
        db.execSQL("INSERT INTO puntuaciones2 VALUES ( null, " +
                puntos + ", " + fecha + ", " + usuario + ")");
    }

    private int buscaInsertaUsuario(SQLiteDatabase db, String nombre) {
        String nombreEscaped = nombre.replace("'", "''");
        Cursor cursor = db.rawQuery("SELECT usu_id FROM usuarios " +
                "WHERE nombre='" + nombreEscaped + "'", null);
        if (cursor.moveToNext()) {
            int result = cursor.getInt(0);
            cursor.close();
            return result;
        } else {
            cursor.close();
            db.execSQL("INSERT INTO usuarios VALUES (null, '" + nombreEscaped +
                    "', 'correo@dominio.es')");
            return buscaInsertaUsuario(db, nombre);
        }
    }

    @Override
    public List<String> listaPuntuaciones(int cantidad) {
        List<String> result = new ArrayList<String>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT p.puntos, u.nombre, p.fecha FROM " +
                "puntuaciones2 p INNER JOIN usuarios u ON p.usuario = u.usu_id ORDER BY " +
                "p.puntos DESC LIMIT " + cantidad, null);

        while (cursor.moveToNext()){
            long fecha = cursor.getLong(2);
            String fechaFormateada = Utils.formatFecha(fecha);
            result.add(cursor.getInt(0)+ " " +cursor.getString(1) + "|" + fechaFormateada);
        }
        cursor.close();
        return result;
    }
}