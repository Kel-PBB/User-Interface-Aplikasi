package com.example.cloudrent

import android.content.Context
import android.graphics.Rect
import android.util.DisplayMetrics
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class SpaceItemDecoration(private val context: Context, private val marginInDp: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val marginInPixels = convertDpToPx(marginInDp)
        outRect.set(marginInPixels, marginInPixels, marginInPixels, marginInPixels)
    }

    private fun convertDpToPx(dp: Int): Int {
        val displayMetrics: DisplayMetrics = context.resources.displayMetrics
        return (dp * (displayMetrics.densityDpi / 160f)).toInt()
    }
}