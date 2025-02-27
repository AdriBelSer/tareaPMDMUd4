package dam.pmdm.spyrothedragon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class FireAnimationView extends View {

    private Bitmap backgroundImage;
    private Paint paint;
    private List<FireParticle> particles;
    private float[] mouthPos;
    private OnAnimationEndListener onAnimationEndListener;

    public FireAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.spyro);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        particles = new ArrayList<>();
    }

    private float[] getMouthPosition() {
        if (backgroundImage == null) {
            return new float[]{0, 0};
        }

        float xMouth = 600;
        float yMouth = 1050;

        // Obtener la transformación de la imagen
        float[] mouthPos = {xMouth, yMouth};

        return mouthPos;
    }

    private Matrix getMatrix(Canvas canvas) {
        if (backgroundImage == null) {
            return null;
        }
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        int imageWidth = backgroundImage.getWidth();
        int imageHeight = backgroundImage.getHeight();

        float scaleFactor = 2f; // Aumentar tamaño de la imagen
        float scale = Math.min((float) canvasWidth / imageWidth, (float) canvasHeight / imageHeight) * scaleFactor;
        float x = (canvasWidth - imageWidth * scale) / 2;
        float y = (canvasHeight - imageHeight * scale) / 2 - 400;

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        matrix.postTranslate(x, y);
        return matrix;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Dibujar la imagen de fondo
        Matrix matrix = getMatrix(canvas);
        if (matrix != null) {
            canvas.drawBitmap(backgroundImage, matrix, null);
        }

        // Obtener la posición de la boca para emitir partículas
        mouthPos = getMouthPosition();

        // Generar nuevas partículas
        int newParticlesCount = 10; // Aumentamos la cantidad de partículas
        for (int i = 0; i < newParticlesCount; i++) {
            particles.add(new FireParticle(mouthPos[0], mouthPos[1]));
        }

        // Dibujar y actualizar partículas
        List<FireParticle> activeParticles = new ArrayList<>();
        for (FireParticle particle : particles) {
            if (particle.radius > 1) { // Filtrar partículas desaparecidas
                paint.setColor(particle.color);

                // Aplicar difuminado al principio de la vida de la partícula
                if (particle.age < 100) {
                    paint.setAlpha((int) (220 * (1 - (particle.age / 100.0))));
                } else {
                    int color = Color.argb(255, 255, (int) (Math.random() * 10), 0);
                    paint.setColor(color);
                    paint.setAlpha(255);
                }

                canvas.drawCircle(particle.x, particle.y, particle.radius, paint);
                particle.update();
                activeParticles.add(particle);
            }
        }
        particles = activeParticles; // Actualizar lista de partículas

        // Si no hay partículas, notificar el fin de la animación
        if (particles.isEmpty() && onAnimationEndListener != null) {
            onAnimationEndListener.onAnimationEnd();
        } else {
            postInvalidateDelayed(30);
        }
    }

    public void startAnimation() {
        // Ejecutar la animación normalmente

        // Usar un Handler para ejecutar una acción después de 15 segundos
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Verifica si el listener no es null y luego dispara el evento
                if (onAnimationEndListener != null) {
                    onAnimationEndListener.onAnimationEnd();
                }
            }
        }, 8000);
    }

    public void setOnAnimationEndListener(OnAnimationEndListener listener) {
        this.onAnimationEndListener = listener;
    }

    public interface OnAnimationEndListener {
        void onAnimationEnd();
    }
}
