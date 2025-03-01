package dam.pmdm.spyrothedragon;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import dam.pmdm.spyrothedragon.databinding.ActivityMainBinding;
import dam.pmdm.spyrothedragon.databinding.GuideCharactersBinding;
import dam.pmdm.spyrothedragon.databinding.GuideCollectiblesBinding;
import dam.pmdm.spyrothedragon.databinding.GuideFinalBinding;
import dam.pmdm.spyrothedragon.databinding.GuideInfoIconBinding;
import dam.pmdm.spyrothedragon.databinding.GuideWelcomeBinding;
import dam.pmdm.spyrothedragon.databinding.GuideWorldsBinding;

public class MainActivity extends AppCompatActivity {

    private NavController navController = null;
    private MenuItem charactersMenuItem = null;
    private MenuItem worldsMenuItem = null;
    private MenuItem collectiblesMenuItem = null;
    private ActivityMainBinding binding;
    private GuideWelcomeBinding guideBinding;
    private GuideCharactersBinding guideCharactersBinding;
    private GuideWorldsBinding guideWorldsBinding;
    private GuideCollectiblesBinding guideCollectiblesBinding;
    private GuideInfoIconBinding guideInfoIconBinding;
    private GuideFinalBinding guideFinalBinding;
    private ArrayList<float[]> menuItemsInfo = new ArrayList<>();
    private boolean reproducingGuide = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setBindings();
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

        getToolbarInfoItemSize();
        getBottomNavigationViewInfo();

