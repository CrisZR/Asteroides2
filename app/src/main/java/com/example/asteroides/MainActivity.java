package com.example.asteroides;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.example.asteroides.databinding.ActivityMainBinding;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import android.graphics.Bitmap;
import android.util.LruCache;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    public static RequestQueue colaPeticiones;
    public static ImageLoader lectorImagenes;

    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 101;

    public static AlmacenPuntuaciones almacen = new AlmacenPuntuacionesList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(this, "OnCreate", Toast.LENGTH_SHORT).show();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        colaPeticiones = Volley.newRequestQueue(this);
        binding.button04.setOnClickListener(this::lanzarPuntuaciones);
        binding.button01.setOnClickListener(this::lanzarJuego);
        lectorImagenes = new ImageLoader(colaPeticiones, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
        checkNotificationPermission();

        binding.botonArrancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService(new Intent(MainActivity.this,
                        ServicioMusica.class));
            }
        });

        binding.botonDetener.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(MainActivity.this,
                        ServicioMusica.class));
            }
        });

        binding.button03.setOnClickListener(this::lanzarAcercaDe);

        binding.button02.setOnClickListener(this::lanzarPreferencias);

        if (binding.toolbar != null) {
            setSupportActionBar(binding.toolbar);

        } else {

        }

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().
                permitNetwork().build());

        TextView titulo = findViewById(R.id.textView_titulo);
        Animation animacion = AnimationUtils.loadAnimation(this, R.anim.giro_con_zoom);
        titulo.startAnimation(animacion);

        View botonJugar = findViewById(R.id.button01);
        Animation animacionAparecer = AnimationUtils.loadAnimation(this, R.anim.aparecer);
        botonJugar.startAnimation(animacionAparecer);

        View botonPreferencias = findViewById(R.id.button02);
        Animation animacionDesplazamiento = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_derecha);
        botonPreferencias.startAnimation(animacionDesplazamiento);

        inicializarAlmacen();
    }

    private void inicializarAlmacen() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String tipoAlmacen = pref.getString("almacenamiento", "0");
        String nombreAlmacen;

        switch (tipoAlmacen) {
            case "0":
                almacen = new AlmacenPuntuacionesList();
                nombreAlmacen = "Array (List)";
                break;
            case "1":
                almacen = new AlmacenPuntuacionesPreferencias(this);
                nombreAlmacen = "Preferencias";
                break;
            case "2":
                almacen = new AlmacenPuntuacionesFicheroInterno(this);
                nombreAlmacen = "Fichero Interno";
                break;
            case "3":
                almacen = new AlmacenPuntuacionesRecursoRaw(this);
                nombreAlmacen = "Recurso Raw (Solo Lectura)";
                break;
            case "4":
                almacen = new AlmacenPuntuacionesRecursoAssets(this);
                nombreAlmacen = "Recurso Assets (Solo Lectura)";
                break;
            case "5":
                almacen = new AlmacenPuntuacionesXML_SAX(this);
                nombreAlmacen = "Fichero XML (SAX)";
                break;
            case "6":
                almacen = new AlmacenPuntuacionesGson(this);
                nombreAlmacen = "Fichero JSON (Gson)";
                break;
            case "7":
                almacen = new AlmacenPuntuacionesJSon(this);
                nombreAlmacen = "Fichero JSON (org.json)";
                break;
            case "8":
                almacen = new AlmacenPuntuacionesSQLite(this);
                nombreAlmacen = "Base de datos SQLite";
                break;
            case "9":
                almacen = new AlmacenPuntuacionesSQLiteRel(this);
                nombreAlmacen = "Base de datos SQLite Relacional";
                break;
            case "10":
                almacen = new AlmacenPuntuacionesSocket();
                nombreAlmacen = "Socket";
                break;
            case "11":
                almacen = new AlmacenPuntuacionesSW_PHP();
                nombreAlmacen = "PHP";
                break;
            case "12":
                almacen = new AlmacenPuntuacionesSW_PHP_AsyncTask(this);
                nombreAlmacen = "PHP (AsyncTask)";
                break;
            case "13":
                almacen = new AlmacenPuntuacionesFirebase(this);
                nombreAlmacen = "Firebase";
                break;

            default:
                if (tipoAlmacen.startsWith("/")) {
                    almacen = new AlmacenPuntuacionesRutaExterna(this, tipoAlmacen);
                    nombreAlmacen = "Fichero Externo (Dinámico)";
                } else {
                    almacen = new AlmacenPuntuacionesList();
                    nombreAlmacen = "Array (Default)";
                }
                break;
        }

        Toast.makeText(this, "Almacenamiento: " + nombreAlmacen + " (" + tipoAlmacen + ")", Toast.LENGTH_LONG).show();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_POST_NOTIFICATIONS);
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permiso de notificación concedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permiso de notificación denegado", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            lanzarPreferencias(null);
            return true;
        } else if (id == R.id.acercaDe) {
            View menuItemView = findViewById(item.getItemId());
            if (menuItemView != null) {
                Animation animacion = AnimationUtils.loadAnimation(this, R.anim.giro_con_zoom);
                menuItemView.startAnimation(animacion);
            }
            lanzarAcercaDe(null); 
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }



    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle estadoGuardado) {
        super.onSaveInstanceState(estadoGuardado);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle estadoGuardado) {
        super.onRestoreInstanceState(estadoGuardado);

    }


    public void lanzarPuntuaciones(View view) {
        Intent i = new Intent(this, Puntuaciones.class);
        startActivity(i);
    }

    static final int ACTIV_JUEGO = 0;
    static final int ACTIV_PREFERENCIAS = 1;
    public void lanzarJuego(View view) {
        Intent i = new Intent(this, Juego.class);
        startActivityForResult(i, ACTIV_JUEGO);
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== ACTIV_JUEGO && resultCode==RESULT_OK && data!=null) {
            int puntuacion = data.getExtras().getInt("puntuacion");
            SharedPreferences pref =
                    PreferenceManager.getDefaultSharedPreferences(this);
            String nombre = pref.getString("nombre_jugador", "Jugador");
            almacen.guardarPuntuacion(puntuacion, nombre,
                    System.currentTimeMillis());
            lanzarPuntuaciones(null);
        }
        else if (requestCode == ACTIV_PREFERENCIAS) {
            inicializarAlmacen();
        }
    }

    public void lanzarPreferencias(View view) {
        Intent i = new Intent(this, Preferencias.class);
        startActivityForResult(i, ACTIV_PREFERENCIAS);
    }

    public void lanzarAcercaDe(View view) {
        if (view != null) {
            Animation animacion = AnimationUtils.loadAnimation(this, R.anim.giro_con_zoom);
            view.startAnimation(animacion);
        }
        Intent i = new Intent(this, AcercaDe.class);
        startActivity(i);

    }

    public void mostrarPreferencias(View view) {

        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(this);
        String s = " Música: " + pref.getBoolean("musica", true)
                + "\n Gráficos: " + pref.getString("graficos", "?")
                + "\n Fragmentos: " + pref.getString("fragmentos", "?")
                + "\n Almacenamiento: " + pref.getString("almacenamiento", "?")
                + "\n Multiplayer: " + pref.getBoolean("multiplayer", false)
                + "\n NumJugadores: " + pref.getString("numJugadores", "?")
                + "\n Conexion: " + pref.getString("conexion", "?");
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }


}