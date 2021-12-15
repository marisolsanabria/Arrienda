package com.example.arrrienda3.ui.Fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.arrrienda3.R
import com.example.arrrienda3.models.Constants
import com.example.arrrienda3.models.Post
import com.google.common.reflect.Reflection.getPackageName
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_crearpublicacion.*
import kotlinx.android.synthetic.main.fragment_crearpublicacion.fab_chat_button
import kotlinx.android.synthetic.main.fragment_crearpublicacion.ivPhotoProfilePublicacion
import kotlinx.android.synthetic.main.fragment_crearpublicacion.tvDescripcionPublicacion
import kotlinx.android.synthetic.main.fragment_crearpublicacion.tvNombrePublicacion
import kotlinx.android.synthetic.main.fragment_crearpublicacion.tvTituloPublicacionn
import kotlinx.android.synthetic.main.fragment_crearpublicacion.view.*
import kotlinx.android.synthetic.main.fragment_publicacion_dialog.*
import java.util.*
import kotlin.collections.HashMap

class CrearpublicacionFragment : Fragment() {

    private lateinit var _view: View

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser
    var refUsers: DatabaseReference? = null

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var postDBRef: CollectionReference
    private var firebaseStorage=FirebaseStorage.getInstance().reference

    private var myUrl= arrayListOf<String>()
    private var imageUri:Uri?=null
    private var storagePostPicRef:StorageReference?=null

    var photo:String=""

    //private var postSuscription: ListenerRegistration?=null
    //private val postList: ArrayList<Post> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _view = inflater.inflate(R.layout.fragment_crearpublicacion, container, false)

        currentUser= FirebaseAuth.getInstance().currentUser!!

        setUpPostDB()
        setUpCurrentUser()
        setUpPublicarBoton()

        storagePostPicRef=firebaseStorage.child("Posts Pictures")

        //crear la lista de imagenes

        /*   val bundle=arguments
           if (bundle!=null){
              val fotos= bundle.getParcelableArrayList<Uri>("listadefotos") as ArrayList<*>

              // val fotosarray=fotos.toArray()

               imageViewFotosPost.setImageURI(fotos[1]as Uri?)
              // imageViewFotos.setImageBitmap(fotos[1] as Bitmap?)
           }
            */

        return _view

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (Constants.imageUri !=null){
            myUrl= Constants.imageUri!!
        }

        //hacemos que nos muestre el nombre del usuario actual
        //   tvNombre.text=currentUser.displayName?.let{currentUser.displayName} ?: run {"sin nombre"}
//para que me muestre de una la imagen del usuario
        val userRef=FirebaseFirestore.getInstance().collection("users")
        userRef.document(currentUser.uid).get().addOnSuccessListener {
            tvNombrePublicacion.text=it.get("name").toString()
            if (it.get("photo").toString()!=null && it.get("photo").toString()!= "" ){
                context?.let { it1 -> Glide.with(it1).load(it.get("photo").toString()).into(
                        ivPhotoProfilePublicacion) }
            }else {
                context?.let { it1 ->
                    Glide.with(it1).load("https://firebasestorage.googleapis.com/v0/b/arrienda3.appspot.com/o/profile%20default.png?alt=media&token=071099bf-47f8-4369-bb2f-e0637e4b3d8b").into(ivPhotoProfilePublicacion) }
            }
        }.addOnFailureListener{ exception ->
            Toast.makeText(context, "Error $exception", Toast.LENGTH_SHORT).show()
        }

        // context?.let { Glide.with(it).load(myUrl).into(imageViewFotosPost) }
//para abrir el botón del chat

        fab_chat_button.setOnClickListener { findNavController().navigate(R.id.ChatDialogFragment)}
        btnAgregarFotos.setOnClickListener { findNavController().navigate(R.id.camFragment)
        }
    }

    private fun setUpPostDB() {

        //crea la coleccion en firestore o sii ya está va a añadir en ella
        postDBRef = store.collection("post")

    }

    private fun setUpCurrentUser() {
        currentUser = mAuth.currentUser!!

    }


    private fun setUpPublicarBoton() {
        val userRef=FirebaseFirestore.getInstance().collection("users")
        // vamos a coger el mensaje, ver que no está vacio y guardarlo
        _view.btnPublicar.setOnClickListener {
//creamos variables y guardamos en ellas cada valor extraido de los datos ingresados por el usuario en los edit text
            val tituloPost = _view.tvTituloPublicacionn.text.toString()
            val descripcionPost = _view.tvDescripcionPublicacion.text.toString()

            //si no ha colocado los datos requeridos vamos a crear un dato post de tipo Post
            if (tituloPost.isNotEmpty() && descripcionPost.isNotEmpty()) {

                var fotos=myUrl

                val userRef=FirebaseFirestore.getInstance().collection("users").document(currentUser.uid)
                userRef.get().addOnSuccessListener {
                    photo=it.get("photo").toString()

                    var post = Post(descripcionPost, Date(), tituloPost, fotos, FirebaseAuth.getInstance().currentUser!!.uid,"","")

                    //guardamos el mensaje (con savePost funcion que veran enseguida )en firebase y luego queremos que el edittext quede limpio, entonces=
                    if (post != null) {
                        savePost(post)
                    }

                }

                tvTituloPublicacionn.setText("")
                tvDescripcionPublicacion.setText("")
                Constants.imageUri= arrayListOf()
                //context?.let { it1 -> Glide.with(it1).load(Constants.imageUri).into(imageViewFotosPost) }
                //si el usuario tiene foto tome el url de la foto y si no que guarde un string vacio

            }
        }
    }

    private fun savePost(post: Post) {
        // para guardar el mensaje
        //el hashMap es un tipo de lista, el cual se usa y es el que espera el firestore
        //entonces los valores String y Any representan el nombre del campo y el tipo de valor que se agrega en la consola (lo que se ve desde el firebase)
        val newPost = HashMap<String, Any>()
        newPost["postDescription"] = post.getPostDescription()
        newPost["date"] = post.getDate()
        newPost["title"] = post.getTitle().toLowerCase()
        newPost["listFotos"]=post.getListFotos()
        newPost["userId"]=post.getUserId()

//el add es de tipo mutable <string,any>
        //acá agregamos el mensaje a la colección
        postDBRef.add(newPost).addOnCompleteListener {
            //si llega acá quiere decir que el mensaje se ha guardado correctamente
            Toast.makeText(activity, "Publicado con éxito", Toast.LENGTH_LONG).show()

        }.addOnFailureListener {
            // en caso de que falle
            Toast.makeText(activity, "error, intente de nuevo", Toast.LENGTH_LONG).show()
        }
    }
}

