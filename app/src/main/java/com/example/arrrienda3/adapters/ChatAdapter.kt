package com.example.arrrienda3.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.arrrienda3.R
import com.example.arrrienda3.models.Message
import com.example.arrrienda3.utils.CircleTransform
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.card_post.view.*
import kotlinx.android.synthetic.main.fragment_chat_dialog.view.*
import kotlinx.android.synthetic.main.fragment_chat_item_left.view.*
import kotlinx.android.synthetic.main.fragment_chat_item_right.view.*
import java.text.SimpleDateFormat

class ChatAdapter (val items:List<Message>, val userId:String):RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    //mensajes que recibo
    private val GLOBAL_MESSAGE=1
    //mis mensajes
    private val MY_MESSAGE=2

    private val  mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // como el adaptador va a tener 2 tipos de layout dependiendo de quien es el mensaje
    private val layoutRight= R.layout.fragment_chat_item_right
    private val layoutLeft=R.layout.fragment_chat_item_left

    // sobreescribimos el siguiente método, que está hecho para este tipo de adaptadores que tiene mas de un tipo

    // con position recogemos el objeto de la lista de mensajes, si e autor es mi usuario, entonces será mi mensaje de lo contrario será un global message

    override fun getItemViewType(position: Int)= if (items[position].authorId==userId) MY_MESSAGE else GLOBAL_MESSAGE



    override fun getItemCount()=items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        //con el view type que nos llega del getItemViewType
        return when (viewType){
            MY_MESSAGE -> viewHolderR(LayoutInflater.from(parent.context).inflate(layoutRight,parent,false))
            //todo ---esto es lo unico que cambie respecto al video
            else-> viewHolderL(LayoutInflater.from(parent.context).inflate(layoutLeft,parent,false))


        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // en este metodo no nos llega el view type entonces lo hacemos de la siguiente forma para obtener el viewtype

        when(holder.itemViewType){
        MY_MESSAGE->(holder as viewHolderR).bind(items[position])
            GLOBAL_MESSAGE->(holder as viewHolderL).bind(items[position])
        }
    }
    }





//ahora vamos a crear los view holder

class viewHolderR(itemView: View): RecyclerView.ViewHolder(itemView) {

     fun bind(message: Message) = with(itemView) {
        textViewMessageRight.text = message.message
        textViewTimeRight.text = SimpleDateFormat("hh:mm").format(message.sentAt)
      /*  val currentUser=FirebaseAuth.getInstance().currentUser!!
       // currentUser.photoUrl.toString=message.profileImageURL

      val photo=  currentUser.photoUrl.toString()


        if (photo.isEmpty()){

            Glide.with(context).load(R.drawable.perfil_icon).override(100,100).circleCrop().into(imageViewProfileRight)
        }else{
            Glide.with(context).load(currentUser.photoUrl).override(100,100).circleCrop().into(imageViewProfileRight)
        }
        */

         if (message.profileImageURL != "") {
             Glide.with(context).load(message.profileImageURL).override(100,100).circleCrop().into(imageViewProfileRight)
         }else {
             Glide.with(context).load(R.drawable.perfil_icon).override(100,100).circleCrop().into(imageViewProfileRight)
         }

        }
    }


class viewHolderL(itemView: View): RecyclerView.ViewHolder(itemView){

    fun bind(message:Message)=with(itemView) {
        textViewMessageLeft.text=message.message
        textViewTimeLeft.text=SimpleDateFormat("hh:mm").format(message.sentAt)
        //acá cargamos la imagen con picasso

        if (message.profileImageURL != "") {
            Glide.with(context).load(message.profileImageURL).override(100,100).circleCrop().into(imageViewProfileLeft)
        }else {
            Glide.with(context).load(R.drawable.perfil_icon).override(100,100).circleCrop().into(imageViewProfileLeft)
        }

    }

}