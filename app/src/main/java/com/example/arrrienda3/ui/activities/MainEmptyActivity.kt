package com.example.arrrienda3.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.arrrienda3.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainEmptyActivity : AppCompatActivity() {

    private val mAuth:FirebaseAuth= FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       if (mAuth.currentUser != null){
           val currentUser= mAuth.currentUser
           val refUser= FirebaseFirestore.getInstance().collection("users").document(currentUser!!.uid)
           refUser.get().addOnSuccessListener {

               val phone= it.get("phone").toString()

               if (phone=="" || phone== "null"){
                   goToInfoActivity()
               }else    {
                   goToMainActivity()
               }
           }

        }else{
            goToLogInActivity()
        }
        finish()


    }
    private fun goToInfoActivity() {

        val intent = Intent(this, InfoActivity::class.java)
        //parqa que este activity no se quede guardado en el historial y al darle atrás no vuelva a este
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun goToMainActivity(){
        val intent=Intent(this,MainActivity::class.java)
        startActivity(intent)
        intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    private fun goToLogInActivity(){
        val intent=Intent(this,logInActivity::class.java)
        startActivity(intent)
        intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
}