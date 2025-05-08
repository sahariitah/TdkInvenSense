package com.example.tdkinvences

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Shader
import android.util.AttributeSet
import com.robinhood.spark.SparkAdapter
import com.robinhood.spark.SparkView

class CustomSparkView(context: Context, attrs: AttributeSet) : SparkView(context, attrs) {

    private val linePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
    }

    private val shadowPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {

        val path = adapter?.let { createPath(it) } ?: return


        val shadowPath = createShadowPath(adapter)


        shadowPaint.shader = LinearGradient(
            0f, 0f, 0f, height.toFloat(),
            Color.parseColor("#80FFA500"),
            Color.TRANSPARENT,
            Shader.TileMode.CLAMP
        )
        canvas.drawPath(shadowPath, shadowPaint)


        linePaint.shader = LinearGradient(
            0f, 0f, width.toFloat(), 0f,
            Color.YELLOW, Color.parseColor("#CC5500"),
            Shader.TileMode.CLAMP
        )
        canvas.drawPath(path, linePaint)


    }

    private fun createPath(adapter: SparkAdapter): Path {
        val path = Path()
        if (adapter.count <= 0) return path

        val maxY = (0 until adapter.count).maxOf { adapter.getY(it) }
        val minY = (0 until adapter.count).minOf { adapter.getY(it) }

        for (i in 0 until adapter.count) {
            val x = width.toFloat() / (adapter.count - 1) * i
            val y = height - ((adapter.getY(i) - minY) / (maxY - minY) * height)
            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        return path
    }

    private fun createShadowPath(adapter: SparkAdapter): Path {
        val shadowPath = createPath(adapter)
        shadowPath.lineTo(width.toFloat(), height.toFloat())
        shadowPath.lineTo(0f, height.toFloat())
        shadowPath.close()
        return shadowPath
    }
}
