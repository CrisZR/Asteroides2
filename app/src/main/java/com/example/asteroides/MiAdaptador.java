package com.example.asteroides;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import java.util.List;

public class MiAdaptador extends RecyclerView.Adapter<MiAdaptador.ViewHolder> {

    private LayoutInflater inflador;
    private List<String> lista;
    protected View.OnClickListener onClickListener;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public TextView titulo, subtitulo;
    public MiAdaptador(Context context, List<String> lista) {
        this.lista = lista;
        inflador = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = inflador.inflate(R.layout.elemento_lista, parent, false);
        v.setOnClickListener(onClickListener);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        holder.titulo.setText(lista.get(i));
        String textoCompleto = lista.get(i);
        String[] partes = textoCompleto.split("\\|");

        holder.titulo.setText(partes[0].trim());

        if (partes.length > 1) {
            holder.subtitulo.setText(partes[1].trim());
        } else {
            holder.subtitulo.setText(""); // O "Sin fecha"
        }

        holder.icon.setImageUrl("http://mmoviles.upv.es/img/moviles.png",
                MainActivity.lectorImagenes);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titulo, subtitulo;
        public NetworkImageView icon;

        ViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.titulo);
            subtitulo = itemView.findViewById(R.id.subtitulo);
            icon = itemView.findViewById(R.id.icono);
        }
    }
}