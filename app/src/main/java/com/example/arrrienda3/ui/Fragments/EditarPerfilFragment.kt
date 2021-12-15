package com.example.arrrienda3.ui.Fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.arrrienda3.R
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_editar_perfil.*
import kotlinx.android.synthetic.main.fragment_editar_perfil.btnGuardarCambios
import kotlinx.android.synthetic.main.fragment_editar_perfil.fab_cambiarFoto
import kotlinx.android.synthetic.main.fragment_editar_perfil.view.*
import kotlinx.android.synthetic.main.fragment_perfil.*
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class EditarPerfilFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var currentUser: FirebaseUser
    private var cheker=""
    private var imageUri: Uri?= null
    private lateinit var _view: View
    private lateinit var userDBRef: CollectionReference
    private val store: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var storageProfilePicRef: StorageReference? = null
    private var myUrl = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        _view= inflater.inflate(R.layout.fragment_editar_perfil, container, false)

        return _view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnGuardarCambios.setOnClickListener { guardarCambiosPerfil() }
        cambiarFotoDePerfil()
    }



    private fun cambiarFotoDePerfil(){

        cheker="clicked"
        fab_cambiarFoto.setOnClickListener{
            // CropImage.startPickImageActivity(MainActivity())
            context?.let { it1 -> CropImage.activity().setAspectRatio(1,1).start(it1,this) }
            // CropImage.activity().setAspectRatio(1,1).start(this.@Main)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode== Activity.RESULT_OK && data!=null){
            val result=CropImage.getActivityResult(data)
            imageUri=result.uri
            //ivProfilePhoto.setImageURI(imageUri)
            context?.let { Glide.with(it).load(imageUri).circleCrop().into(_view.ivProfilePhotoEditar)}
        }
    }


    private fun guardarCambiosPerfil() {

            val prog = ProgressDialog(context)
            prog.setTitle("cargando")
            prog.setMessage("espera por favor")
            prog.setCanceledOnTouchOutside(false)
            prog.show()

            if (imageUri == null) {
                Toast.makeText(context, "no has seleccionado una imagen", Toast.LENGTH_LONG).show()
            } else {
                val fileRef = storageProfilePicRef!!.child(currentUser.uid + ".jpg")
                var uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    prog.dismiss()
                    return@Continuation fileRef.downloadUrl

                }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful) {
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseFirestore.getInstance().collection("users").document(currentUser.uid)

                        val userMap = HashMap<String, Any>()
                        userMap.put("photo", myUrl)

                        ref.update(userMap)

                        Toast.makeText(context, "actualización exitosa", Toast.LENGTH_SHORT).show()


                    }
                })
            }

    }








}