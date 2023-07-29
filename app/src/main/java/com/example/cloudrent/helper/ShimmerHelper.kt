import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.children
import com.example.cloudrent.R
import com.facebook.shimmer.ShimmerFrameLayout

class ShimmerHelper(private val context: Context) {
    private val shimmerLayout: ShimmerFrameLayout = ShimmerFrameLayout(context)

    private val originalDrawables: MutableMap<View, Drawable?> = mutableMapOf()

    fun startShimmer(viewGroup: ViewGroup) {
        // Save the original drawables of views in the viewGroup
        for (view in viewGroup.children) {
            originalDrawables[view] = view.background
        }

        // Clone views and set shimmer backgrounds
        for (view in viewGroup.children) {
            val viewClone = createViewClone(view)
            shimmerLayout.addView(viewClone)
        }
        Log.d("ShimmerHelper", "Start shimmer")
        viewGroup.addView(shimmerLayout)
        shimmerLayout.startShimmer()
        shimmerLayout.requestLayout()
        shimmerLayout.invalidate()
    }

    fun stopShimmer() {
        shimmerLayout.stopShimmer()
        shimmerLayout.removeAllViews()
        shimmerLayout.parent?.let {
            (it as ViewGroup).removeView(shimmerLayout)
        }
        Log.d("ShimmerHelper", "Stop shimmer")

        // Restore the original drawables of views in the viewGroup
        for ((view, drawable) in originalDrawables) {
            view.background = drawable
        }
    }

    private fun createViewClone(originalView: View): View {
        return when (originalView) {
            is ImageView -> {
                val imageViewClone = ImageView(context)
                imageViewClone.layoutParams = originalView.layoutParams
                imageViewClone.scaleType = originalView.scaleType

                // Use a transparent placeholder drawable for the ImageView
                imageViewClone.setImageDrawable(ColorDrawable(Color.TRANSPARENT))

                imageViewClone
            }
            is TextView -> {
                val textViewClone = TextView(context)
                textViewClone.layoutParams = originalView.layoutParams
                textViewClone.text = originalView.text
                textViewClone.textSize = originalView.textSize
                textViewClone.setTextColor(originalView.currentTextColor)
                textViewClone.typeface = originalView.typeface

                // Use a transparent ColorDrawable as the background for TextView
                textViewClone.background = ColorDrawable(Color.TRANSPARENT)

                // Set the custom shimmer background for TextView
                textViewClone.setBackgroundResource(R.drawable.shimmer_background)

                textViewClone
            }
            // Handle other view types as needed (e.g., Button, EditText, etc.)
            else -> View(context)
        }
    }
}
