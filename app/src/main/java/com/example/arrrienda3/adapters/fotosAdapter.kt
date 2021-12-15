package com.example.arrrienda3.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.arrrienda3.MainActivity
import com.example.arrrienda3.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_foto.view.*

class fotosAdapter  (val listFotos: List<String>,var clickFLitenerr: onItemFotoClickLitener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {



        // acá en la primera linea nos dará el size de el data y así tendremos el número de items
        override fun getItemCount() = listFotos.size



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            // en from(context) puede ir activity o parent.context
            val layout = LayoutInflater.from(parent.context).inflate(R.layout.item_foto, parent, false)

            return viewHolderrr(layout)
        }


        //en el onBindViewHolder es donde vamos a mostrar toda la información
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            // (holder as viewHolder).bind(dataset[position])
            (holder as viewHolderrr).initializer(listFotos.get(position), clickFLitenerr)


        }
    }
// vamos a crear un view holder que contendrá pues el layout

  class viewHolderrr(itemView: View): RecyclerView.ViewHolder(itemView){

      fun  initializer(item:String,action: onItemFotoClickLitener){
          if (item!=null){
              Picasso.get().load(item).into(itemView.ivItemFoto)
          }
          itemView.setOnClickListener{
              action.onItemFClick(item,adapterPosition)
          }

      }

  }
    interface onItemFotoClickLitener{
        fun onItemFClick(item: String, position: Int){

        }

    }
