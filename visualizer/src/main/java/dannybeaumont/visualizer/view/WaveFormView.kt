package dannybeaumont.visualizer.view

import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.util.AttributeSet
import android.view.View
import dannybeaumont.visualizer.R
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin

class WaveFormView : View {
    private var phase = 0f
    private var amplitude = 0f
    private var frequency = 0f
    private var idleAmplitude = 0f
    private var numberOfWaves = 0f
    private var phaseShift = 0f
    private var density = 0f
    private var primaryWaveLineWidth = 0f
    private var secondaryWaveLineWidth = 0f
    private var mPaintColor: Paint? = null
    private var mSecondaryPaint: Paint? = null
    private var mThirdPaint: Paint? = null

    constructor(context: Context?) : super(context) {
        setUp()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        val attributes = context?.obtainStyledAttributes(attrs, R.styleable.WaveFormView)
        setUp(attributes!!)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val attributes = context?.obtainStyledAttributes(attrs, R.styleable.WaveFormView,defStyleAttr,0)
        setUp(attributes!!)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        val attributes = context?.obtainStyledAttributes(attrs, R.styleable.WaveFormView,defStyleAttr,defStyleRes)
        setUp(attributes!!)
    }

    private fun setUp(values :TypedArray) {
        this.frequency = values.getFloat(R.styleable.WaveFormView_frequency,DEFAULT_FREQUENCY)
        this.amplitude = values.getFloat(R.styleable.WaveFormView_amplitude,DEFAULT_AMPLITUDE)
        this.idleAmplitude = values.getFloat(R.styleable.WaveFormView_idle_amplitude,DEFAULT_IDLE_AMPLITUDE)
        this.numberOfWaves = values.getFloat(R.styleable.WaveFormView_number_of_waves,DEFAULT_NUMBER_OF_WAVES)
        this.phaseShift = values.getFloat(R.styleable.WaveFormView_phase_shift,DEFAULT_PHASE_SHIFT)
        this.primaryWaveLineWidth = values.getFloat(R.styleable.WaveFormView_primary_line_width,DEFAULT_PRIMARY_LINE_WIDTH)
        this.secondaryWaveLineWidth = values.getFloat(R.styleable.WaveFormView_secondary_line_width,DEFAULT_SECONDARY_LINE_WIDTH)
        this.density = values.getFloat(R.styleable.WaveFormView_density,DEFAULT_DENSITY)
        mPaintColor = Paint()
        mPaintColor!!.color = values.getColor(R.styleable.WaveFormView_primary_color,Color.BLUE)
        mPaintColor!!.strokeWidth = primaryWaveLineWidth
        mSecondaryPaint = Paint()
        mSecondaryPaint!!.color = values.getColor(R.styleable.WaveFormView_secondary_color, Color.BLACK)
        mSecondaryPaint!!.strokeWidth = secondaryWaveLineWidth
        mThirdPaint = Paint()
        mThirdPaint!!.color = values.getColor(R.styleable.WaveFormView_tertiary_color,Color.YELLOW)
        mThirdPaint!!.strokeWidth = secondaryWaveLineWidth
    }

    private fun setUp() {
        this.frequency = DEFAULT_FREQUENCY

        this.amplitude = DEFAULT_AMPLITUDE
        this.idleAmplitude = DEFAULT_IDLE_AMPLITUDE

        this.numberOfWaves = DEFAULT_NUMBER_OF_WAVES
        this.phaseShift = DEFAULT_PHASE_SHIFT
        this.density = DEFAULT_DENSITY

        this.primaryWaveLineWidth = DEFAULT_PRIMARY_LINE_WIDTH
        this.secondaryWaveLineWidth = DEFAULT_SECONDARY_LINE_WIDTH
        mPaintColor = Paint()
        mPaintColor!!.color = Color.BLUE
        mPaintColor!!.strokeWidth = primaryWaveLineWidth
        mSecondaryPaint = Paint()
        mSecondaryPaint!!.color = Color.BLACK
        mSecondaryPaint!!.strokeWidth = secondaryWaveLineWidth
        mThirdPaint = Paint()
        mThirdPaint!!.color = Color.YELLOW
        mThirdPaint!!.strokeWidth = secondaryWaveLineWidth
    }

    fun updateAmplitude(amplitude: Float) {
        this.amplitude = max(amplitude.toDouble(), idleAmplitude.toDouble()).toFloat()
    }


    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.TRANSPARENT)
        var i = 0
        while (i < numberOfWaves) {
            val paint = getPaint(i)
            val halfHeight = (canvas.height / 2).toFloat()
            val width = canvas.width.toFloat()
            val mid = (width / 2)

            val maxAmplitude = halfHeight - 4.0f
            val progress = 1.0f - i.toFloat() / this.numberOfWaves
            val normedAmplitude = (1.5f * progress - 0.5f) * this.amplitude
            val path = path

            val multiplier = min(
                1.0,
                ((progress / 3.0f * 2.0f) + (1.0f / 3.0f)).toDouble()
            ).toFloat()

            var x = 0f
            while (x < width + density) {
                // We use a parable to scale the sine wave, that has its peak in the middle of the view.
                val scaling: Float = (-(1 / mid * (x - mid)).pow(2f) + 1)

                val y =
                    (scaling * maxAmplitude * normedAmplitude * sin(2 * Math.PI * (x / width) * multiplier * frequency + phase) + halfHeight).toFloat()

                if (x == 0f) {
                    path.moveTo(x, y)
                } else {
                    path.lineTo(x, y)
                }
                x += density
            }
            paint!!.style = Paint.Style.STROKE
            paint.isAntiAlias = true
            canvas.drawPath(path, paint)

            i++
        }
        this.phase += phaseShift
        invalidate()
    }

    private fun getPaint(i: Int): Paint? {
        if (i == 0) return mPaintColor
        if (i % 2 == 0) return mSecondaryPaint
        return mThirdPaint
    }

    private val path: Path
        get() = Path()

    companion object {
        private const val DEFAULT_FREQUENCY = 1.5f
        private const val DEFAULT_AMPLITUDE = 1.0f
        private const val DEFAULT_IDLE_AMPLITUDE = 0.01f
        private const val DEFAULT_NUMBER_OF_WAVES = 20.0f
        private const val DEFAULT_PHASE_SHIFT = -0.15f
        private const val DEFAULT_DENSITY = 5.0f
        private const val DEFAULT_PRIMARY_LINE_WIDTH = 3.0f
        private const val DEFAULT_SECONDARY_LINE_WIDTH = 1.0f
    }
}