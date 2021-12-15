package com.example.arrrienda3.ui.Fragments

import android.Manifest
import android.R.attr
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.provider.SyncStateContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.arrrienda3.R
import com.example.arrrienda3.models.Constants
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_camara.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [camFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class camFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    private val REQUEST_PERMISSION_CAMERA = 100
    private val REQUEST_IMAGE_CAMERA = 101
    private val REQUEST_PERMISSION_GALLERY = 102
    private val REQUEST_IMAGE_GALLERY = 103


    var currentPhotoPath: String? = null
    private var myUrl = ""
    private var imageUri: Uri? = null
    private var storagePostPicRef: StorageReference? = null
    private var mArrayUri = arrayListOf<Uri>()
    private var mArrayUriString = arrayListOf<String>()
    private var mAuth = FirebaseAuth.getInstance()
    private lateinit var imageFile: File

    private lateinit var mDialog:ProgressDialog

    var UriList= arrayListOf<Uri>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cam, container, false)

        // val fragment=CrearpublicacionFragment()
        //supportFragmentManager.beginTransaction().replace(R.id.fragment_container_view_tag,fragment).commit()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storagePostPicRef = FirebaseStorage.getInstance().reference.child("fotosPost").child(FirebaseAuth.getInstance().currentUser?.uid.toString())

        btnCamara.setOnClickListener(View.OnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context?.let { it1 -> ActivityCompat.checkSelfPermission(it1, Manifest.permission.CAMERA) } == PackageManager.PERMISSION_GRANTED) {
                    gotoCamera()
                } else {
                    activity?.let { it1 -> ActivityCompat.requestPermissions(it1, arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION_CAMERA) }
                }
            } else {
                gotoCamera()

            }


        })


        btnAbrirGaleria.setOnClickListener(View.OnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context?.let { it1 ->
                            ActivityCompat.checkSelfPermission(
                                    it1, Manifest.permission.READ_EXTERNAL_STORAGE
                            )
                        } == PackageManager.PERMISSION_GRANTED
                ) {
                    gotoGallery2()
                } else {
                    activity?.let { it1 ->
                        ActivityCompat.requestPermissions(
                                it1,
                                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_PERMISSION_GALLERY)
                    }
                }

            } else {
                gotoGallery2()
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment camFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                camFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            //Abrimos el cuadro de dialogo para permisos
            if (permissions.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                gotoCamera()
            } else {
                Toast.makeText(
                        context,
                        "No podemos acceder a la camara si el permiso no ha sido aceptado",
                        Toast.LENGTH_LONG
                ).show()
            }
        }
        if (requestCode == REQUEST_PERMISSION_GALLERY) {
            //Abrimos el cuadro de dialogo para permisos
            if (permissions.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                gotoGallery2()
            } else {
                Toast.makeText(
                        context,
                        "No podemos acceder a la galeria si el permiso no ha sido aceptado",
                        Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)



        if (requestCode == REQUEST_IMAGE_CAMERA) {
            if (resultCode == AppCompatActivity.RESULT_OK) {

                val bitmap= data?.extras?.get("data") as Bitmap


                if (bitmap==null){
                    Toast.makeText(context, "no has tomado una foto", Toast.LENGTH_LONG).show()
                }else{
                    var p = (Math.random() * 25 + 1).toInt()
                    var s = (Math.random() * 25 + 1).toInt()
                    var t = (Math.random() * 25 + 1).toInt()
                    var c = (Math.random() * 25 + 1).toInt()
                    var numero1 = (Math.random() * 1012 + 2111).toInt()
                    var numero2 = (Math.random() * 1012 + 2111).toInt()


                    var elementos = arrayOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z")
                    val aleatorio = elementos[p] + elementos[s] + numero1 + elementos[t] + elementos[c] + numero2 + ".jpg"

                    val baos=ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data=baos.toByteArray()

                    val fileRef = storagePostPicRef!!.child(aleatorio)

                    var uploadTask: StorageTask<*>
                    uploadTask = fileRef.putBytes(data)
                    uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }

                        Toast.makeText(context, "FOTO CARGADA PORFIN $myUrl", Toast.LENGTH_LONG).show()
                        return@Continuation fileRef.downloadUrl


                    }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                        if (task.isSuccessful) {

                            val downloadUrl = task.result
                            myUrl = downloadUrl.toString()


                            goToCrearPublicación()

                        }
                    })
                }

            }
        }//fin del if de la cámara

        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == AppCompatActivity.RESULT_OK) {
            progressDialog()
            if (data?.clipData !=null){
                var count= data.clipData!!.itemCount
                for (i in 0..count - 1) {

                    var p = (Math.random() * 25 + 1).toInt()
                    var s = (Math.random() * 25 + 1).toInt()
                    var t = (Math.random() * 25 + 1).toInt()
                    var c = (Math.random() * 25 + 1).toInt()
                    var numero1 = (Math.random() * 1012 + 2111).toInt()
                    var numero2 = (Math.random() * 1012 + 2111).toInt()


                    var elementos = arrayOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z")
                    val aleatorio = elementos[p] + elementos[s] + numero1 + elementos[t] + elementos[c] + numero2 + ".jpg"

                    var imageUri: Uri = data.clipData!!.getItemAt(i).uri
                    val fileRef = storagePostPicRef!!.child(aleatorio)
                    var uploadTask: StorageTask<*>
                    uploadTask = fileRef.putFile(imageUri)
                    uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                        if (!task.isSuccessful) {
                            task.exception?.let {
                                throw it
                            }
                        }
                        mDialog.dismiss()
                        Toast.makeText(context, "FOTO CARGADA PORFIN $myUrl", Toast.LENGTH_LONG).show()
                        return@Continuation fileRef.downloadUrl


                    }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                        if (task.isSuccessful) {

                            val downloadUrl = task.result
                            myUrl = downloadUrl.toString()

                            mArrayUriString.add(myUrl)

                        }
                    })

                }

                goToCrearPublicación()
            }else if (data?.getData() != null){

                var p = (Math.random() * 25 + 1).toInt()
                var s = (Math.random() * 25 + 1).toInt()
                var t = (Math.random() * 25 + 1).toInt()
                var c = (Math.random() * 25 + 1).toInt()
                var numero1 = (Math.random() * 1012 + 2111).toInt()
                var numero2 = (Math.random() * 1012 + 2111).toInt()


                var elementos = arrayOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z")
                val aleatorio = elementos[p] + elementos[s] + numero1 + elementos[t] + elementos[c] + numero2 + ".jpg"

                progressDialog()
                val imageUri: Uri = data.data!!
                val fileRef = storagePostPicRef!!.child(aleatorio)
                val uploadTask: StorageTask<*>
                uploadTask = fileRef.putFile(imageUri)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }

                    Toast.makeText(context, "FOTO CARGADA PORFIN $myUrl", Toast.LENGTH_LONG).show()
                    return@Continuation fileRef.downloadUrl


                }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful) {

                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        mArrayUriString.add(myUrl)
                        mDialog.dismiss()

                    }
                })

                goToCrearPublicación()

            }




        } //fin del if para galeria
    }//fin del on Activity result

    private fun progressDialog() {
        mDialog = ProgressDialog(context)
        mDialog.setTitle("cargando")
        mDialog.setMessage("espera por favor")
        mDialog.setCanceledOnTouchOutside(false)
        mDialog.show()
    }

    private fun goToCrearPublicación() {
        Constants.imageUri=mArrayUriString
        findNavController().navigate(R.id.bnvpublicar)


    }

    private fun gotoGallery2() {

       val GalleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        GalleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        GalleryIntent.setType("image/*")
        startActivityForResult(GalleryIntent, REQUEST_IMAGE_GALLERY)


    }

    private fun gotoCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //si nuestro dispositivo no tiene camara
        if (activity?.let { cameraIntent.resolveActivity(it.packageManager) } != null) {
            //    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAMERA);
            //agregamos un archivo donde guardamos la foto qu
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAMERA)
        }
    }
}






