package dev.affan.sinergi.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity.CENTER
import android.widget.TextView


class CircleTextView : TextView {
    private var strokeWdth: Float = 0f
    private var strokeClr: Int = 0
    private var solidClr: Int = 0

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        setStrokeWidth(1)
        setSolidColor("#0083B0")
        setStrokeColor("#0F2027")
        gravity = CENTER
    }

    override fun draw(canvas: Canvas?) {
        val circlePaint = Paint()
        circlePaint.color = solidClr
        circlePaint.flags = Paint.ANTI_ALIAS_FLAG
        val strokePaint = Paint()
        strokePaint.color = strokeClr
        strokePaint.flags = Paint.ANTI_ALIAS_FLAG
        val h = this.height
        val w = this.width
        val diameter = if (h > w) h else w
        val radius = diameter / 2.0f
        this.height = diameter
        this.width = diameter
        canvas?.drawCircle(diameter / 2.0f, diameter / 2.0f, radius, strokePaint)
        canvas?.drawCircle(diameter / 2.0f, diameter / 2.0f, radius - strokeWdth, circlePaint)
        super.draw(canvas)
    }

    private fun setStrokeWidth(dp: Int) {
        val scale = context.resources.displayMetrics.density
        strokeWdth = dp * scale
    }

    private fun setStrokeColor(color: String) {
        strokeClr = Color.parseColor(color)
    }

    private fun setSolidColor(color: String) {
        solidClr = Color.parseColor(color)
    }
}