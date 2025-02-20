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
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import dam.pmdm.spyrothedragon.databinding.ActivityMainBinding;
import dam.pmdm.spyrothedragon.databinding.GuideCharactersBinding;
import dam.pmdm.spyrothedragon.databinding.GuideWelcomeBinding;

public class MainActivity extends AppCompatActivity {

    NavController navController = null;
    private ActivityMainBinding binding;
    private GuideWelcomeBinding guideBinding;
    private GuideCharactersBinding guideCharactersBinding;
    private Boolean needToStartGuide = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        guideBinding = binding.includeLayoutWelcome;
        guideCharactersBinding = binding.includeLayoutCharacters;
        setContentView(binding.getRoot());

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
                // Para las pantallas de los tabs, no queremos que aparezca la flecha de atrás
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            } else {
                // Si se navega a una pantalla donde se desea mostrar la flecha de atrás, habilítala
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
            //binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            ObjectAnimator scaleX = ObjectAnimator.ofFloat(
                    guideCharactersBinding.avFireSelectionCharacter, "scaleX", 1f, 0.5f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(
                    guideCharactersBinding.avFireSelectionCharacter, "scaleY", 1f, 0.5f);
            ObjectAnimator fadeIn = ObjectAnimator.ofFloat(
                    guideCharactersBinding.textStepCharacters, "alpha", 0f, 1f);

            scaleX.setRepeatCount(3);
            scaleY.setRepeatCount(3);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(scaleX).with(scaleY).before(fadeIn);
            animatorSet.setDuration(1000);
            animatorSet.start();
            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (needToStartGuide) {
                        super.onAnimationEnd(animation);
                        //binding.drawerLayout.open();
                        guideCharactersBinding.avFireSelectionCharacter.setVisibility(View.GONE);
                        guideCharactersBinding.textStepCharacters.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }

    private void onExitGuide(View view) {
        needToStartGuide = false;
        //TODO GUARDAR EN SHARED PREFERENCES
        //binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        guideBinding.guideWelcomeLayout.setVisibility(View.GONE);
        //binding.drawerLayout.close();
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