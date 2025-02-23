package dam.pmdm.spyrothedragon;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
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
import dam.pmdm.spyrothedragon.databinding.GuideWelcomeBinding;
import dam.pmdm.spyrothedragon.databinding.GuideWorldsBinding;

public class MainActivity extends AppCompatActivity {

    NavController navController = null;
    private ActivityMainBinding binding;
    private GuideWelcomeBinding guideBinding;
    private GuideCharactersBinding guideCharactersBinding;
    private GuideWorldsBinding guideWorldsBinding;
    private GuideCollectiblesBinding guideCollectiblesBinding;
    private Boolean needToStartGuide = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        guideBinding = binding.includeLayoutWelcome;
        guideCharactersBinding = binding.includeLayoutCharacters;
        guideWorldsBinding = binding.includeLayoutWorlds;
        guideCollectiblesBinding = binding.includeLayoutCollectibles;
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
        guideCharactersBinding.btnExitGuideCharacters.setOnClickListener(this::onExitGuide);

        if (needToStartGuide) {

            //TODO PONER TIEMPO QUE PERMANECERÁ EN LA PANTALLA
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
        //Para ir a una de las pantallas del menu
        selectedBottomMenuById(R.id.nav_characters);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(
                guideCharactersBinding.pulseImageCharacters, "scaleX", 1f, 0.5f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(
                guideCharactersBinding.pulseImageCharacters, "scaleY", 1f, 0.5f);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(
                guideCharactersBinding.textStepCharacters, "alpha", 0f, 1f);
        scaleX.setRepeatCount(3);
        scaleY.setRepeatCount(3);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleX).with(scaleY).before(fadeIn);
        animatorSet.setDuration(1000);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (needToStartGuide) {
                    super.onAnimationEnd(animation);
                    guideCharactersBinding.pulseImageCharacters.setVisibility(View.GONE);
                    // goToWorlds();
                }
            }
        });

        animatorSet.start();

    }

    private void goToWorlds() {
        guideWorldsBinding.guideWorldsLayout.setVisibility(View.VISIBLE);
        selectedBottomMenuById(R.id.nav_worlds);


        //goToCollectibles();

    }

    private void goToCollectibles() {
        guideCollectiblesBinding.guideCollectiblesLayout.setVisibility(View.VISIBLE);
        selectedBottomMenuById(R.id.nav_collectibles);

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