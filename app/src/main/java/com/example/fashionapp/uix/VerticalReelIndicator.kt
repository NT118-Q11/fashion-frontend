package com.example.fashionapp.uix

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import kotlin.math.max

class VerticalReelIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        alpha = (0.7f * 255).toInt()
        style = Paint.Style.FILL
    }
    private val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        alpha = (0.85f * 255).toInt()
        style = Paint.Style.FILL
    }

    private var dotRadiusPx = dp(2f)
    private var thumbWidthPx = dp(3f)
    private var thumbHeightPx = dp(18f)

    private var total = 1
    private var position = 0
    private var offset = 0f

    // Reuse rect to avoid allocations during draw
    private val rect = RectF()

    fun setTotal(total: Int) {
        this.total = max(1, total)
        invalidate()
    }

    fun onPageScrolled(position: Int, positionOffset: Float) {
        this.position = position
        this.offset = positionOffset
        invalidate()
    }

    fun onPageSelected(position: Int) {
        this.position = position
        this.offset = 0f
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (height <= paddingTop + paddingBottom) return

        val contentTop = paddingTop
        val contentBottom = height - paddingBottom
        val contentLeft = paddingLeft
        val contentRight = width - paddingRight
        val cx = (contentLeft + contentRight) / 2f
        val availableHeight = (contentBottom - contentTop).toFloat()

        val steps = max(1, total - 1)
        val spacing = if (steps == 0) 0f else availableHeight / steps

        // draw dots
        for (i in 0 until total) {
            val y = contentTop + i * spacing
            canvas.drawCircle(cx, y, dotRadiusPx, dotPaint)
        }

        // draw thumb (rounded rect) at interpolated position
        val thumbCenterY = (contentTop + position * spacing + offset * spacing)
        val left = cx - thumbWidthPx / 2f
        val top = thumbCenterY - thumbHeightPx / 2f
        val right = cx + thumbWidthPx / 2f
        val bottom = thumbCenterY + thumbHeightPx / 2f
        rect.set(left, top, right, bottom)
        canvas.drawRoundRect(rect, thumbWidthPx, thumbWidthPx, thumbPaint)
    }

    private fun dp(v: Float): Float = v * resources.displayMetrics.density
}
