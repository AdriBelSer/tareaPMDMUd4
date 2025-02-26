package dam.pmdm.spyrothedragon.ui;

import android.app.Dialog;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import dam.pmdm.spyrothedragon.R;

public class VideoFragment extends DialogFragment {

    private static final String VIDEO_RES_ID = "video_res_id";
    private VideoView videoView;
    private int originalOrientation;
    private MediaController mediaController;

    public static VideoFragment newInstance(int videoResId) {
        VideoFragment fragment = new VideoFragment();
        Bundle args = new Bundle();
        args.putInt(VIDEO_RES_ID, videoResId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            }
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        videoView = view.findViewById(R.id.videoView);
        ImageButton closeButton = view.findViewById(R.id.btnClose);

        int videoResId = getArguments().getInt(VIDEO_RES_ID);
        Uri videoUri = Uri.parse("android.resource://" + requireContext().getPackageName() + "/" + videoResId);

        // Guardar la orientación actual para restaurarla después
        originalOrientation = requireActivity().getRequestedOrientation();
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        videoView.setVideoURI(videoUri);
        videoView.setOnCompletionListener(mp -> dismiss()); // Cerrar al terminar

        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(false);
            videoView.start();
            videoView.requestFocus();
        });

        // Botón de cerrar
        closeButton.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction
                .add(android.R.id.content, this, tag)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireActivity().setRequestedOrientation(originalOrientation); // Restaurar orientación
    }
}