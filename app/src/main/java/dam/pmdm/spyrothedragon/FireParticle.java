package dam.pmdm.spyrothedragon;

import android.graphics.Color;

public class FireParticle {
    float x, y;
    float radius;
    int color;
    float speedY;
    float speedX;
    int age;

    public FireParticle(float startX, float startY) {
        this(startX, startY, 1.1f, 1f, 4f);
    }

    public FireParticle(float startX, float startY, float sizeFactor, float speedFactor, float spreadFactor) {
        x = startX;
        y = startY;

        // Tamaño aleatorio escalado
        radius = (20 + (float) Math.random() * 10) * sizeFactor;

        // Color rojo-naranja
        color = Color.argb(255, 255, (int) (Math.random() * 100), 0);

        // Velocidad hacia abajo
        speedY = ((float) Math.random() * 10 + 5) * speedFactor;

        // Movimiento horizontal controlado por spreadFactor
        speedX = ((float) Math.random() * 4 - 2) * spreadFactor;

        // Inicializar la edad de la partícula
        age = 0;
    }

    public void update() {
        x += speedX;
        y += speedY;
        radius *= 0.98f; // Disminución gradual del tamaño

        if (radius < 1) {
            // Límite mínimo de tamaño
            radius = 1;
        }
        age++;
    }
}
