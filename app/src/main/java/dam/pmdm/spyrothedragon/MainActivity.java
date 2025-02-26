package dam.pmdm.spyrothedragon;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;

import dam.pmdm.spyrothedragon.databinding.ActivityMainBinding;
import dam.pmdm.spyrothedragon.databinding.GuideCharactersBinding;
import dam.pmdm.spyrothedragon.databinding.GuideCollectiblesBinding;
import dam.pmdm.spyrothedragon.databinding.GuideFinalBinding;
import dam.pmdm.spyrothedragon.databinding.GuideInfoIconBinding;
import dam.pmdm.spyrothedragon.databinding.GuideWelcomeBinding;
import dam.pmdm.spyrothedragon.databinding.GuideWorldsBinding;

public class MainActivity extends AppCompatActivity {

    NavController navController = null;
    private ActivityMainBinding binding;
    private GuideWelcomeBinding guideBinding;
    private GuideCharactersBinding guideCharactersBinding;
    private GuideWorldsBinding guideWorldsBinding;
    private GuideCollectiblesBinding guideCollectiblesBinding;
    private GuideInfoIconBinding guideInfoIconBinding;
    private GuideFinalBinding guideFinalBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        guideBinding = binding.includeLayoutWelcome;
        guideCharactersBinding = binding.includeLayoutCharacters;
        guideWorldsBinding = binding.includeLayoutWorlds;
        guideCollectiblesBinding = binding.includeLayoutCollectibles;
        guideInfoIconBinding = binding.includeLayoutInfoIcon;
        guideFinalBinding = binding.includeLayoutFinal;
        setContentView(binding.getRoot());

        setDefaultActionBar();

        Fragment navHostFragment = getSupportFragmentManager().findFragmentById(R.id.navHostFragment);
        if (navHostFragment != null) {
            navController = NavHostFragment.findNavController(navHostFragment);
            NavigationUI.setupWithNavController(binding.navView, navController);
            NavigationUI.setupActionBarWithNavController(this, navController);
        }

