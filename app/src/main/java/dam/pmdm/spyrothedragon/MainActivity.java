package dam.pmdm.spyrothedragon;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
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
    private Boolean needToStartGuide = true;

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
                // TODO quitar la flecha del navigation icon
            }
        });

        initializeGuide();

    }

    private void initializeGuide() {
        guideBinding.btnExitGuide.setOnClickListener(this::onExitGuide);
        guideBinding.btnStartGuide.setOnClickListener(this::onStartGuide);
        guideBinding.guideWelcomeLayout.setVisibility(View.VISIBLE);

    }

    private void onStartGuide(View view) {
        guideBinding.guideWelcomeLayout.setVisibility(View.GONE);
        guideCharactersBinding.guideCharactersLayout.setVisibility(View.VISIBLE);

        if (needToStartGuide) {
            goToCharacters();

        }
    }

    private void onExitGuide(View view) {
        needToStartGuide = false;
        //TODO GUARDAR EN SHARED PREFERENCES
        //binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        guideBinding.guideWelcomeLayout.setVisibility(View.GONE);
        guideCharactersBinding.guideCharactersLayout.setVisibility(View.GONE);
        guideWorldsBinding.guideWorldsLayout.setVisibility(View.GONE);
        guideCollectiblesBinding.guideCollectiblesLayout.setVisibility(View.GONE);
        //binding.drawerLayout.close();

    }

    private void goToCharacters() {
        guideCharactersBinding.btnExitGuideCharacters.setOnClickListener(this::onExitGuide);
        guideCharactersBinding.btnNextGuideCharacters.setOnClickListener(this::goToWorlds);
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
        guideWorldsBinding.btnExitGuideWorlds.setOnClickListener(this::onExitGuide);
        guideWorldsBinding.btnNextGuideWorlds.setOnClickListener(this::goToCollectibles);
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
        guideCollectiblesBinding.btnExitGuideCollectibles.setOnClickListener(this::onExitGuide);
        guideCollectiblesBinding.btnNextGuideCollectibles.setOnClickListener(this::goToInfoIcon);
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
        guideInfoIconBinding.btnExitGuideInfoIcon.setOnClickListener(this::onExitGuide);
        guideInfoIconBinding.btnNextGuideInfoIcon.setOnClickListener(this::goToFinal);
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
        guideFinalBinding.btnExitGuideFinal.setOnClickListener(this::onExitGuide);
        guideFinalBinding.guideFinalLayout.setVisibility(View.VISIBLE);

        animateSection(
                guideFinalBinding.avArrowSelectionCharacterFinal,
                guideFinalBinding.pulseImageCharactersFinal,
                guideFinalBinding.textStepCharactersFinal,
                0 // Delay inicial
        );

        animateSection(
                guideFinalBinding.avArrowSelectionWorldsFinal,
                guideFinalBinding.pulseImageWorldsFinal,
                guideFinalBinding.textStepWorldsFinal,
                3000 // 3 segundo después
        );

        animateSection(
                guideFinalBinding.avArrowSelectionCollectiblesFinal,
                guideFinalBinding.pulseImageCollectiblesFinal,
                guideFinalBinding.textStepCollectiblesFinal,
                6000 // 6 segundos después
        );

        animateSection(
                guideFinalBinding.avArrowSelectionInfoIconFinal,
                guideFinalBinding.pulseImageInfoIconFinal,
                guideFinalBinding.textStepInfoIconFinal,
                9000 // 9 segundos después
        );
    }

    private void animateSection(View arrow, View pulse, View text, long startDelay) {
        arrow.setAlpha(0f);
        pulse.setAlpha(0f);
        text.setAlpha(0f);

        arrow.setScaleX(0.5f);
        arrow.setScaleY(0.5f);
        pulse.setScaleX(0.5f);
        pulse.setScaleY(0.5f);
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

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                fadeInArrow, fadeInPulse, fadeInText,
                scaleUpArrowX, scaleUpArrowY,
                scaleUpPulseX, scaleUpPulseY,
                scaleUpTextX, scaleUpTextY
        );
        animatorSet.setDuration(1000);
        animatorSet.setStartDelay(startDelay);
        animatorSet.start();
    }

        //Que vayan apareciendo poco a poco los bocadillos
        ///////Lo puedo hacer con el handler
        //Que aparezca lo último el botón de finalizar



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
        if (menuItemId == R.id.nav_collectibles)
            navController.navigate(R.id.navigation_collectibles);
        else if (menuItemId == R.id.nav_worlds)
            navController.navigate(R.id.navigation_worlds);
        else
            navController.navigate(R.id.navigation_characters);
    }

    private boolean selectedBottomMenu(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_characters)
            navController.navigate(R.id.navigation_characters);
        else if (menuItem.getItemId() == R.id.nav_worlds)
            navController.navigate(R.id.navigation_worlds);
        else
            navController.navigate(R.id.navigation_collectibles);
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