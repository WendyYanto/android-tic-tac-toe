package dev.wendyyanto.tictactoe.customview.instancestate

import android.os.Parcel
import android.os.Parcelable
import android.view.View.BaseSavedState

class TicTacToeInstanceState : BaseSavedState {

    var boardStateList = mutableListOf<String>()
    var userOddTouchFlag = 0
    var playerOneChoices = mutableListOf<Int>()
    var playerTwoChoices = mutableListOf<Int>()

    constructor(parcelable: Parcelable?) : super(parcelable)

    constructor(parcel: Parcel) : super(parcel) {
        boardStateList.clear()
        parcel.readList(mutableListOf<String>() as List<*>, String::class.java.classLoader)
        parcel.readList(mutableListOf<Int>() as List<*>, Int::class.java.classLoader)
        userOddTouchFlag = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeList(boardStateList as List<String>)
        parcel.writeList(playerOneChoices as List<Int>)
        parcel.writeList(playerTwoChoices as List<Int>)
        parcel.writeInt(userOddTouchFlag)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TicTacToeInstanceState> {
        override fun createFromParcel(parcel: Parcel): TicTacToeInstanceState {
            return TicTacToeInstanceState(
                parcel
            )
        }

        override fun newArray(size: Int): Array<TicTacToeInstanceState?> {
            return arrayOfNulls(size)
        }
    }

}