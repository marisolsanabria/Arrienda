package com.example.arrrienda3.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Toast
import com.example.arrrienda3.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_fotgot_password.*
import java.util.regex.Pattern
import java.util.regex.Pattern.compile

class fotgotPasswordActivity : AppCompatActivity() {

    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fotgot_password)


        btnSentEmail.setOnClickListener {
            val email = etForgotEmail.text.toString()

            // si el correo es valido entonces
            if (isValidateEmail(email)) {
                //enviamos el correo de resetear contraseña
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(this){ Task->
                    if (Task.isSuccessful){
                        // si se logró enviar el correo mostrará el siguiente mensaje y lo dirijirá al login
                        Toast.makeText(this, "se ha enviado un email a tu correo para reestablecer la contraseña", Toast.LENGTH_LONG).show()
                        goToLogIn()
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    }else{
                        // de lo contrario mostraremos el siguiente mensaje de error
                        val message=Task.exception!!.toString()
                        Toast.makeText(this,"Error: $message",Toast.LENGTH_SHORT).show()
                        mAuth.signOut()
                    }
                }
            } else {
                // si el email no es valido entonces mostramos el siguiente toast
                Toast.makeText(this, "dirección de correo inválida", Toast.LENGTH_LONG).show()
            }
        }

        btnGoBackLogIn.setOnClickListener {
            goToLogIn()
        }
    }

    private fun goToLogIn() {
        val intent = Intent(this, logInActivity::class.java)
        //parqa que este activity no se quede guardado en el historial y al darle atrás no vuelva a este
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun isValidateEmail(email:String):Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

}

