package com.example.arrrienda3.ui.Fragments

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.arrrienda3.MainActivity
import com.example.arrrienda3.R
import com.example.arrrienda3.adapters.fotosAdapter
import com.example.arrrienda3.adapters.onItemFotoClickLitener
import com.example.arrrienda3.models.Constants
import com.example.arrrienda3.models.Post
import com.example.arrrienda3.ui.activities.FotoActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_crearpublicacion.*
import kotlinx.android.synthetic.main.fragment_publicacion_dialog.*
import kotlinx.android.synthetic.main.fragment_publicacion_dialog.ivPhotoProfilePublicacion
import kotlinx.android.synthetic.main.fragment_publicacion_dialog.tvDescripcionPublicacion
import kotlinx.android.synthetic.main.fragment_publicacion_dialog.tvNombrePublicacion
import kotlinx.android.synthetic.main.fragment_publicacion_dialog.tvTituloPublicacionn
import kotlinx.android.synthetic.main.fragment_publicacion_dialog.view.*
import kotlinx.android.synthetic.main.fragment_publicaciones.view.*
import java.io.BufferedInputStream
import java.net.URL
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PublicacionDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PublicacionDialogFragment : Fragment(), onItemFotoClickLitener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var _view:View

    private var position = 0
    private var listt: MutableList<String> = arrayListOf()
    private lateinit var adapter: fotosAdapter

    private var listFotos:List<String> = listOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _view= inflater.inflate(R.layout.fragment_publicacion_dialog, container, false)








        return _view





    } // fin del onCreate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

      /*  var urlConection=URL("https://firebasestorage.googleapis.com/v0/b/arrienda3.appspot.com/o/fotosPost%2Fby58zpUfetSucviBjXPTrEn8jGO2%2Fwi2876ux2870.jpg?alt=media&token=81da5c9e-da59-4e5e-973d-47eed263ed50").openConnection()
        urlConection.connect()
        var inputStream=urlConection.getInputStream()
        var bis= BufferedInputStream(inputStream,8192)
        var bm=BitmapFactory.decodeStream(bis)

       */

        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/arrienda3.appspot.com/o/fotosPost%2Fby58zpUfetSucviBjXPTrEn8jGO2%2Fwi2876ux2870.jpg?alt=media&token=81da5c9e-da59-4e5e-973d-47eed263ed50").into(IVVideoViewPublicacion)




       // videoViewPublicacion.background=BitmapDrawable("https://firebasestorage.googleapis.com/v0/b/arrienda3.appspot.com/o/fotosPost%2Fby58zpUfetSucviBjXPTrEn8jGO2%2Fwi2876ux2870.jpg?alt=media&token=81da5c9e-da59-4e5e-973d-47eed263ed50")


        tvNombrePublicacion.setOnClickListener {
             
                    val intent=Intent(context, playVideoActivity::class.java)
                    startActivity(intent)



           /* val path= "android.resource://com.example.arrrienda3.ui.Fragments/R.raw.iphone_video"
            val uriii= Uri.parse("https://firebasestorage.googleapis.com/v0/b/arrienda3.appspot.com/o/videoPost%2Flp2234mz3047.mp4?alt=media&token=152aea4b-e9de-435a-bbc4-c43f12664823")


            val mediaController=MediaController(context)
            mediaController.setAnchorView(videoViewPublicacion)
            videoViewPublicacion.setMediaController(mediaController)
            videoViewPublicacion.setVideoURI(uriii)
            IVVideoViewPublicacion.isVisible=false
            videoViewPublicacion.requestFocus()
            videoViewPublicacion.start()


            videoViewPublicacion.setOnCompletionListener {
                Toast.makeText(context,"video completo", Toast.LENGTH_LONG).show()
            }

            videoViewPublicacion.setOnCompletionListener {
                Toast.makeText(context,"error en video", Toast.LENGTH_LONG).show()
            }

            */

        }





       /* val url="https://firebasestorage.googleapis.com/v0/b/arrienda3.appspot.com/o/videoPost%2Flp2234mz3047.mp4?alt=media&token=152aea4b-e9de-435a-bbc4-c43f12664823"

        var mediaPlayer=MediaPlayer().apply {

        }

        */




        val pd=ProgressDialog(context)
        pd.setMessage("Buffering...")
        pd.setCanceledOnTouchOutside(true)

        FirebaseFirestore.getInstance().collection("post").document(Constants.postId.toString()).get().addOnSuccessListener { document->

            if (document !=null) {
                val post = document.toObject(Post::class.java)
                val fotos= document.get("listFotos")
                //.toString().split("\\s*,\\s*")
                if (fotos==null){
                    Toast.makeText(context,"fotos es nulo",Toast.LENGTH_SHORT).show()
                }


                //   context?.let { it1 -> Glide.with(it1).load(fotos[1]).into(isFotosPostPublicacion) }

                // Toast.makeText(context,fotos[1],Toast.LENGTH_SHORT).show()



                if (post != null) {

                    tvTituloPublicacionn.text = post.getTitle()
                    tvDescripcionPublicacion.text = post.getPostDescription()

                    var userPostId=post.getUserId()
                    var infoUserPost=FirebaseFirestore.getInstance().collection("users").document(userPostId)
                    infoUserPost.get().addOnSuccessListener {
                        tvNombrePublicacion.text = it.get("name").toString()
                        if (it.get("photo") != null && it.get("photo") != "" && it.get("photo")!= "null"){
                            context?.let { it1 -> Glide.with(it1).load(it.get("photo")).into(ivPhotoProfilePublicacion) }
                        }else {
                            context?.let { it1 -> Glide.with(it1).load("https://firebasestorage.googleapis.com/v0/b/arrienda3.appspot.com/o/sin%20foto.png?alt=media&token=6152a0c8-7ed2-47ae-b558-27944640cab9").into(ivPhotoProfilePublicacion) }
                        }
                    }



                /*    if  (post.getVideo() != null && post.getVideo() != ""){


                        pd.show()

                        val mediaController= MediaController(context)
                        mediaController.setAnchorView(videoViewPublicacion)
                        mediaController.setMediaPlayer(videoViewPublicacion)
                        videoViewPublicacion.setMediaController(mediaController)
                        videoViewPublicacion.setVideoURI(Uri.parse(post.getVideo()))
                        videoViewPublicacion.start()

                        Toast.makeText(context,post.getVideo(),Toast.LENGTH_LONG).show()
                    }

                    videoViewPublicacion.setOnPreparedListener {
                        pd.dismiss()

                    }

                 */




                    listFotos = post.getListFotos()

                    if ( listFotos != null) {

                        setUpRecyclerView()
                    }// fin del coso de las fotos



                  /*  videoViewPublicacion.setOnClickListener {
                        if (post.getVideo()!=null && post.getVideo() != ""){
                            val intent=Intent(context, playVideoActivity::class.java)
                            intent.putExtra("video",post.getVideo())
                            startActivity(intent)
                        }
                    }

                   */


                }
            }


        }// fin del on success


    }


    private fun setUpRecyclerView() {

        listFotos.forEach {


            adapter= fotosAdapter(listFotos,this)
            val layoutManayer = LinearLayoutManager(context)
            layoutManayer.orientation= LinearLayoutManager.HORIZONTAL

            _view.rvPublicacionDialog.setHasFixedSize(true)
            _view.rvPublicacionDialog.layoutManager = layoutManayer
           // _view.isNestedScrollingEnabled = true
            _view.rvPublicacionDialog.itemAnimator = DefaultItemAnimator()
            _view.rvPublicacionDialog.adapter = adapter

        }
    }

    override fun onItemFClick(item: String, position: Int) {
        val intent=Intent(context, FotoActivity::class.java).apply {
            putExtra("posicion",position)
            putStringArrayListExtra("listFotos",listFotos as ArrayList<String>)
        }
        startActivity(intent)
        super.onItemFClick(item, position)
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PublicacionDialogFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PublicacionDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}