package com.example.arrrienda3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.arrrienda3.ui.Fragments.CrearpublicacionFragment
import com.example.arrrienda3.ui.activities.InfoActivity
import com.example.arrrienda3.ui.activities.logInActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

      /*  val currentUser=FirebaseAuth.getInstance().currentUser

        if (currentUser !=null){
            val refUser= FirebaseFirestore.getInstance().collection("users").document(currentUser.uid)
            refUser.get().addOnSuccessListener {

                val phone= it.get("phone").toString()

                if (phone=="" || phone== "null"){
                    goToInfoActivity()
                }
            }
        }

       */


     /*  if (FirebaseAuth.getInstance().currentUser != null){
            val currentUserUID= FirebaseAuth.getInstance().currentUser!!.uid

            val userRef=FirebaseFirestore.getInstance().collection("users").document(currentUserUID)

            userRef.get().addOnSuccessListener {
            val name=it.get("name").toString()
            val number=it.get("number").toString()
                if (name!=null && name!="" && number!= null && number!=""){
                }else{
                    goToInfoActivity()
                }
            }

        }

      */







        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        findViewById<BottomNavigationView>(R.id.bnvMenu).setupWithNavController(navController)



    }

    private fun goToInfoActivity() {

        val intent = Intent(this, InfoActivity::class.java)
        //parqa que este activity no se quede guardado en el historial y al darle atrás no vuelva a este
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }



}