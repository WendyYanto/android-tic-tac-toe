package com.example.tictactoe

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class TicTacToeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var boardSize = 300
    private val boardList by lazy {
        mutableListOf<Rect>()
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
}