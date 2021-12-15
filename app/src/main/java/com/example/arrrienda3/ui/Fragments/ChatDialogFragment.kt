package com.example.arrrienda3.ui.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.arrrienda3.R
import com.example.arrrienda3.adapters.ChatAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.fragment_chat_dialog.*
import kotlinx.android.synthetic.main.fragment_chat_dialog.view.*
import java.util.*
import kotlin.collections.ArrayList
import com.example.arrrienda3.models.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.android.synthetic.main.fragment_perfil.*
import kotlin.collections.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ChatDialogFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    private lateinit var _view:View

    private lateinit var adapter:ChatAdapter
    private val messageList:ArrayList<Message> = ArrayList()

    private val  mAuth:FirebaseAuth= FirebaseAuth.getInstance()
    private lateinit var currentUser:FirebaseUser
    var refUsers:DatabaseReference?=null

    private val store:FirebaseFirestore= FirebaseFirestore.getInstance()
    private lateinit var chatDBRef:CollectionReference

    private var chatSuscription: ListenerRegistration?=null
    private var photo:String=""

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PerfilFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                PerfilFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //para que al abrir el reclado el contenido no se corra hacia arriba
        getActivity()?.getWindow()?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        // Inflate the layout for this fragment

        //usaremos este view porque vamos a necesitar referenciarlo mas adelante
        _view= inflater.inflate(R.layout.fragment_chat_dialog, container, false)


        //para configurar la coneccion de la base de datos para el chat
        setUpChatDB()
        // el current user que ya sabemos obtener
        setUpCurrentUser()
        //acá pondremos el adaptador y la lista de datos que le vamos a pasar
        setUpRecyclerView()
        //lo que sucederá al darle click al botón
        setUpChatBtn()
        //llamamos al metodo que nos actualizará los cambios en tiempo real
        subscribeToChatMessages()



     /*   refUsers!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
               if (p0.exists()){
                   val user:Users?=p0.getValue(Users::class.java)

               }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

      */

        //esto es porque habiamos borrado arriba el onflate del layout y como necesitamos referenciarlo lo metimos en una variable y lo pusimos acá
        return _view
    }


    private fun  setUpChatDB(){

        //crea la coleccion en firestore o sii ya está va a añadir en ella
        chatDBRef=store.collection("chat")

    }
    private fun  setUpCurrentUser(){
        currentUser=mAuth.currentUser!!

    }
    private fun  setUpRecyclerView(){
        val layoutManayer=LinearLayoutManager(context)
        adapter= ChatAdapter(messageList,currentUser.uid)

        _view.recyclerViewChat.setHasFixedSize(true)
        _view.recyclerViewChat.layoutManager= layoutManayer
        _view.recyclerViewChat.itemAnimator= DefaultItemAnimator()
        _view.recyclerViewChat.adapter=adapter

    }
    private fun  setUpChatBtn(){
        val userRef=FirebaseFirestore.getInstance().collection("users")
        // vamos a coger el mensaje, ver que no está vacio y guardarlo
        _view.buttonSend.setOnClickListener {

            val messageText = _view.editTextMessage.text.toString()
            if (messageText.isNotEmpty()) {
                //si el usuario tiene foto tome el url de la foto y si no que guarde un string vacio
                userRef.document(currentUser.uid).get().addOnSuccessListener {
                    photo=it.get("photo").toString()
                }
               // val photo=currentUser.photoUrl?.let { currentUser.photoUrl.toString() } ?: run { "" }
                val message = Message(currentUser.uid, messageText, photo, Date())
                //guardamos el mensaje (con save message funcion que veran enseguida )en firebase y luego queremos que el edittext quede limpio, entonces=
                saveMessage(message)
                _view.editTextMessage.setText("")
            }
        }

    }
    private fun saveMessage(message: Message){

        // para guardar el mensaje
        //el hashMap es un tipo de lista, el cual se usa y es el que espera el firestore
        //entonces los valores String y Any representan el nombre del campo y el tipo de valor que se agrega en la consola (lo que se ve desde el firebase)
        val newMessage=HashMap<String,Any>()
        newMessage["authorId"]=message.authorId
        newMessage["message"]=message.message
        newMessage["profileImageURL"]=message.profileImageURL
        newMessage["sentAt"]=message.sentAt
//el add es de tipo mutable <string,any>
        //acá agregamos el mensaje a la colección
        chatDBRef.add(newMessage).addOnCompleteListener{
            //si llega acá quiere decir que el mensaje se ha guardado correctamente
            Toast.makeText(activity,"mensaje enviado",Toast.LENGTH_LONG).show()

        }.addOnFailureListener {
            // en caso de que falle
            Toast.makeText(activity,"error, intente de nuevo",Toast.LENGTH_LONG).show()
        }

    }
private fun subscribeToChatMessages(){

    //llamamos a chatDBRef y aplicamos addSnapshotListener y llamamos a un objeto eventListener de java y un segundo EvenListener DE FIRESTORE , e implementamos miembros
   chatSuscription= chatDBRef.orderBy("sentAt",Query.Direction.DESCENDING)
        .limit(1000)
        .addSnapshotListener(object :java.util.EventListener, EventListener<QuerySnapshot>{
        //esye metodo on event  es el que se va lanzar cada vez que alguien envie un mensaje, o lo borre, o lo edite
        override fun onEvent(snapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {

            //si exception es nula quiere decir que ha salido bien, y si no pues que ha habido un error, mostraremos un toast

            exception?.let {
                // si entra acá significa que no es nulo
                Toast.makeText(activity,"Exception",Toast.LENGTH_LONG).show()
                return
            }

            snapshot?.let {
                //primero borramos la lista de mensajes que tenemos para que no se duplique, porque acá nos va a llegar todos los documentos
                messageList.clear()
                //ahora vamos a rescatar los mensajes
                //del query snapshot vamos a pasarlo a objetos
                //acá nos botará un error debido a que nuestra clase message no tiene ningun contructor vacio, así que lo añadimos en la clase
                //por eso tenemos que cambiar la clase a esto:
               // data class Message(val authorId:String="", val message: String="", val profileImageURL:String="", val sentAt:Date=Date())
                val messages=it.toObjects(Message::class.java)
                // para que tengga orden por hora en que se envia el mensaje usamos el asReversed


                //vamos a pasarle los mensajes al messageList
                messageList.addAll(messages.asReversed())
                //ahora notificamos al asaptador de que ha habido un cambio

                adapter.notifyDataSetChanged()
                //para que los mensajes vayan subiendo conforme nosotros enviamos o recibimos mensajes
                //el messagelist.size me da mi ulrima posicion
                _view.recyclerViewChat.smoothScrollToPosition(messageList.size)

            }


        }

    })
}

    override fun onDestroy() {
        //si chatSubscription no es nulo llamo al remove
        chatSuscription?.remove()
        super.onDestroy()
    }

}