        MyApp app = (MyApp) getApplication();
        boolean showGuide = app.getSavedNeedGuide();
        if (showGuide)
            initializeGuide();
    }

    private void setBindings() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        guideBinding = binding.includeLayoutWelcome;
        guideCharactersBinding = binding.includeLayoutCharacters;
        guideWorldsBinding = binding.includeLayoutWorlds;
        guideCollectiblesBinding = binding.includeLayoutCollectibles;
        guideInfoIconBinding = binding.includeLayoutInfoIcon;
        guideFinalBinding = binding.includeLayoutFinal;
    }

    private void setGuideOnClickListeners() {
        guideBinding.btnStartGuide.setOnClickListener(view -> {
            guideBinding.guideWelcomeLayout.setVisibility(View.GONE);
            onStartGuide(); // Inicia la guía
        });
        guideBinding.btnExitGuide.setOnClickListener(this::onExitGuide);
    }

    private void setCharactersGuideOnClickListeners() {
        guideCharactersBinding.btnExitGuideCharacters.setOnClickListener(this::onExitGuide);
        guideCharactersBinding.btnNextGuideCharacters.setOnClickListener(view -> {
            soundOnClickButtonNext(); // Reproduce el sonido
            guideCharactersBinding.guideCharactersLayout.setVisibility(View.GONE);
            goToWorlds();
        });
    }

    private void setWorldsGuideOnClickListeners() {
        guideWorldsBinding.btnExitGuideWorlds.setOnClickListener(this::onExitGuide);
        guideWorldsBinding.btnNextGuideWorlds.setOnClickListener(v -> {
            soundOnClickButtonNext(); // Reproduce el sonido
            guideWorldsBinding.guideWorldsLayout.setVisibility(View.GONE);
            goToCollectibles();
        });
    }

    private void setCollectiblesGuideClickListeners() {
        guideCollectiblesBinding.btnExitGuideCollectibles.setOnClickListener(this::onExitGuide);
        guideCollectiblesBinding.btnNextGuideCollectibles.setOnClickListener(v -> {
            soundOnClickButtonNext(); // Reproduce el sonido
            guideCollectiblesBinding.guideCollectiblesLayout.setVisibility(View.GONE);
            goToInfoIcon();
        });
    }

    private void setInfoIconGuideClickListeners() {
        guideInfoIconBinding.btnExitGuideInfoIcon.setOnClickListener(this::onExitGuide);
    }

    private void setFinalGuideOnClickListeners() {
        guideFinalBinding.btnExitGuideFinal.setOnClickListener(this::onExitGuide);
    }

    private void getToolbarInfoItemSize() {
        MaterialToolbar mToolbar = findViewById(R.id.main_appbar);

        // Usamos post para asegurarnos de que las vistas estén completamente renderizadas antes de obtener el ancho
        mToolbar.post(() -> {
            Menu mToolbarMenu = mToolbar.getMenu();
            MenuItem menuItem = mToolbarMenu.findItem(R.id.action_info);

            if (menuItem != null) {
                View infoItem = mToolbar.findViewById(menuItem.getItemId());

                if (infoItem != null) {
                    float itemWidth = infoItem.getWidth();
                    int[] location = new int[2];
                    infoItem.getLocationOnScreen(location);
                    float itemX = location[0];
                    float itemY = infoItem.getY();
                    float[] itemInfo = {0, itemWidth, itemX, itemY};

                    menuItemsInfo.add(itemInfo);
                }
            }
        });
    }

    private void getBottomNavigationViewInfo() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.navView);
        getBottomNavMenuItemsSize(bottomNavigationView);
        findMenuItems(bottomNavigationView);
    }

    private void findMenuItems(BottomNavigationView bottomNavigationView) {
        Menu bottomNavigationMenu = bottomNavigationView.getMenu();
        charactersMenuItem = bottomNavigationMenu.findItem(R.id.nav_characters);
        worldsMenuItem = bottomNavigationMenu.findItem(R.id.nav_worlds);
        collectiblesMenuItem = bottomNavigationMenu.findItem(R.id.nav_collectibles);
    }

    private void getBottomNavMenuItemsSize(BottomNavigationView bottomNavigationView) {
        Menu bottomNavigationMenu = bottomNavigationView.getMenu();
        int menuItemsNumber = bottomNavigationMenu.size();

        // Usamos post para asegurarnos de que las vistas estén completamente renderizadas antes de obtener el ancho
        bottomNavigationView.post(() -> {
            for (int i = 0; i < menuItemsNumber; i++) {
                ViewGroup menuView = (ViewGroup) bottomNavigationView.getChildAt(0);
                View itemView = menuView.getChildAt(i);

                float totalWidth = bottomNavigationView.getWidth();
                float itemWidth = itemView.getWidth();
                float sidePadding = (totalWidth - (itemWidth * menuItemsNumber)) / 2f;

                float itemX = itemView.getX();
                float itemY = itemView.getY();
                float[] itemInfo = {sidePadding, itemWidth, itemX, itemY};

                menuItemsInfo.add(itemInfo);
            }
        });
    }


    private void initializeGuide() {
        setGuideOnClickListeners();
        reproducingGuide = true;
        guideBinding.guideWelcomeLayout.setVisibility(View.VISIBLE);
    }

    private void onStartGuide() {
        soundOnClickButtonStart(); // Reproduce el sonido
        goToCharacters();
    }

    private void soundOnClickButtonStart() {
        startSound(R.raw.dragon_get);
    }

    private void soundOnClickButtonNext() {
        startSound(R.raw.gem_chest);
    }

    private void soundOnClickButtonFinish() {
        startSound(R.raw.flameout);
    }

    private void startSound(int sound) {
        // Reproducir sonido al pulsar el botón de inicio
        MediaPlayer mediaPlayer = MediaPlayer.create(this, sound);
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
        soundOnClickButtonFinish();

        MyApp app = (MyApp) getApplication();
        app.saveNeedGuide(false);

        guideBinding.guideWelcomeLayout.setVisibility(View.GONE);
        guideCharactersBinding.guideCharactersLayout.setVisibility(View.GONE);
        guideWorldsBinding.guideWorldsLayout.setVisibility(View.GONE);
        guideCollectiblesBinding.guideCollectiblesLayout.setVisibility(View.GONE);
        guideInfoIconBinding.guideInfoIconLayout.setVisibility(View.GONE);
        guideFinalBinding.guideFinalLayout.setVisibility(View.GONE);

        selectedBottomMenu(charactersMenuItem);
        reproducingGuide = false;
    }

    private AnimatorSet getPulseAnimatorSet(int duration, ObjectAnimator scaleX, ObjectAnimator scaleY) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(scaleX).with(scaleY);
        animatorSet.setDuration(duration);
        return animatorSet;
    }

    private void startPulseAnimation(View view) {
        float valueStart = 1f;
        float valueEnd = 0.5f;
        ObjectAnimator scaleX = getScaleXObjectAnimator(view, valueStart, valueEnd);
        ObjectAnimator scaleY = getScaleYObjectAnimator(view, valueStart, valueEnd);

        scaleX.setRepeatCount(3);
        scaleY.setRepeatCount(3);

        AnimatorSet animatorSet = getPulseAnimatorSet(1000, scaleX, scaleY);

        animatorSet.start();
    }

    private void setItemPosition(View view, int itemWidth, float itemX, float itemY) {
        // Establecer la posición X de la vista antes de la animación
        if (itemWidth != -1) {
            LayoutParams params = view.getLayoutParams();
            params.width = itemWidth;
            view.setLayoutParams(params);
        }
        if (itemX != -1) {
            view.setTranslationX(itemX);
        }
        if (itemY != -1) {
            view.setTranslationY(itemY);
        }
    }

    private float getFireItemMargin(int position, int itemWidth) {
        float margin = 0;
        if (position == 1) {
            margin = -itemWidth + (itemWidth / 4f);
        } else if (position == 2) {
            margin = -itemWidth;
        } else if (position == 3) {
            margin = - itemWidth / 6f;
        }
        return margin;
    }

    private void setMenuPulsePosition(View pulse, View text, View fire, int position) {
        float[] itemInfo = menuItemsInfo.get(position);

        float padding = itemInfo[0];
        int itemWidth = (int) itemInfo[1];
        float itemX = itemInfo[2] + padding;
        float itemY = itemInfo[3];
        float textMargin = getTextItemMargin(position, itemWidth);

        setItemPosition(pulse, itemWidth, itemX, itemY);
        setItemPosition(text, -1, itemX + textMargin, -1);

        // Iniciar la animación cuando ya se ha renderizado la vista en pantalla
        pulse.post(() -> {
            if (fire != null) {
                int textWidth = text.getWidth();
                float fireMargin = getFireItemMargin(position, textWidth);
                setItemPosition(fire, textWidth, itemX + fireMargin, -1);
            }
            startPulseAnimation(pulse);
        });
    }

    private void goToCharacters() {
        guideCharactersBinding.guideCharactersLayout.setVisibility(View.VISIBLE);
        setCharactersGuideOnClickListeners();

        View pulseItem = guideCharactersBinding.pulseImageCharacters;
        View textItem = guideCharactersBinding.textStepCharacters;
        View fireItem = guideCharactersBinding.avFireSelectionCharacters;
        setMenuPulsePosition(pulseItem, textItem, fireItem, 1);

        selectedBottomMenu(charactersMenuItem);
    }

    private void goToWorlds() {
        guideWorldsBinding.guideWorldsLayout.setVisibility(View.VISIBLE);
        setWorldsGuideOnClickListeners();

        View pulseItem = guideWorldsBinding.pulseImageWorlds;
        View textItem = guideWorldsBinding.textStepWorlds;
        View fireItem = guideWorldsBinding.avFireSelectionWorlds;
        setMenuPulsePosition(pulseItem, textItem, fireItem, 2);

        selectedBottomMenu(worldsMenuItem);
    }


    private void goToCollectibles() {
        guideCollectiblesBinding.guideCollectiblesLayout.setVisibility(View.VISIBLE);
        setCollectiblesGuideClickListeners();

        View pulseItem = guideCollectiblesBinding.pulseImageCollectibles;
        View textItem = guideCollectiblesBinding.textStepCollectibles;
        View fireItem = guideCollectiblesBinding.avFireSelectionCollectibles;
        setMenuPulsePosition(pulseItem, textItem, fireItem, 3);

        selectedBottomMenu(collectiblesMenuItem);
    }

    private void goToInfoIcon() {
        guideInfoIconBinding.guideInfoIconLayout.setVisibility(View.VISIBLE);
        setInfoIconGuideClickListeners();

        View pulseInfoIcon = guideInfoIconBinding.pulseImageInfoIcon;
        View textInfo = guideInfoIconBinding.textStepInfoIcon;
        setMenuPulsePosition(pulseInfoIcon, textInfo, null, 0);

        new Handler(Looper.getMainLooper()).postDelayed(this::showInfoDialog, 500);
    }

    private void goToFinal() {
        guideFinalBinding.btnExitGuideFinal.setVisibility(View.INVISIBLE);
        guideFinalBinding.guideFinalLayout.setVisibility(View.VISIBLE);
        setFinalGuideOnClickListeners();

        View arrow = guideFinalBinding.avArrowSelectionFinalCharacters;
        View pulse = guideFinalBinding.pulseImageFinalCharacters;
        View text = guideFinalBinding.textStepFinalCharacters;
        long delay = 0; // Empezamos sin retraso

        setAnimationSetPositions(1, arrow, pulse, text);
        animateSection(arrow, pulse, text, delay);

        arrow = guideFinalBinding.avArrowSelectionFinalWorlds;
        pulse = guideFinalBinding.pulseImageFinalWorlds;
        text = guideFinalBinding.textStepFinalWorlds;
        delay = 4000;

        setAnimationSetPositions(2, arrow, pulse, text);
        animateSection(arrow, pulse, text, delay);

        arrow = guideFinalBinding.avArrowSelectionFinalCollectibles;
        pulse = guideFinalBinding.pulseImageFinalCollectibles;
        text = guideFinalBinding.textStepFinalCollectibles;
        delay = 8000;

        setAnimationSetPositions(3, arrow, pulse, text);
        animateSection(arrow, pulse, text, delay);

        arrow = guideFinalBinding.avArrowSelectionFinalInfoIcon;
        pulse = guideFinalBinding.pulseImageFinalInfoIcon;
        text = guideFinalBinding.textStepFinalInfoIcon;
        delay = 12000;

        setAnimationSetPositions(0, arrow, pulse, text);
        animateSection(arrow, pulse, text, delay);

        // Mostrar botón de salida al final
        delay = 16000;
        showExitButton(delay);
    }

    private void setItemAnimation(View view, float alfa, float scaleX, float scaleY) {
        view.setAlpha(alfa);
        view.setScaleX(scaleX);
        view.setScaleY(scaleY);
    }

    private ObjectAnimator getFadeObjectAnimator(View view, float valueStart, float valueEnd) {
        return ObjectAnimator.ofFloat(view, "alpha", valueStart, valueEnd);
    }

    private ObjectAnimator getScaleXObjectAnimator(View view, float valueStart, float valueEnd) {
        return ObjectAnimator.ofFloat(view, "scaleX", valueStart, valueEnd);
    }

    private ObjectAnimator getScaleYObjectAnimator(View view, float valueStart, float valueEnd) {
        return ObjectAnimator.ofFloat(view, "scaleY", valueStart, valueEnd);
    }

    /**
     * Generar animaciones de aparición
     *
     * @param arrow
     * @param pulse
     * @param text
     * @param duration
     * @param startDelay
     * @return AnimatorSet
     */
    private AnimatorSet getAppearSet(View arrow, View pulse, View text, long duration, long startDelay) {
        AnimatorSet appearSet = new AnimatorSet();

        float alfa = 0f;
        float scaleX = 0.5f;
        float scaleY = 0.5f;
        setItemAnimation(arrow, alfa, scaleX, scaleY);
        setItemAnimation(pulse, alfa, scaleX, scaleY);
        setItemAnimation(text, alfa, scaleX, scaleY);

        float valueStart = 0f;
        float valueEnd = 1f;
        ObjectAnimator fadeInArrow = getFadeObjectAnimator(arrow, valueStart, valueEnd);
        ObjectAnimator fadeInPulse = getFadeObjectAnimator(pulse, valueStart, valueEnd);
        ObjectAnimator fadeInText = getFadeObjectAnimator(text, valueStart, valueEnd);

        valueStart = 0.5f;
        ObjectAnimator scaleUpArrowX = getScaleXObjectAnimator(arrow, valueStart, valueEnd);
        ObjectAnimator scaleUpPulseX = getScaleXObjectAnimator(pulse, valueStart, valueEnd);
        ObjectAnimator scaleUpTextX = getScaleXObjectAnimator(text, valueStart, valueEnd);

        ObjectAnimator scaleUpArrowY = getScaleYObjectAnimator(arrow, valueStart, valueEnd);
        ObjectAnimator scaleUpPulseY = getScaleYObjectAnimator(pulse, valueStart, valueEnd);
        ObjectAnimator scaleUpTextY = getScaleYObjectAnimator(text, valueStart, valueEnd);

        appearSet.playTogether(
                fadeInArrow, fadeInPulse, fadeInText,
                scaleUpArrowX, scaleUpArrowY,
                scaleUpPulseX, scaleUpPulseY,
                scaleUpTextX, scaleUpTextY
        );
        appearSet.setDuration(duration);
        appearSet.setStartDelay(startDelay);

        return appearSet;
    }

    /**
     * Generar animaciones de desaparición
     *
     * @param arrow
     * @param pulse
     * @param text
     * @param duration
     * @return AnimatorSet
     */
    private AnimatorSet getDisappearSet(View arrow, View pulse, View text, long duration) {
        AnimatorSet disappearSet = new AnimatorSet();

        ObjectAnimator fadeOutArrow = getFadeObjectAnimator(arrow, 1f, 0f);
        ObjectAnimator fadeOutPulse = getFadeObjectAnimator(pulse, 1f, 0f);
        ObjectAnimator fadeOutText = getFadeObjectAnimator(text, 1f, 0f);

        disappearSet.playTogether(fadeOutArrow, fadeOutPulse, fadeOutText);
        disappearSet.setDuration(duration);
        disappearSet.setStartDelay(duration);

        return disappearSet;
    }

    private float getTextItemMargin(int position, int itemWidth) {
        float margin = 0;
        if (position == 1) {
            margin = -(itemWidth / 2f);
        } else if (position == 2) {
            margin = -(itemWidth / 2f) - (itemWidth / 4f);
        } else if (position == 3) {
            margin = (itemWidth / 2f);
        }
        return margin;
    }

    private void setAnimationSetPositions(int position, View arrow, View pulse, View text) {
        float[] itemInfo = menuItemsInfo.get(position);

        float padding = itemInfo[0];
        int itemWidth = (int) itemInfo[1];
        float itemX = itemInfo[2] + padding;
        float itemY = itemInfo[3];
        float textMargin = getTextItemMargin(position, itemWidth);

        if (position == 0) {
            itemY = itemInfo[3];
        }

        setItemPosition(arrow, itemWidth, itemX, -1);
        setItemPosition(pulse, itemWidth, itemX, itemY);
        setItemPosition(text, -1, itemX + textMargin, -1);
    }

    private void animateSection(View arrow, View pulse, View text, long startDelay) {
        long duration = 2000; // Duración de animaciones

        AnimatorSet appearSet = getAppearSet(arrow, pulse, text, duration, startDelay);
        AnimatorSet disappearSet = getDisappearSet(arrow, pulse, text, duration);

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

    private boolean selectedBottomMenu(@NonNull MenuItem menuItem) {
        int destinationId = menuItem.getItemId();
        NavDestination currentDestination = navController.getCurrentDestination();

        if (currentDestination != null && currentDestination.getId() == destinationId) {
            return true;  // Evita recargar el mismo fragmento
        }

        NavOptions navOptions = new NavOptions.Builder()
                .setEnterAnim(R.anim.nav_slide_left)
                .setExitAnim(R.anim.nav_slide_right)
                .setPopEnterAnim(R.anim.nav_slide_left)
                .setPopExitAnim(R.anim.nav_slide_right)
                .build();

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

    private void handleDialogDismiss(AlertDialog dialog) {
        soundOnClickButtonNext(); // Reproduce el sonido
        guideInfoIconBinding.guideInfoIconLayout.setVisibility(View.GONE);
        goToFinal();
        dialog.dismiss();
    }

    private void showInfoDialog() {
        // Crear un diálogo de información
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.title_about)
                .setMessage(R.string.text_about)
                .setPositiveButton(R.string.accept, null);
        AlertDialog infoDialog = builder.create();
        infoDialog.show();

        if (reproducingGuide) {
            infoDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(v -> {
                handleDialogDismiss(infoDialog);
            });
            infoDialog.setOnDismissListener(dialog -> {
                handleDialogDismiss(infoDialog);
            });
        }
    }

}