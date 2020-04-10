package io.sulek.schibstedtask.ui.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

class DataStateLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private var currentState: State? = null

    fun changeState(state: State) {
        if (state == currentState) return
        currentState = state

        val dataView: View = getChildAt(0)
        val emptyView: View = getChildAt(1)
        val loadingView: View = getChildAt(2)
        val errorView: View = getChildAt(3)

        when (currentState) {
            State.DATA -> {
                dataView.visibility = View.VISIBLE
                emptyView.visibility = View.GONE
                loadingView.visibility = View.GONE
                errorView.visibility = View.GONE
            }
            State.LOADING -> {
                dataView.visibility = View.GONE
                emptyView.visibility = View.GONE
                loadingView.visibility = View.VISIBLE
                errorView.visibility = View.GONE
            }
            State.EMPTY -> {
                dataView.visibility = View.GONE
                emptyView.visibility = View.VISIBLE
                loadingView.visibility = View.GONE
                errorView.visibility = View.GONE
            }
            State.FAILURE -> {
                emptyView.visibility = View.GONE
                dataView.visibility = View.GONE
                loadingView.visibility = View.GONE
                errorView.visibility = View.VISIBLE
            }
        }
    }

    enum class State {
        DATA,
        LOADING,
        EMPTY,
        FAILURE
    }
}