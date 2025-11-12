package com.example.asteroides;

import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PreferenciasFragment extends PreferenceFragment {

    public static PreferenciasFragment newInstance() {
        return new PreferenciasFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferencias);


        ListPreference almacenPref = (ListPreference) findPreference("almacenamiento");

        List<CharSequence> entries = new ArrayList<>();
        List<CharSequence> entryValues = new ArrayList<>();

        entries.add("Array (en memoria)");
        entryValues.add("0");
        entries.add("Preferencias (SharedPreferences)");
        entryValues.add("1");
        entries.add("Fichero en memoria interna");
        entryValues.add("2");
        entries.add("Fichero de Recurso (solo lectura)");
        entryValues.add("3");
        entries.add("Fichero de Assets (solo lectura)");
        entryValues.add("4");
        entries.add("Fichero XML (SAX)");
        entryValues.add("5");
        entries.add("Fichero JSON (Gson)");
        entryValues.add("6");
        entries.add("Fichero JSON (org.json)");
        entryValues.add("7");
        entries.add("Base de datos SQLite");
        entryValues.add("8");
        entries.add("Base de datos SQLite Relacional");
        entryValues.add("9");
        entries.add("Servidor");
        entryValues.add("10");
        entries.add("PHP");
        entryValues.add("11");
        entries.add("PHP (AsyncTask)");
        entryValues.add("12");
        entries.add("Firebase");
        entryValues.add("13");


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            File[] dirs = getActivity().getExternalFilesDirs(null);
            if (dirs != null) {
                int i = 0;
                for (File dir : dirs) {
                    if (dir != null) {
                        String path = dir.getAbsolutePath();
                        String name = "Externo " + (i + 1);

                        if (i == 0) {
                            name += " (Principal)";
                        } else {
                            name += " (Secundario/Tarjeta SD)";
                        }

                        entries.add(name);
                        entryValues.add(path);
                        i++;
                    }
                }
            }
        }

        almacenPref.setEntries(entries.toArray(new CharSequence[0]));
        almacenPref.setEntryValues(entryValues.toArray(new CharSequence[0]));

    }
}