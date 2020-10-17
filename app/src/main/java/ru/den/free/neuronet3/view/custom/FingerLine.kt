package ru.den.free.neuronet3.view.custom

import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.max


class FingerLine constructor(context: Context?, attrs: AttributeSet? = null):View(context, attrs) {

    companion object{
        const val MNIST_IMAGE_SIZE  = 28
        const val MNIST_IMAGE_MARGIN = 4
    }

    interface IFingerLine{
        fun onBitmap(bmp: Bitmap, img: Array<Array<Int>>)
    }

    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var path   = Path()
    private var listener : IFingerLine? = null

    init {
        mPaint.style = Paint.Style.STROKE
        mPaint.color = Color.WHITE
        mPaint.strokeWidth = 36.0f
        viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                } else {
                    viewTreeObserver.removeGlobalOnLayoutListener(this)
                }
                mPaint.strokeWidth = width / 8f
            }
        })
    }

    fun setListener(listener: IFingerLine){
        this.listener = listener
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawColor(Color.BLACK)
        canvas.drawPath(path, mPaint)
    }

    private fun toGray(colour: Int) : Int {
        val red = Color.red(colour)
        val blue = Color.blue(colour)
        val green = Color.green(colour)
        return (red + blue + green) / 3
    }

    private fun getPath(bmp: Bitmap): Rect {
        val res = Rect()
        res.top = -1
        for (y in 0 until bmp.height){
            for (x in 0 until bmp.width){
                val p = bmp.getPixel(x, y)
                if (Color.red(p) + Color.green(p) + Color.blue(p) > 0){
                    res.top = y
                    break
                }
            }
            if (res.top >= 0) break
        }
        res.bottom = -1
        for (y in bmp.height - 1 downTo 0){
            for (x in 0 until bmp.width){
                val p = bmp.getPixel(x, y)
                if (Color.red(p) + Color.green(p) + Color.blue(p) > 0){
                    res.bottom = y
                    break
                }
            }
            if (res.bottom >= 0) break
        }
        res.left = -1
        for (x in 0 until bmp.width){
            for (y in 0 until bmp.height){
                val p = bmp.getPixel(x, y)
                if (Color.red(p) + Color.green(p) + Color.blue(p) > 0){
                    res.left = x
                    break
                }
            }
            if (res.left >= 0) break
        }
        res.right = -1
        for (x in bmp.width - 1 downTo 0){
            for (y in 0 until bmp.height){
                val p = bmp.getPixel(x, y)
                if (Color.red(p) + Color.green(p) + Color.blue(p) > 0){
                    res.right = x
                    break
                }
            }
            if (res.right >= 0) break
        }

        if (res.left < 0) res.left = 0
        if (res.top  < 0) res.top  = 0

        return res
    }

    private fun createImgBitmap(){
        GlobalScope.launch(Dispatchers.IO) {
            var bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            var c = Canvas(bitmap)
            draw(c)
            val rect = getPath(bitmap)
            bitmap = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height())
            val size = max(bitmap.width, bitmap.height)
            var bitmap2 = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            c = Canvas(bitmap2)
            c.drawColor(Color.BLACK)
            c.drawBitmap(bitmap, (size - bitmap.width) / 2f, (size - bitmap.height) / 2f, mPaint)
            val bmp = Bitmap.createScaledBitmap(
                bitmap2,
                MNIST_IMAGE_SIZE - MNIST_IMAGE_MARGIN * 2,
                MNIST_IMAGE_SIZE - MNIST_IMAGE_MARGIN * 2, false
            )
            val bmp2 = Bitmap.createBitmap(
                MNIST_IMAGE_SIZE,
                MNIST_IMAGE_SIZE,
                Bitmap.Config.ARGB_8888
            )
            val c2 = Canvas(bmp2)
            c2.drawColor(Color.BLACK)
            c2.drawBitmap(bmp, MNIST_IMAGE_MARGIN.toFloat(), MNIST_IMAGE_MARGIN.toFloat(), mPaint)

            val arr = Array(MNIST_IMAGE_SIZE){ y->
                Array(MNIST_IMAGE_SIZE){ x->
                    toGray(bmp2.getPixel(x, y))
                }
            }

            launch(Dispatchers.Main){
                listener?.onBitmap(bmp2, arr)
                invalidate()
                path = Path()
            }

        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                path = Path()
                path.moveTo(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                path.lineTo(event.x, event.y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                if (listener != null) {
                    createImgBitmap()
                } else {
                    invalidate()
                    path = Path()
                }
            }
        }
        return true
    }


}