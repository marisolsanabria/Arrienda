package com.example.arrrienda3.ui.activities

import ahmed.easyslider.SliderAdapter
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ViewSwitcher
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.PagerAdapter
import com.example.arrrienda3.R
import com.example.arrrienda3.adapters.fotosAdapter
import com.example.arrrienda3.adapters.sliderAdapter
import kotlinx.android.synthetic.main.activity_foto.*
import kotlinx.android.synthetic.main.fragment_foto.*


class FotoActivity : AppCompatActivity() {

    var listFotos= mutableListOf<Int>()
    lateinit var adapter: sliderAdapter




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foto)
        val posicion= intent.getIntExtra("posicion", 0)
        val listFotos: ArrayList<String> = intent.getStringArrayListExtra("listFotos") as ArrayList<String>

        adapter= sliderAdapter( this, listFotos)


        viewPagerFoto.adapter= adapter
        viewPagerFoto.currentItem=posicion


/*
        val posicion= intent.getIntExtra("posicion",0)
        val listFotos: ArrayList<String> = intent.getStringArrayListExtra("listFotos") as ArrayList<String>

        adapter= fotosAdapter(listFotos,this)
        vpBigFoto.adapter=adapter
        vpBigFoto.orientation=ViewPager2.ORIENTATION_HORIZONTAL

 */










      //  Picasso .get().load(listFotos[posicion]).into(isBigFoto)
    }
    
}