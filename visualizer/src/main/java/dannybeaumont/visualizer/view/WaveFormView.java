package dannybeaumont.visualizer.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

public class WaveFormView extends View {

    private static final float DEFAULT_FREQUENCY = 1.5f;
    private static final float DEFAULT_AMPLITUDE = 1.0f;
    private static final float DEFAULT_IDLE_AMPLITUDE = 0.01f;
    private static final float DEFAULT_NUMBER_OF_WAVES = 20.0f;
    private static final float DEFAULT_PHASE_SHIFT = -0.15f;
    private static final float DEFAULT_DENSITY = 5.0f;
    private static final float DEFAULT_PRIMARY_LINE_WIDTH = 3.0f;
    private static final float DEFAULT_SECONDARY_LINE_WIDTH = 1.0f;

    private float phase;
    private float amplitude;
    private float frequency;
    private float idleAmplitude;
    private float numberOfWaves;
    private float phaseShift;
    private float density;
    private float primaryWaveLineWidth;
    private float secondaryWaveLineWidth;
    private Paint mPaintColor;
    private Paint mSecondaryPaint;
    private Paint mThirdPaint;

    public WaveFormView(Context context) {
        super(context);
        setUp();
    }

    public WaveFormView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp();
    }

    public WaveFormView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUp();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WaveFormView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setUp();
    }

    private void setUp() {
        this.frequency = DEFAULT_FREQUENCY;

        this.amplitude = DEFAULT_AMPLITUDE;
        this.idleAmplitude = DEFAULT_IDLE_AMPLITUDE;

        this.numberOfWaves = DEFAULT_NUMBER_OF_WAVES;
        this.phaseShift = DEFAULT_PHASE_SHIFT;
        this.density = DEFAULT_DENSITY;

        this.primaryWaveLineWidth = DEFAULT_PRIMARY_LINE_WIDTH;
        this.secondaryWaveLineWidth = DEFAULT_SECONDARY_LINE_WIDTH;
        mPaintColor = new Paint();
        mPaintColor.setColor(Color.BLUE);
        mPaintColor.setStrokeWidth(primaryWaveLineWidth);
        mSecondaryPaint = new Paint();
        mSecondaryPaint.setColor(Color.BLACK);
        mSecondaryPaint.setStrokeWidth(secondaryWaveLineWidth);
        mThirdPaint = new Paint();
        mThirdPaint.setColor(Color.YELLOW);
        mThirdPaint.setStrokeWidth(secondaryWaveLineWidth);
    }

    public void updateAmplitude(float ampli) {
        this.amplitude = Math.max(ampli, idleAmplitude);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        for (int i = 0; i < numberOfWaves; i++) {
            Paint paint = getPaint(i);
            float halfHeight = canvas.getHeight() / 2;
            float width = canvas.getWidth();
            float mid = canvas.getWidth() / 2;

            float maxAmplitude = halfHeight - 4.0f;
            float progress = 1.0f - (float) i / this.numberOfWaves;
            float normedAmplitude = (1.5f * progress - 0.5f) * this.amplitude;
            Path path = getPath();

            float multiplier = Math.min(1.0f, (progress / 3.0f * 2.0f) + (1.0f / 3.0f));

            for (float x = 0; x < width + density; x += density) {
                // We use a parable to scale the sine wave, that has its peak in the middle of the view.
                float scaling = (float) (-Math.pow(1 / mid * (x - mid), 2) + 1);

                float y = (float) (scaling * maxAmplitude * normedAmplitude * Math.sin(2 * Math.PI * (x / width) * multiplier * frequency + phase) + halfHeight);

                if (x == 0) {
                    path.moveTo(x, y);
                } else {
                    path.lineTo(x, y);
                }
            }
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            canvas.drawPath(path, paint);

        }
        this.phase += phaseShift;
        invalidate();
    }

    private Paint getPaint(int i) {
        if (i == 0)
            return mPaintColor;
        if (i%2 == 0)
            return mSecondaryPaint;
        return mThirdPaint;
    }

    @NonNull
    private Path getPath() {
        return new Path();
    }
}