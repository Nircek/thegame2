package io.github.nircek.thegame2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class RadarView(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet) {
    enum class ACTION {
        TURN_LEFT, TURN_RIGHT, ACCELERATE, STOP
    }

    private val dp =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics)
    private val path = Path()

    private val pTurn = PI.toFloat()
    private val pPullInc = 30f
    private val pAnimFrames = 3L
    private val pPossColors = 8

    private val wallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = dp
    }
    private var _rotation = 0f
    private var _pull = 30f

    /** make it a square */
    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val squareSize = min(MeasureSpec.getSize(widthSpec), MeasureSpec.getSize(heightSpec))
        val finalSpec = MeasureSpec.makeMeasureSpec(squareSize, MeasureSpec.EXACTLY)
        super.onMeasure(finalSpec, finalSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        path.reset()
        path.addCircle(
            width / 2f,
            height / 2f,
            min(width, height) / 2f - 4 * dp,
            Path.Direction.CCW
        )
        path.moveTo(width / 2f, height / 2f)
        path.lineTo(
            width / 2f - (_pull * sin(_rotation * PI / 180f)).toFloat(),
            height / 2f - (_pull * cos(_rotation * PI / 180f)).toFloat(),
        )
        path.addCircle(width / 2f, 0f, 200f, Path.Direction.CCW)
        canvas?.drawPath(path, wallPaint)
    }

    private fun rotate(offset: Float) {
        RotateAnimation(
            _rotation,
            _rotation + offset,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        ).apply {
            duration = pAnimFrames * 1000 / 60
            interpolator = LinearInterpolator()
            fillAfter = true
        }.also { startAnimation(it) }
        _rotation += offset
    }

    private fun randomColor(n: Int) =
        Color.HSVToColor(floatArrayOf(n * (0 until 360 / n).random().toFloat(), 1f, 1f))

    fun takeAction(action: ACTION) {
        when (action) {
            ACTION.TURN_LEFT -> rotate(-pTurn)
            ACTION.TURN_RIGHT -> rotate(pTurn)
            ACTION.ACCELERATE -> {
                _pull += pPullInc; invalidate()
            }
            ACTION.STOP -> {
                _pull = 0f
                wallPaint.color = randomColor(pPossColors)
                invalidate()
            }
        }
    }
}
