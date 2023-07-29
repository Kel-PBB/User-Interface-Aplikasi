package com.example.cloudrent.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.cloudrent.R
import com.makeramen.roundedimageview.RoundedImageView

class SliderAdapter(private val slides: List<Int>) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    inner class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_slide, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        val slideImage = holder.itemView.findViewById<ImageView>(R.id.img_slider)
        val actualPosition = position % slides.size
        val currentPosition = if(actualPosition == 0) slides.lastIndex else  actualPosition - 1
        slideImage.setImageResource(slides[actualPosition])
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }
}