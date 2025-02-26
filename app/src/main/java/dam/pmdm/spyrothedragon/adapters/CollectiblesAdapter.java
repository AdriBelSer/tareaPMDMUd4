package dam.pmdm.spyrothedragon.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dam.pmdm.spyrothedragon.R;
import dam.pmdm.spyrothedragon.models.Collectible;
import dam.pmdm.spyrothedragon.ui.VideoFragment;

public class CollectiblesAdapter extends RecyclerView.Adapter<CollectiblesAdapter.CollectiblesViewHolder> {

    private List<Collectible> list;
    private Context context;

    private int clickCount = 0; // Para contar las pulsaciones consecutivas

    public CollectiblesAdapter(Context context, List<Collectible> collectibleList) {
        this.list = collectibleList;
        this.context = context;
    }

    @Override
    public CollectiblesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new CollectiblesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CollectiblesViewHolder holder, int position) {
        Collectible collectible = list.get(position);
        holder.nameTextView.setText(collectible.getName());

        // Cargar la imagen (simulado con un recurso drawable)
        int imageResId = holder.itemView.getContext().getResources().getIdentifier(collectible.getImage(), "drawable", holder.itemView.getContext().getPackageName());
        holder.imageImageView.setImageResource(imageResId);

        // Detectar clics en el elemento y verificar si es "Gemas"
        holder.itemView.setOnClickListener(v -> {
            if (collectible.getName().equals("Gemas")) {
                Log.d("count", Integer.toString(clickCount));
                clickCount++;
                if (clickCount == 4) {
                    Log.d("count", "FINISH");
                    // Aquí activamos el Easter Egg y cargamos el Fragment
                    activateEasterEgg();
                    clickCount = 0;
                }
            } else {
                clickCount = 0; // Resetear contador si el nombre no es "Gemas"
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private void activateEasterEgg() {
        // Cargar el Fragment con el video
/*        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        VideoFragment videoFragment = new VideoFragment();
        transaction.replace(R.id.fragment_container, videoFragment); // Asegúrate de tener un contenedor de Fragmentos en tu layout
        transaction.addToBackStack(null); // Permite volver atrás
        transaction.commit();*/
        VideoFragment dialog = VideoFragment.newInstance(R.raw.spyro_video);
        dialog.show(((AppCompatActivity) this.context).getSupportFragmentManager(), "VideoDialog");
    }

    public static class CollectiblesViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        ImageView imageImageView;

        public CollectiblesViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.name);
            imageImageView = itemView.findViewById(R.id.image);
        }
    }
}
