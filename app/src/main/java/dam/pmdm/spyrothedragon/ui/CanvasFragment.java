package dam.pmdm.spyrothedragon.ui;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import dam.pmdm.spyrothedragon.FireAnimationView;
import dam.pmdm.spyrothedragon.R;

public class CanvasFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_canvas, container, false);

        FireAnimationView fireView = view.findViewById(R.id.fireView);
        ImageButton closeButton = view.findViewById(R.id.btnFireClose);

        // Establecer el listener para la animación
        fireView.setOnAnimationEndListener(() -> {
            dismiss();
        });

        fireView.startAnimation();

        // Botón de cerrar
        closeButton.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            startRoarSound();
        }
    }

    private void startRoarSound() {
            MediaPlayer mediaPlayer = MediaPlayer.create(requireContext(), R.raw.dragon_get);
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();  // Liberar el MediaPlayer
                }
            });

    }

}