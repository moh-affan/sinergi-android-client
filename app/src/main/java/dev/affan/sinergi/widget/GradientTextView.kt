package dev.affan.sinergi.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet
import android.widget.TextView
import dev.affan.sinergi.R


class GradientTextView : TextView {
    private lateinit var gradient: LinearGradient
    private var startColor: Int = Color.parseColor("#11998e")
    private var endColor: Int = Color.parseColor("#38ef7d")
    private var withShadow = false

    constructor(context: Context) : super(context) {
        setGradient()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        setColors(context.obtainStyledAttributes(attrs, R.styleable.GradientTextView))
        setGradient()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setColors(context.obtainStyledAttributes(attrs, R.styleable.GradientTextView))
        setGradient()
    }

    private fun setColors(a: TypedArray) {
        startColor = a.getColor(R.styleable.GradientTextView_color_start, startColor)
        endColor = a.getColor(R.styleable.GradientTextView_color_end, endColor)
        withShadow = a.getBoolean(R.styleable.GradientTextView_with_shadow, false)
    }

    private fun setGradient() {
        val width = paint.measureText(text.toString())
        if (withShadow)
            setShadowLayer(1.5f, -1.5f, 1.5f, Color.LTGRAY)
        setTextColor(Color.WHITE)
        gradient = LinearGradient(
            0f, 0f, width, textSize,
            intArrayOf(
                startColor,
                endColor
            ), null, Shader.TileMode.CLAMP
        )
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            paint.shader = gradient
        }
    }
}