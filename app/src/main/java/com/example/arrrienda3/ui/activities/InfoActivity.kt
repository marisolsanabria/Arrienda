package com.example.arrrienda3.ui.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.example.arrrienda3.MainActivity
import com.example.arrrienda3.R
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_info.*
import kotlinx.android.synthetic.main.fragment_perfil.*

class InfoActivity : AppCompatActivity() {

    private var cheker = ""
    private var imageUriPhotoProfile:Uri?=null
    private var storageProfilePicRef: StorageReference? = null
    private var myUrl = ""
    private var name=""
    private var savename=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        storageProfilePicRef = FirebaseStorage.getInstance().reference.child("profilePictures")

        val currentUser= FirebaseAuth.getInstance().currentUser

        if (currentUser!=null){
            tvInfoCorreo.text=currentUser.email
            if (currentUser.photoUrl!=null){
               Picasso.get().load(currentUser.photoUrl).into(ivInfoFotoPerfil)
            }else{
                Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/arrienda3.appspot.com/o/profile%20default.png?alt=media&token=071099bf-47f8-4369-bb2f-e0637e4b3d8b").into(ivInfoFotoPerfil)
            }
        }

        //Ahora en el caso del nombre
        if (currentUser!=null){

            if (currentUser.displayName != null){
                val namee=currentUser.displayName
                etInfoNombre.hint=namee.toString()
            }
        }



        tvInfoCambiarFotoDePerfil.setOnClickListener { infoCambiarFotoDePerfil() }

        btnInfoGuardarCambios.setOnClickListener { guardarInfo() }
    }// fin del onCreate

    private fun guardarInfo() {

        val currentUser=FirebaseAuth.getInstance().currentUser

        if (etInfoNombre.text.isNotEmpty()){
            name=etInfoNombre.text.toString()
        }else{
            if (currentUser?.displayName!=null){
                name=currentUser.displayName.toString()
            }else{
                name=""

            }

        }



        if (etInfoCelular.text.isNotEmpty() && name !=null && name!= ""){
            val prog = ProgressDialog(this)
            prog.setTitle("cargando")
            prog.setMessage("espera por favor")
            prog.setCanceledOnTouchOutside(false)
            prog.show()

            if (currentUser !=null){
                val userRef= FirebaseFirestore.getInstance().collection("users").document(currentUser!!.uid)

                val userMap= HashMap<String,Any>()

               userMap.put("name",name)

                userMap.put("phone",etInfoCelular.text.toString())
             userMap.put("id",etInfoCC.text.toString())

                userRef.update(userMap)
                Toast.makeText(this,"actualización exitosa", Toast.LENGTH_SHORT).show()

                if (imageUriPhotoProfile!=null){
                    val fileRef = storageProfilePicRef!!.child(currentUser.uid + ".jpg")
                    var uploadTask: StorageTask<*>
                    uploadTask = fileRef.putFile(imageUriPhotoProfile!!)
                    uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        return@Continuation fileRef.downloadUrl

                    }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                        if (task.isSuccessful) {
                            val downloadUrl = task.result
                            myUrl = downloadUrl.toString()

                            val ref = FirebaseFirestore.getInstance().collection("users").document(currentUser.uid)

                            val userMap = HashMap<String, Any>()
                            userMap.put("photo", myUrl)

                            ref.update(userMap)


                        }
                    })

                }
                prog.dismiss()
                goToMainActivity()
            }
        }else{
            if (name==""){
                showAlrtNombre()
            }else{
                if (etInfoCelular.text.isEmpty()){
                    showAlertCelular()
                }
            }
        }
    }

    private fun goToMainActivity() {

        val intent = Intent(this, MainActivity::class.java)
        //parqa que este activity no se quede guardado en el historial y al darle atrás no vuelva a este
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

    }

    private fun showAlertCelular() {
        val builder=AlertDialog.Builder(this)
        builder.setMessage("porfavor ingrese un número de celular")
        builder.setPositiveButton("aceptar",null)
        val dialog:AlertDialog=builder.create()
        dialog.show()
    }

    private fun showAlrtNombre() {
        val builder= AlertDialog.Builder(this)
        builder.setMessage("porfavor ingrese un Nombre")
        builder.setPositiveButton("aceptar",null)
        val dialog: AlertDialog =builder.create()
        dialog.show()
    }

    private fun infoCambiarFotoDePerfil() {
        cheker = "clicked"
            // CropImage.startPickImageActivity(MainActivity())
            CropImage.activity().setAspectRatio(1, 1).start(this)
            // CropImage.activity().setAspectRatio(1,1).start(this.@Main)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = CropImage.getActivityResult(data)

            imageUriPhotoProfile=result.uri

            //ivProfilePhoto.setImageURI(imageUri)
            Glide.with(this).load(result.uri).circleCrop().into(ivInfoFotoPerfil)
            //vamos a obtener la url y guardarla en el usuario
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AuthUI.getInstance().signOut(this).addOnSuccessListener {
            goToLogInActivity()
        }

    }


    private fun goToLogInActivity() {
        val intent = Intent(this, logInActivity::class.java)
        //parqa que este activity no se quede guardado en el historial y al darle atrás no vuelva a este
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}