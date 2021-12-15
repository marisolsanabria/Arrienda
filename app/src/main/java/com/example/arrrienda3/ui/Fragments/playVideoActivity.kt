package com.example.arrrienda3.ui.Fragments

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.MediaController
import com.example.arrrienda3.R
import kotlinx.android.synthetic.main.activity_play_video.*
import kotlinx.android.synthetic.main.fragment_publicacion_dialog.*

class playVideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play_video)

       /* val mediaController= MediaController(this)
        mediaController.setAnchorView(FrameVideo)
        mediaController.setMediaPlayer(videoView2)
        videoView2.setMediaController(mediaController)
        val video=intent.getStringExtra("video")
        videoView2.setVideoURI(Uri.parse(video))
        videoView2.start()

        */
        val mediaController= MediaController(this)
        mediaController.setAnchorView(FrameVideo)
        mediaController.setMediaPlayer(videoView2)
        videoView2.setMediaController(mediaController)
        val video=intent.getStringExtra("video")
        videoView2.setVideoURI(Uri.parse("https://firebasestorage.googleapis.com/v0/b/arrienda3.appspot.com/o/videoPost%2Flp2234mz3047.mp4?alt=media&token=152aea4b-e9de-435a-bbc4-c43f12664823"))
        videoView2.start()
    }
}