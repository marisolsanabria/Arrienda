
package com.example.arrrienda3.ui.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.example.arrrienda3.R
import com.example.arrrienda3.models.Constants
import com.example.arrrienda3.ui.Fragments.CrearpublicacionFragment
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storageMetadata
import kotlinx.android.synthetic.main.activity_camara.*
import kotlinx.android.synthetic.main.fragment_crearpublicacion.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class camaraActivity : AppCompatActivity() {

    private val REQUEST_PERMISSION_CAMERA = 100
    private val REQUEST_IMAGE_CAMERA = 101
    private val REQUEST_PERMISSION_GALLERY = 102
    private val REQUEST_IMAGE_GALLERY = 103



    var currentPhotoPath: String? = null
    private var myUrl=""
    private var imageUri:Uri?=null
    private var storagePostPicRef: StorageReference?=null
    private var mArrayUri= arrayListOf<Uri>()
    private var mArrayUriString= arrayListOf<String>()
    private var mAuth=FirebaseAuth.getInstance()
   private lateinit var imageFile:File


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camara)

       // val fragment=CrearpublicacionFragment()
        //supportFragmentManager.beginTransaction().replace(R.id.fragment_container_view_tag,fragment).commit()

        val currentUser= FirebaseAuth.getInstance().currentUser?.uid

        storagePostPicRef=FirebaseStorage.getInstance().reference.child("Fotos post").child(currentUser.toString())

        btnCamara.setOnClickListener(View.OnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    gotoCamera()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.CAMERA),
                        REQUEST_PERMISSION_CAMERA
                    )
                }
            } else {
                gotoCamera()
            }


        })


        btnAbrirGaleria.setOnClickListener(View.OnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    gotoGallery2()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_PERMISSION_GALLERY
                    )
                }

            } else {
                gotoGallery2()
            }
        })

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION_CAMERA) {
            //Abrimos el cuadro de dialogo para permisos
            if (permissions.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                gotoCamera()
            } else {
                Toast.makeText(
                    this,
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
                    this,
                    "No podemos acceder a la galeria si el permiso no ha sido aceptado",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        var p = (Math.random() * 25 + 1).toInt()
        var s = (Math.random() * 25 + 1).toInt()
        var t = (Math.random() * 25 + 1).toInt()
        var c = (Math.random() * 25 + 1).toInt()
        var numero1 = (Math.random() * 1012 + 2111).toInt()
        var numero2 = (Math.random() * 1012 + 2111).toInt()


        var elementos = arrayOf(
            "a",
            "b",
            "c",
            "d",
            "e",
            "f",
            "g",
            "h",
            "i",
            "j",
            "k",
            "l",
            "m",
            "n",
            "o",
            "p",
            "q",
            "r",
            "s",
            "t",
            "u",
            "v",
            "w",
            "x",
            "y",
            "z"
        )
        val aleatorio = elementos[p] + elementos[s] + numero1 + elementos[t] + elementos[c] + numero2 + ".jpg"



        if (requestCode == REQUEST_IMAGE_CAMERA) {
            if (resultCode == RESULT_OK) {
                imageViewFoto!!.setImageURI(Uri.parse(currentPhotoPath))
            } else {
                Toast.makeText(this, "foto no fue tomada con exito", Toast.LENGTH_LONG).show()
            }
        }

        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            imageUri= data!!.data
            if (imageUri==null){
                Toast.makeText(this, "no has seleccionado una imagen", Toast.LENGTH_LONG).show()
            }else   {
                val fileRef= storagePostPicRef!!.child(aleatorio)
               // val fileRef= imageUri?.lastPathSegment?.let { storagePostPicRef!!.child(it) }
                var uploadTask: StorageTask<*>
                uploadTask=fileRef.putFile(imageUri!!)
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }

                    Toast.makeText(this, "CARGADA PORFIN $myUrl", Toast.LENGTH_LONG).show()
                    var metadata= storageMetadata {
                        setCustomMetadata("user",FirebaseAuth.getInstance().currentUser?.uid)
                    }
                    fileRef.updateMetadata(metadata)

                    return@Continuation fileRef.downloadUrl


                }).addOnCompleteListener(OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful) {

                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()


                        goToCrearPublicación()

                    }
                })
            }





        } //fin del if para galeria
    }//fin del on Activity result

    private fun goToCrearPublicación() {
        //Constants.imageUri=myUrl



        val transaccion=supportFragmentManager.beginTransaction()
        val fragmento=CrearpublicacionFragment()
        transaccion.replace(R.id.container,fragmento)
        transaccion.commit()


    }



    private fun gotoGallery2() {
        val GalleryIntent = Intent(Intent.ACTION_GET_CONTENT)

       // GalleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        GalleryIntent.setType("image/*")
        startActivityForResult(GalleryIntent, REQUEST_IMAGE_GALLERY)
    }

    private fun gotoCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //si nuestro dispositivo no tiene camara
        if (cameraIntent.resolveActivity(packageManager) != null) {
            //    startActivityForResult(cameraIntent, REQUEST_IMAGE_CAMERA);
            //agregamos un archivo donde guardamos la foto que tomaremos con la camara
            var photoFile: File? = null

            //le damos un try catch a esta linea acá NO podemos poner el throw IOException
            try {
                photoFile = createFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (photoFile != null) {
                val photoUri = FileProvider.getUriForFile(this, "com.example.arrrienda3", photoFile)
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAMERA)
            }
        }
    }


    private fun createFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HH-mm-ss", Locale.getDefault()).format(Date())
        val imgFileName = "IMG_" + timeStamp + "_"
        //creamos un almacenamiento dentro del directorio, dentro del directorio va a haber un subdirectorio (enviroment)
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        //creamos un archivo temporal que recibirá un prefijo que es el imgFilename un sufijo que puede cer Mp4 Png, jpg y un directorio( el que acabamos de crear)
        val image = File.createTempFile(imgFileName, ".jpg", storageDir)
        // como arriba esto está igualado a null debemos hacer un try catch seleccionamos el File image y damos windows + ctrl + alt + T y try catch o simplemente luego de la declaracion en la funcion throws IOException{

        // con esto ya tenemos toda nuestra ruta completa
        currentPhotoPath = image.absolutePath
        return image
    }


    /* private fun goToGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
    }

     */


     */
}

