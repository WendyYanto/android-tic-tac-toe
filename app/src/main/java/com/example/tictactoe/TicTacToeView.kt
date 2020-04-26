package com.example.tictactoe

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class TicTacToeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }
    private var boardSize = 300
    private val boardList by lazy {
        mutableListOf<Rect>()
    }
    private val boardStateList by lazy {
        mutableListOf<String>()
    }

    init {
        for (index in 1..9) {
            boardStateList.add(State.BLANK)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val size = widthMeasureSpec.coerceAtMost(heightMeasureSpec)
        setMeasuredDimension(size, size)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val size = w.coerceAtMost(h)
        boardSize = size / 3
        this.generateBoard()
    }

    private fun generateBoard() {
        var row = 0
        for (index in 1..9) {
            var column = (index % 3) - 1
            if (column < 0) {
                column = 2
            }
            val rect = Rect()
            with(rect) {
                top = row * boardSize
                left = column * boardSize
                right = left + boardSize
                bottom = top + boardSize
            }
            boardList.add(rect)
            if (index % 3 == 0) {
                row = index / 3
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.strokeWidth = 5F
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        canvas?.let { it ->
            drawBoard(it)
            boardList.forEachIndexed { index, board ->
                when (boardStateList[index]) {
                    State.CIRCLE -> drawCircle(canvas, board)
                }
            }
        }
    }

    private fun drawBoard(canvas: Canvas) {
        boardList.forEach {
            canvas.drawRect(it, paint)
        }
    }

    private fun drawCircle(canvas: Canvas, block: Rect) {
        canvas.drawCircle(
            block.exactCenterX(),
            block.exactCenterY(),
            boardSize.toFloat() / 2,
            paint
        )
    }
}