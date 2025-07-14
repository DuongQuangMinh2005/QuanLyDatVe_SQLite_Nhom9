package com.example.quanlydatve_sqlite.customview

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatTextView

class AutoScrollTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    private var animator: ObjectAnimator? = null

    fun startScroll(speed: Float = 30f) { // speed: px/giây, càng nhỏ chạy càng chậm
        post {
            val textWidth = paint.measureText(text.toString())
            val viewWidth = width.toFloat()
            if (textWidth <= viewWidth) return@post  // Không cần scroll nếu text ngắn hơn view

            animator?.cancel()
            translationX = viewWidth
            val duration = ((viewWidth + textWidth) / speed * 1000).toLong()
            animator = ObjectAnimator.ofFloat(this, "translationX", viewWidth, -textWidth)
            animator?.apply {
                this.duration = duration
                interpolator = LinearInterpolator()
                repeatCount = ObjectAnimator.INFINITE
                start()
            }
        }
    }

    fun stopScroll() {
        animator?.cancel()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }

    override fun isFocused(): Boolean = true // Để có hiệu ứng marquee nếu muốn
}