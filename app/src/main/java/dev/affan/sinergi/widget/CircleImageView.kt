package dev.affan.sinergi.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.widget.ImageView


class CircleImageView : ImageView {
    private lateinit var b: Bitmap

    constructor(context: Context) : super(context) {
        createBitmap()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        createBitmap()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        createBitmap()
    }

    private fun createBitmap() {
        this.b = if (drawable is BitmapDrawable) (drawable as BitmapDrawable).bitmap else {
            val bmp = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0)
                Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            else
                Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmp)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bmp
        }
    }

    override fun onDraw(canvas: Canvas?) {
        //super.onDraw(canvas)
        if (drawable == null)
            return
        if (width == 0 || height == 0) {
            return
        }
        val bitmap = b.copy(Bitmap.Config.ARGB_8888, true)

        val w = width
        val h = height
        val roundBitmap = getCroppedBitmap(bitmap, w)
        canvas?.drawBitmap(roundBitmap, 0f, 0f, null)
    }

    private fun getCroppedBitmap(bmp: Bitmap, radius: Int): Bitmap {
        val sbmp: Bitmap
        sbmp = if (bmp.width != radius || bmp.height != radius) {
            val smallest = Math.min(bmp.width, bmp.height).toFloat()
            val factor = smallest / radius
            Bitmap.createScaledBitmap(bmp, (bmp.width / factor).toInt(), (bmp.height / factor).toInt(), false)
        } else {
            bmp
        }

        val output = Bitmap.createBitmap(radius, radius, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val color = "#BAB399"
        val paint = Paint()
        val rect = Rect(0, 0, radius, radius)

        paint.isAntiAlias = true
        paint.isFilterBitmap = true
        paint.isDither = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = Color.parseColor(color)
        canvas.drawCircle(
            radius / 2 + 0.7f, radius / 2 + 0.7f,
            radius / 2 + 0.1f, paint
        )
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(sbmp, rect, rect, paint)
        return output
    }
}