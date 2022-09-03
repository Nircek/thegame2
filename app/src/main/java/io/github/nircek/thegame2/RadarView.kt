package io.github.nircek.thegame2

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import kotlin.math.*
import kotlin.random.Random

@Suppress("DEPRECATION")
class RadarView(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet) {
    enum class ACTION {
        TURN_LEFT, TURN_RIGHT, ACCELERATE, STOP
    }

    private val dp =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics)
    private val path = Path()


    private val pTurn = 30.toFloat()
    private val pPush = 0.2f
    private val pRenderDistance = E.toFloat()
    private val pAnimFrames = 7L
    private val pPossColors = 8
    private val pMargin = 3 * dp
    private val pSeed: Int? = null
    private val map = MazeMap(80, 50, if (pSeed == null) Random.Default else Random(pSeed))

    private val curPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = resources.getColor(R.color.colorPrimary)
        style = Paint.Style.STROKE
        strokeWidth = 3 * dp
    }
    private val wallPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = resources.getColor(R.color.colorWalls)
        style = Paint.Style.STROKE
        strokeWidth = dp
    }
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = resources.getColor(R.color.colorBackground)
        style = Paint.Style.FILL_AND_STROKE
    }

    private var _rotation = 0f

    /** make it a square */
    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val squareSize = min(MeasureSpec.getSize(widthSpec), MeasureSpec.getSize(heightSpec))
        val finalSpec = MeasureSpec.makeMeasureSpec(squareSize, MeasureSpec.EXACTLY)
        super.onMeasure(finalSpec, finalSpec)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val side = width.toFloat()
        fun Path.polarLineTo(d: Float, r: Float) = lineTo(
            side / 2f - (r * dp / pRenderDistance * sin((_rotation - d) * PI / 180f)).toFloat(),
            side / 2f - (r * dp / pRenderDistance * cos((_rotation - d) * PI / 180f)).toFloat(),
        )

        path.reset()
        path.moveTo(side / 2, side / 2)
        path.polarLineTo(0f, 50f)
        path.polarLineTo(230f, 20f)
        path.polarLineTo(130f, 20f)
        path.polarLineTo(0f, 50f)
        path.polarLineTo(0f, 0f)
        path.polarLineTo(230f, 20f)
        path.polarLineTo(130f, 20f)
        path.polarLineTo(0f, 0f)
        canvas?.drawPath(path, curPaint)

        path.reset()
        map.drawOn(path, side, pMargin, pRenderDistance)
        canvas?.drawPath(path, wallPaint)

        path.reset()
        path.fillType = Path.FillType.EVEN_ODD
        path.addCircle(side / 2, side / 2, side / 2 - pMargin, Path.Direction.CCW)
        path.close()
        path.addCircle(side / 2, side / 2, side, Path.Direction.CCW)
        path.close()
        canvas?.drawPath(path, bgPaint)
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
            interpolator = AccelerateDecelerateInterpolator()
            fillAfter = true
        }.also { startAnimation(it) }
        _rotation += offset
    }

    private fun randomColor(n: Int) =
        Color.HSVToColor(floatArrayOf(n * (0 until 360 / n).random().toFloat(), 1f, 1f))

    fun takeAction(action: ACTION) {
        when (action) {
            ACTION.TURN_LEFT -> rotate(pTurn)
            ACTION.TURN_RIGHT -> rotate(-pTurn)
            ACTION.ACCELERATE -> {
                map.push(_rotation, pPush)
                invalidate()
            }
            ACTION.STOP -> {
                wallPaint.color = randomColor(pPossColors)
                curPaint.color = randomColor(2 * pPossColors)
                invalidate()
            }
        }
    }
}
