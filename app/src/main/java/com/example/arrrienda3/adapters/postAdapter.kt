package com.example.arrrienda3.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.arrrienda3.MainActivity
import com.example.arrrienda3.R
import com.example.arrrienda3.models.Message
import com.example.arrrienda3.models.Post
import com.example.arrrienda3.models.User
import com.example.arrrienda3.ui.Fragments.PublicacionDialogFragment
import com.example.arrrienda3.ui.activities.logInActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_post.view.*
import kotlinx.android.synthetic.main.fragment_chat_item_right.view.*
import kotlinx.android.synthetic.main.fragment_crearpublicacion.*
import java.text.SimpleDateFormat
import kotlin.coroutines.coroutineContext


class postAdapter (val activity:MainActivity, val dataset:List<Post>, var clickLitener: onItemClickLitener):RecyclerView.Adapter<RecyclerView.ViewHolder>() {



    // acá en la primera linea nos dará el size de el data y así tendremos el número de items
   override fun getItemCount() = dataset.size



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // en from(context) puede ir activity o parent.context
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.card_post, parent, false)

        return viewHolder(layout)
    }


    //en el onBindViewHolder es donde vamos a mostrar toda la información
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       // (holder as viewHolder).bind(dataset[position])

        (holder as viewHolder).initialize(dataset.get(position), clickLitener)
        holder.publisherInfo(dataset.get(position))

    }
}
// vamos a crear un view holder que contendrá pues el layout

        class viewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

            private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
            val currentUser = mAuth.currentUser

            val firestore=FirebaseFirestore.getInstance()

            //toma los datos de donde los ingresa el usuario y los asigna a la variable de la clase post

            fun bind(post: Post) = with(itemView) {
                tvDescriptionPost.text = post.getPostDescription()
                title.text = post.getTitle()
                val sdf = SimpleDateFormat("dd/M/yyyy")
                tvFecha.text = sdf.format(post.getDate())

            }

            fun initialize(item: Post, action: onItemClickLitener){

                itemView.tvDescriptionPost.text=item.getPostDescription()
                itemView.title.text=item.getTitle()
                val sdf = SimpleDateFormat("dd/M/yyyy")
                itemView.tvFecha.text=sdf.format(item.getDate())

                itemView.setOnClickListener {
                    action.onItemClick(item, adapterPosition)
                }


            }

            fun publisherInfo(item: Post){
                val reference= firestore.collection("users")
                        .document(item.getUserId())

                reference.get().addOnSuccessListener {

                    if (it.get("photo").toString() != "" && it.get("photo").toString() != "null") {
                        Picasso.get().load(it.get("photo").toString()).into(itemView.ivProiflePicturePost)
                    } else {
                        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/arrienda3.appspot.com/o/sin%20foto.png?alt=media&token=6152a0c8-7ed2-47ae-b558-27944640cab9").into(itemView.ivProiflePicturePost)
                    }

                   itemView.tvUserNamePost.tvUserNamePost.text=it.get("name").toString()

                }
            }



}



interface onItemClickLitener{
    fun onItemClick(item:Post, position: Int){

    }


}








