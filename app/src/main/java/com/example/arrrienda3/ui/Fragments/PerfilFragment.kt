package com.example.arrrienda3.ui.Fragments


import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.arrrienda3.MainActivity
import com.example.arrrienda3.R
import com.example.arrrienda3.ui.activities.logInActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.data.model.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_camara.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.fragment_perfil.*
import kotlinx.android.synthetic.main.fragment_perfil.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class PerfilFragment : Fragment() {

    private lateinit var _view: View
    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser

    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var currentUser: FirebaseUser

    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var chatDBRef: CollectionReference

    private val userList: MutableList<User> = mutableListOf<User>()


    private var cheker = ""
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storageProfilePicRef: StorageReference? = null
    private lateinit var userDBRef: CollectionReference

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _view = inflater.inflate(R.layout.fragment_perfil, container, false)

        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("profilePictures")
        return _view


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab_chat_button.setOnClickListener { findNavController().navigate(R.id.selectChatFragment) }
        btnEditarPerfil.setOnClickListener { findNavController().navigate(R.id.editarPerfilFragment) }


        setUpChatDB()
        setUpCurrentUser()
        setUpCurrentUserInfoUI()
        cambiarFotoDePerfil()

        //cerrar sesión y que al hacerlo me lleve al Log in
        /*btnGuardarCambios.setOnClickListener {
           // FirebaseAuth.getInstance().signOut()
            activity?.let { it1 ->
                AuthUI.getInstance().signOut(it1).addOnSuccessListener {
                    Toast.makeText(activity, "Hasta pronto", Toast.LENGTH_SHORT).show()
                    goToLogIn()

                }.addOnFailureListener {
                    Toast.makeText(activity, "Ocurrio un error ${it.message}", Toast.LENGTH_SHORT).show()
                }
            }

        }

         */
    }

    private fun goToLogIn() {
        val intent = Intent(activity, logInActivity::class.java)
        activity?.startActivity(intent)
    }

    private fun setUpChatDB() {

        //crea la coleccion en firestore o sii ya está va a añadir en ella
        chatDBRef = store.collection("chat")

    }

    private fun setUpCurrentUser() {
        currentUser = mAuth.currentUser!!

    }

    @SuppressLint("ResourceAsColor")
    private fun setUpCurrentUserInfoUI() {
        //tomamos la información de nuestro usuario y la pondremos dentro de los elementos de nuestro layout

        /*  val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
          if (pref != null) {
              this.profileId = pref.getString("id", "none").toString()
          }

          if (profileId == firebaseUser.uid) {

          }
         */

        profileId = currentUser.uid

        val userRef = FirebaseFirestore.getInstance().collection("users")

        userRef.document(profileId).get().addOnSuccessListener {
            _view.tvName.text = it.get("name").toString()
        }.addOnFailureListener { exception ->
            Toast.makeText(context, "Error $exception", Toast.LENGTH_SHORT).show()
        }

        userRef.document(profileId).get().addOnSuccessListener {
            _view.tvEmail.text = it.get("email").toString()
        }.addOnFailureListener { exception ->
            Toast.makeText(context, "Error $exception", Toast.LENGTH_SHORT).show()
        }

        userRef.document(profileId).get().addOnSuccessListener {
            if (it.get("photo")!=null && it.get("photo") !=""){
                context?.let { it1 -> Glide.with(it1).load(it.get("photo").toString()).circleCrop().into(ivProfilePhotoEditar) }
            }else{
                context?.let { it1 -> Glide.with(it1).load("https://firebasestorage.googleapis.com/v0/b/arrienda3.appspot.com/o/sin%20foto.png?alt=media&token=6152a0c8-7ed2-47ae-b558-27944640cab9").circleCrop().into(ivProfilePhotoEditar) }
            }

        }.addOnFailureListener { exception ->
            Toast.makeText(context, "Error $exception", Toast.LENGTH_SHORT).show()
        }


        //  _view.tvEmail.text=currentUser.email
        //  _view.tvName.text=currentUser.displayName?.let{currentUser.displayName} ?: run {"sin nombre"}

        // currentUser.photoUrl?.let{
        //   activity?.let { it1 -> Glide.with(it1).load(currentUser.photoUrl).circleCrop().into(ivProfilePhoto) }
        // }
    }

    private fun cambiarFotoDePerfil() {

        cheker = "clicked"
        fab_cambiarFoto.setOnClickListener {
            // CropImage.startPickImageActivity(MainActivity())
            context?.let { it1 -> CropImage.activity().setAspectRatio(1, 1).start(it1, this) }
            // CropImage.activity().setAspectRatio(1,1).start(this.@Main)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            //ivProfilePhoto.setImageURI(imageUri)
            context?.let { Glide.with(it).load(imageUri).circleCrop().into(ivProfilePhotoEditar) }
            //vamos a obtener la url y guardarla en el usuario

        }
    }



}
