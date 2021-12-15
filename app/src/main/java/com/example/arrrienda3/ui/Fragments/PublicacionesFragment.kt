package com.example.arrrienda3.ui.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.arrrienda3.MainActivity
import com.example.arrrienda3.R
import com.example.arrrienda3.adapters.ChatAdapter
import com.example.arrrienda3.adapters.onItemClickLitener
import com.example.arrrienda3.adapters.postAdapter
import com.example.arrrienda3.models.Constants
import com.example.arrrienda3.models.Message
import com.example.arrrienda3.models.Post
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_chat_dialog.view.*
import kotlinx.android.synthetic.main.fragment_favoritos.*
import kotlinx.android.synthetic.main.fragment_publicaciones.*
import kotlinx.android.synthetic.main.fragment_publicaciones.view.*
import java.util.*
import kotlin.collections.ArrayList


class PublicacionesFragment : Fragment(), onItemClickLitener {
    private lateinit var _view:View

    private lateinit var adapter:postAdapter

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var postDBRef: CollectionReference

    private var postList:MutableList<Post> = arrayListOf()

    private var postSuscription: ListenerRegistration?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _view= inflater.inflate(R.layout.fragment_publicaciones, container, false)





        setUpPostDB()
        setUpRecyclerView()
       // subscribeToPost()


        return _view
    }



    private fun  setUpPostDB(){

        //crea la coleccion en firestore o sii ya está va a añadir en ella
        postDBRef=store.collection("post")

    }

   fun setUpRecyclerView(){

       postDBRef.addSnapshotListener(EventListener<QuerySnapshot> { value, error ->
//vamos a darle un uid a cada post del firebase para esto lo hacemos con el index

           if (error != null || value == null) {
              // Toast.makeText(context,"error",Toast.LENGTH_LONG).show()
           } else {
               // 7
               val posts = ArrayList<Post>()
               // 8
               for (doc in value) {
                   // 9
                   val post = doc.toObject(Post::class.java)
                   posts.add(post)
               }
               posts.forEachIndexed { index, post ->
                   post.uid = value.documents[index].id

                   //ahora cargamos el adapter en el main activity con una cantidad de items igual al tamaño de posts que nos dara la variable posts
                  adapter = postAdapter(MainActivity(), posts, this)


                   //acá coloco que quiero mostrar y como lo quiero mostrar

                   //tipo de layout en que lo quiero mostrar
                   val layoutManayer = LinearLayoutManager(context)

                   _view.rvPublicaciones.setHasFixedSize(true)
                   _view.rvPublicaciones.layoutManager = layoutManayer
                   _view.rvPublicaciones.itemAnimator = DefaultItemAnimator()
                  _view.rvPublicaciones.adapter = adapter

               }
           }
       })
   }

    override fun onItemClick(item: Post, position: Int) {

        Constants.postId=item.uid

        findNavController().navigate(R.id.publicacionDialogFragment)
        // Toast.makeText(context, item.getTitle(),Toast.LENGTH_SHORT).show()

    }

}


