package com.example.asteroides; // Adjust package name if needed

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class AlmacenPuntuacionesSQLite extends SQLiteOpenHelper
        implements AlmacenPuntuaciones {

    public AlmacenPuntuacionesSQLite(Context context) {
        super(context, "puntuaciones", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE puntuaciones (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "puntos INTEGER, nombre TEXT, fecha BIGINT)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    @Override
    public void guardarPuntuacion(int puntos, String nombre, long fecha) {
        SQLiteDatabase db = getWritableDatabase();
        String nombreEscaped = nombre.replace("'", "''");
        db.execSQL("INSERT INTO puntuaciones VALUES ( null, " +
                puntos+", '"+nombreEscaped+"', "+fecha+")");
    }

    @Override
    public List<String> listaPuntuaciones(int cantidad) {
        List<String> result = new ArrayList<String>();
        SQLiteDatabase db = getReadableDatabase();
        String[] CAMPOS = {"puntos", "nombre", "fecha"};
        Cursor cursor=db.query("puntuaciones", CAMPOS, null, null,
                null, null, "puntos DESC", Integer.toString(cantidad));

        while (cursor.moveToNext()){
            long fecha = cursor.getLong(2);
            String fechaFormateada = Utils.formatFecha(fecha);
            result.add(cursor.getInt(0)+" " +cursor.getString(1) + "|" + fechaFormateada);
        }
        cursor.close();
        return result;
    }
}