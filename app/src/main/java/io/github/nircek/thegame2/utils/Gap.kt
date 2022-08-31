package io.github.nircek.thegame2.utils

import android.content.Context
import android.util.AttributeSet
import android.view.View
import kotlin.math.min

/** like API14 Space */
class Gap @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    init {
        if (visibility == VISIBLE) visibility = INVISIBLE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(
            getDefaultSize2(suggestedMinimumWidth, widthMeasureSpec),
            getDefaultSize2(suggestedMinimumHeight, heightMeasureSpec)
        )
    }

    companion object {
        internal fun getDefaultSize2(size: Int, measureSpec: Int): Int {
            val specMode = MeasureSpec.getMode(measureSpec)
            val specSize = MeasureSpec.getSize(measureSpec)
            return when (specMode) {
                MeasureSpec.EXACTLY -> specSize
                MeasureSpec.AT_MOST -> min(size, specSize)
                else -> size
            }
        }
    }
}