        binding.navView.setOnItemSelectedListener(this::selectedBottomMenu);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_characters ||
                    destination.getId() == R.id.navigation_worlds ||
                    destination.getId() == R.id.navigation_collectibles) {
                //Quitar el botón back en los fragments
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            }
        });

        MyApp app = (MyApp) getApplication();
        boolean showGuide = app.getSavedNeedGuide();
        if (showGuide)
            initializeGuide();

    }

    private void initializeGuide() {
        guideBinding.btnStartGuide.setOnClickListener(view -> {
            soundOnClickButtonStart(view); // Reproduce el sonido
            onStartGuide(view); // Inicia la guía
        });
        guideBinding.btnExitGuide.setOnClickListener(view -> {
            soundOnClickButtonFinish(view);
            onExitGuide(view);
        });
        guideBinding.guideWelcomeLayout.setVisibility(View.VISIBLE);

    }

    private void onStartGuide(View view) {
        guideBinding.guideWelcomeLayout.setVisibility(View.GONE);
        guideCharactersBinding.guideCharactersLayout.setVisibility(View.VISIBLE);
        goToCharacters();

    }
    private void soundOnClickButtonStart(View view) {
        // Reproducir sonido al pulsar el botón de inicio
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.dragon_get);
        mediaPlayer.start();

        // Establecer el listener para liberar recursos después de la reproducción
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();  // Liberar el MediaPlayer
            }
        });
    }

    private void soundOnClickButtonNext(View view) {
        // Reproducir sonido al pulsar el botón de inicio
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.gem_chest);
        mediaPlayer.start();

        // Establecer el listener para liberar recursos después de la reproducción
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();  // Liberar el MediaPlayer
            }
        });
    }

    private void soundOnClickButtonFinish(View view) {
        // Reproducir sonido al pulsar el botón de inicio
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.flameout);
        mediaPlayer.start();

        // Establecer el listener para liberar recursos después de la reproducción
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();  // Liberar el MediaPlayer
            }
        });
    }

    private void onExitGuide(View view) {
        MyApp app = (MyApp) getApplication();
        app.saveNeedGuide(false);

        guideBinding.guideWelcomeLayout.setVisibility(View.GONE);
        guideCharactersBinding.guideCharactersLayout.setVisibility(View.GONE);
        guideWorldsBinding.guideWorldsLayout.setVisibility(View.GONE);
        guideCollectiblesBinding.guideCollectiblesLayout.setVisibility(View.GONE);
        guideInfoIconBinding.guideInfoIconLayout.setVisibility(View.GONE);
        guideFinalBinding.guideFinalLayout.setVisibility(View.GONE);

        selectedBottomMenuById(R.id.nav_characters);


    }

    private void goToCharacters() {
        guideCharactersBinding.btnExitGuideCharacters.setOnClickListener(this::onExitGuide);
        guideCharactersBinding.btnNextGuideCharacters.setOnClickListener(view -> {
            soundOnClickButtonNext(view); // Reproduce el sonido
            goToWorlds(view);
        });
        guideCharactersBinding.btnExitGuideCharacters.setOnClickListener(view -> {
            soundOnClickButtonFinish(view);
            onExitGuide(view);
        });
        guideCharactersBinding.guideCharactersLayout.setVisibility(View.VISIBLE);
        //Para ir a una de las pantallas del menu
        selectedBottomMenuById(R.id.nav_characters);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(
                guideCharactersBinding.pulseImageCharacters, "scaleX", 1f, 0.5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(
                guideCharactersBinding.pulseImageCharacters, "scaleY", 1f, 0.5f);
        scaleX.setRepeatCount(3);
        scaleY.setRepeatCount(3);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.setDuration(1000);

        animatorSet.start();


    }

    private void goToWorlds(View view) {
        guideCharactersBinding.guideCharactersLayout.setVisibility(View.GONE);
        guideWorldsBinding.btnNextGuideWorlds.setOnClickListener(v -> {
            soundOnClickButtonNext(view); // Reproduce el sonido
            goToCollectibles(view);
        });
        guideWorldsBinding.btnExitGuideWorlds.setOnClickListener(v -> {
            soundOnClickButtonFinish(view);
            onExitGuide(view);
        });


        guideWorldsBinding.guideWorldsLayout.setVisibility(View.VISIBLE);
        selectedBottomMenuById(R.id.nav_worlds);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(
                guideWorldsBinding.pulseImageWorlds, "scaleX", 1f, 0.5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(
                guideWorldsBinding.pulseImageWorlds, "scaleY", 1f, 0.5f);
        scaleX.setRepeatCount(3);
        scaleY.setRepeatCount(3);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.setDuration(1000);

        animatorSet.start();

    }


    private void goToCollectibles(View view) {
        guideWorldsBinding.guideWorldsLayout.setVisibility(View.GONE);
        guideCollectiblesBinding.btnNextGuideCollectibles.setOnClickListener(v -> {
            soundOnClickButtonNext(view); // Reproduce el sonido
            goToInfoIcon(view);
        });
        guideCollectiblesBinding.btnExitGuideCollectibles.setOnClickListener(v -> {
            soundOnClickButtonFinish(view);
            onExitGuide(view);
        });

        guideCollectiblesBinding.guideCollectiblesLayout.setVisibility(View.VISIBLE);
        selectedBottomMenuById(R.id.nav_collectibles);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(
                guideCollectiblesBinding.pulseImageCollectibles, "scaleX", 1f, 0.5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(
                guideCollectiblesBinding.pulseImageCollectibles, "scaleY", 1f, 0.5f);
        scaleX.setRepeatCount(3);
        scaleY.setRepeatCount(3);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.setDuration(1000);

        animatorSet.start();

    }

    private void goToInfoIcon(View view) {
        guideCollectiblesBinding.guideCollectiblesLayout.setVisibility(View.GONE);
        guideInfoIconBinding.btnNextGuideInfoIcon.setOnClickListener(v -> {
            soundOnClickButtonNext(view); // Reproduce el sonido
            goToFinal(view);
        });
        guideInfoIconBinding.btnExitGuideInfoIcon.setOnClickListener(v -> {
                    soundOnClickButtonFinish(view);
                    onExitGuide(view);
                });
        guideInfoIconBinding.guideInfoIconLayout.setVisibility(View.VISIBLE);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(
                guideInfoIconBinding.pulseImageInfoIcon, "scaleX", 1f, 0.5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(
                guideInfoIconBinding.pulseImageInfoIcon, "scaleY", 1f, 0.5f);
        scaleX.setRepeatCount(3);
        scaleY.setRepeatCount(3);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.setDuration(1000);
        animatorSet.start();

        new Handler(Looper.getMainLooper()).postDelayed(this::showInfoDialog, 2000);

    }

    private void goToFinal(View view) {
        guideInfoIconBinding.guideInfoIconLayout.setVisibility(View.GONE);
        guideFinalBinding.btnExitGuideFinal.setVisibility(View.INVISIBLE);
        guideFinalBinding.btnExitGuideFinal.setOnClickListener(v -> {
            soundOnClickButtonFinish(view);
            onExitGuide(view);
        });
        guideFinalBinding.guideFinalLayout.setVisibility(View.VISIBLE);

        long delay = 0; // Empezamos sin retraso

        animateSection(
                guideFinalBinding.avArrowSelectionCharacterFinal,
                guideFinalBinding.pulseImageCharactersFinal,
                guideFinalBinding.textStepCharactersFinal,
                delay = 0
        );

        animateSection(
                guideFinalBinding.avArrowSelectionWorldsFinal,
                guideFinalBinding.pulseImageWorldsFinal,
                guideFinalBinding.textStepWorldsFinal,
                delay = 4000
        );

        animateSection(
                guideFinalBinding.avArrowSelectionCollectiblesFinal,
                guideFinalBinding.pulseImageCollectiblesFinal,
                guideFinalBinding.textStepCollectiblesFinal,
                delay = 8000
        );

        animateSection(
                guideFinalBinding.avArrowSelectionInfoIconFinal,
                guideFinalBinding.pulseImageInfoIconFinal,
                guideFinalBinding.textStepInfoIconFinal,
                delay = 12000
        );

        // Mostrar botón de salida al final
        showExitButton(delay = 16000);
    }

    private void animateSection(View arrow, View pulse, View text, long startDelay) {
        long duration = 2000; // Duración de aparición

        arrow.setAlpha(0f);
        arrow.setScaleX(0.5f);
        arrow.setScaleY(0.5f);

        pulse.setAlpha(0f);
        pulse.setScaleX(0.5f);
        pulse.setScaleY(0.5f);

        text.setAlpha(0f);
        text.setScaleX(0.5f);
        text.setScaleY(0.5f);

        ObjectAnimator fadeInArrow = ObjectAnimator.ofFloat(arrow, "alpha", 0f, 1f);
        ObjectAnimator fadeInPulse = ObjectAnimator.ofFloat(pulse, "alpha", 0f, 1f);
        ObjectAnimator fadeInText = ObjectAnimator.ofFloat(text, "alpha", 0f, 1f);

        ObjectAnimator scaleUpArrowX = ObjectAnimator.ofFloat(arrow, "scaleX", 0.5f, 1f);
        ObjectAnimator scaleUpArrowY = ObjectAnimator.ofFloat(arrow, "scaleY", 0.5f, 1f);
        ObjectAnimator scaleUpPulseX = ObjectAnimator.ofFloat(pulse, "scaleX", 0.5f, 1f);
        ObjectAnimator scaleUpPulseY = ObjectAnimator.ofFloat(pulse, "scaleY", 0.5f, 1f);
        ObjectAnimator scaleUpTextX = ObjectAnimator.ofFloat(text, "scaleX", 0.5f, 1f);
        ObjectAnimator scaleUpTextY = ObjectAnimator.ofFloat(text, "scaleY", 0.5f, 1f);

        AnimatorSet appearSet = new AnimatorSet();
        appearSet.playTogether(
                fadeInArrow, fadeInPulse, fadeInText,
                scaleUpArrowX, scaleUpArrowY,
                scaleUpPulseX, scaleUpPulseY,
                scaleUpTextX, scaleUpTextY
        );
        appearSet.setDuration(duration);
        appearSet.setStartDelay(startDelay);

        // Animaciones de desaparición
        ObjectAnimator fadeOutArrow = ObjectAnimator.ofFloat(arrow, "alpha", 1f, 0f);
        ObjectAnimator fadeOutPulse = ObjectAnimator.ofFloat(pulse, "alpha", 1f, 0f);
        ObjectAnimator fadeOutText = ObjectAnimator.ofFloat(text, "alpha", 1f, 0f);

        AnimatorSet disappearSet = new AnimatorSet();
        disappearSet.playTogether(fadeOutArrow, fadeOutPulse, fadeOutText);
        disappearSet.setDuration(duration);
        disappearSet.setStartDelay(duration);

        AnimatorSet fullSet = new AnimatorSet();
        fullSet.playSequentially(appearSet, disappearSet);
        fullSet.start();
    }

    // Método para mostrar el botón de salida al final
    private void showExitButton(long startDelay) {
        guideFinalBinding.btnExitGuideFinal.setAlpha(0f);
        guideFinalBinding.btnExitGuideFinal.setVisibility(View.VISIBLE);

        ObjectAnimator fadeInButton = ObjectAnimator.ofFloat(guideFinalBinding.btnExitGuideFinal, "alpha", 0f, 1f);
        fadeInButton.setDuration(1000);
        fadeInButton.setStartDelay(startDelay);
        fadeInButton.start();
    }


    /**
     * Sets the default action bar using a MaterialToolbar.
     * Configures the AppBarLayout to disable lift-on-scroll behavior.
     */
    private void setDefaultActionBar() {
        MaterialToolbar toolbar = findViewById(R.id.main_appbar);
        setSupportActionBar(toolbar);
        AppBarLayout appBarLayout = findViewById(R.id.main_appbar_layout);
        appBarLayout.setLiftOnScroll(false);
    }


    private void selectedBottomMenuById(int menuItemId) {
        NavOptions navOptions = new NavOptions.Builder()
                .setEnterAnim(R.anim.nav_slide_left)
                .setExitAnim(R.anim.nav_slide_right)
                .setPopEnterAnim(R.anim.nav_slide_left)
                .setPopExitAnim(R.anim.nav_slide_right)
                .build();

        if (menuItemId == R.id.nav_collectibles)
            navController.navigate(R.id.navigation_collectibles, null, navOptions);
        else if (menuItemId == R.id.nav_worlds)
            navController.navigate(R.id.navigation_worlds, null, navOptions);
        else
            navController.navigate(R.id.navigation_characters, null, navOptions);

    }

    private boolean selectedBottomMenu(@NonNull MenuItem menuItem) {
        int destinationId = menuItem.getItemId();
        NavOptions navOptions = new NavOptions.Builder()
                .setEnterAnim(R.anim.nav_slide_left)
                .setExitAnim(R.anim.nav_slide_right)
                .setPopEnterAnim(R.anim.nav_slide_left)
                .setPopExitAnim(R.anim.nav_slide_right)
                .build();

        if (navController.getCurrentDestination() != null &&
                navController.getCurrentDestination().getId() == destinationId) {
            return true;  // Evita recargar el mismo fragmento
        }

        if (destinationId == R.id.nav_characters)
            navController.navigate(R.id.navigation_characters, null, navOptions);
        else if (destinationId == R.id.nav_worlds)
            navController.navigate(R.id.navigation_worlds, null, navOptions);
        else
            navController.navigate(R.id.navigation_collectibles, null, navOptions);
        return true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Infla el menú
        getMenuInflater().inflate(R.menu.about_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Gestiona el clic en el ítem de información
        if (item.getItemId() == R.id.action_info) {
            showInfoDialog();  // Muestra el diálogo
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showInfoDialog() {
        // Crear un diálogo de información
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_about)
                .setMessage(R.string.text_about)
                .setPositiveButton(R.string.accept, null)
                .show();
    }


}