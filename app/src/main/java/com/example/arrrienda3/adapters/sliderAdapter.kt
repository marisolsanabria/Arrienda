package com.example.arrrienda3.adapters

import android.content.Context
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import com.example.arrrienda3.R
import com.squareup.picasso.Picasso

class sliderAdapter: PagerAdapter {

    var context: Context
    var images: ArrayList<String>
    lateinit var inflater : LayoutInflater
    //var positionList: Int

    constructor(context: Context, images: ArrayList<String>): super(){
        this.context=context
        this.images=images
        //this.positionList=positionList

    }
    override fun getCount(): Int {
        return images.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean = view == `object` as RelativeLayout

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var image:ImageView
        inflater= context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view: View= inflater.inflate(R.layout.slider_image_item, container,false)
        image=view.findViewById(R.id.sliderImageView)
        Picasso.get().load(images[position]).into(image)
        container!!.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container!!.removeView( `object` as RelativeLayout)
    }


}
