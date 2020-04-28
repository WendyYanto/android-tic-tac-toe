package com.example.tictactoe.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Path
import android.os.Handler
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import com.example.tictactoe.R
import com.example.tictactoe.customview.constant.State
import com.example.tictactoe.customview.instancestate.TicTacToeInstanceState
import com.example.tictactoe.toBoolean
import com.example.tictactoe.toInt

class TicTacToeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var boardSize = 300
    private var userOddTouchFlag = false
    private var isResetting = false
    private val boardList by lazy {
        mutableListOf<Rect>()
    }
    private val boardStateList by lazy {
        mutableListOf<String>()
    }
    private val playerOneChoice by lazy {
        mutableListOf<Int>()
    }
    private val playerTwoChoice by lazy {
        mutableListOf<Int>()
    }
    private val winnerChoice by lazy {
        mutableListOf<Int>()
    }
    private val paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }
    private val path by lazy {
        Path()
    }

    init {
        isSaveEnabled = true
        setupPaint()
        initBoardState()
    }

    private fun initBoardState() {
        for (index in 1..9) {
            boardStateList.add(State.BLANK)
        }
    }

    private fun setupPaint() {
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        setupStrokeWidth()
    }

    private fun setupStrokeWidth() {
        val displayMetrics = context.resources.displayMetrics
        val density = displayMetrics.density
        paint.strokeWidth = density * DEFAULT_STROKE_WIDTH
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
        canvas?.let { it ->
            drawBoard(it)
            boardList.forEachIndexed { index, board ->
                when (boardStateList[index]) {
                    State.CIRCLE -> drawCircle(canvas, board)
                    State.CROSS -> drawCross(canvas, board)
                }
            }
            if (winnerChoice.isNotEmpty()) {
                drawWinnerLine(it)
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
            boardSize.toFloat() / 3,
            paint
        )
    }

    private fun drawCross(canvas: Canvas, block: Rect) {
        path.moveTo(block.left.toFloat() + CROSS_OFFSET, block.top.toFloat() + CROSS_OFFSET)
        path.lineTo(block.right.toFloat() - CROSS_OFFSET, block.bottom.toFloat() - CROSS_OFFSET)
        path.moveTo(block.right.toFloat() - CROSS_OFFSET, block.top.toFloat() + CROSS_OFFSET)
        path.lineTo(block.left.toFloat() + CROSS_OFFSET, block.bottom.toFloat() - CROSS_OFFSET)
        canvas.drawPath(path, paint)
    }

    private fun drawWinnerLine(canvas: Canvas) {
        val start = boardList[winnerChoice.first()]
        val end = boardList[winnerChoice.last()]
        path.moveTo(start.exactCenterX(), start.exactCenterY())
        path.lineTo(end.exactCenterX(), end.exactCenterY())
        canvas.drawPath(path, paint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let { motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_UP -> updateBoardState(event)
                else -> true
            }
        }
        return true
    }

    private fun updateBoardState(event: MotionEvent): Boolean {
        if (isResetting) return true
        boardList.asSequence().find { rect ->
            rect.contains(event.x.toInt(), event.y.toInt())
        }?.apply {
            val index = boardList.indexOf(this)
            if (boardStateList[index] != State.BLANK) return true
            if (userOddTouchFlag) {
                playerTwoChoice.add(index)
                boardStateList[index] = State.CROSS
            } else {
                playerOneChoice.add(index)
                boardStateList[index] = State.CIRCLE
            }
            findWinner()
            userOddTouchFlag = userOddTouchFlag.not()
            invalidate()
        }
        return true
    }

    private fun findWinner() {
        val state = if (userOddTouchFlag) {
            if (playerTwoChoice.size < 3) return
            State.CROSS
        } else {
            if (playerOneChoice.size < 3) return
            State.CIRCLE
        }
        when {
            isWin(state) -> showToast(R.string.win_message, state, RESET_TIME / 1000)
            isDraw() -> showToast(R.string.draw_message, RESET_TIME / 1000)
            else -> return
        }
        isResetting = true
        Handler().postDelayed({
            reset()
            isResetting = false
            invalidate()
        }, RESET_TIME)
    }

    private fun isDraw(): Boolean {
        return playerOneChoice.size + playerTwoChoice.size == 9
    }

    private fun showToast(@StringRes stringId: Int, vararg value: Any) {
        Toast.makeText(
            this.context,
            this.context.getString(stringId, *value),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun isWin(state: String): Boolean {
        return checkBlock(state, arrayListOf(0, 1, 2)) or
                checkBlock(state, arrayListOf(3, 4, 5)) or
                checkBlock(state, arrayListOf(6, 7, 8)) or
                checkBlock(state, arrayListOf(0, 3, 6)) or
                checkBlock(state, arrayListOf(1, 4, 7)) or
                checkBlock(state, arrayListOf(2, 5, 8)) or
                checkBlock(state, arrayListOf(0, 4, 8)) or
                checkBlock(state, arrayListOf(2, 4, 6))
    }

    private fun reset() {
        boardStateList.clear()
        playerOneChoice.clear()
        playerTwoChoice.clear()
        winnerChoice.clear()
        path.reset()
        userOddTouchFlag = false
        initBoardState()
    }

    private fun checkBlock(state: String, possibleSolutions: List<Int>): Boolean {
        val response = when (state) {
            State.CROSS -> {
                possibleSolutions.all {
                    playerTwoChoice.contains(it)
                }
            }
            State.CIRCLE -> {
                possibleSolutions.all {
                    playerOneChoice.contains(it)
                }
            }
            else -> false
        }
        if (response) {
            winnerChoice.addAll(possibleSolutions)
        }
        return response
    }

    override fun onSaveInstanceState(): Parcelable? {
        val instanceState = super.onSaveInstanceState()
        val state =
            TicTacToeInstanceState(
                instanceState
            )
        state.boardStateList.addAll(this.boardStateList)
        state.userOddTouchFlag = this.userOddTouchFlag.toInt()
        state.playerOneChoices.addAll(this.playerOneChoice)
        state.playerTwoChoices.addAll(this.playerTwoChoice)
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        state?.let {
            val instanceState = it as TicTacToeInstanceState
            super.onRestoreInstanceState(instanceState.superState)
            this.boardStateList.clear()
            this.boardStateList.addAll(instanceState.boardStateList)
            this.playerOneChoice.addAll(instanceState.playerOneChoices)
            this.playerTwoChoice.addAll(instanceState.playerTwoChoices)
            this.userOddTouchFlag = instanceState.userOddTouchFlag.toBoolean()
            invalidate()
        }
    }

    companion object {
        private const val CROSS_OFFSET = 75
        private const val DEFAULT_STROKE_WIDTH = 4F
        private const val RESET_TIME = 2000L
    }
}