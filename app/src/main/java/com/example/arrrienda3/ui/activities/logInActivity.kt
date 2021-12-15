package com.example.arrrienda3.ui.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.arrrienda3.MainActivity
import com.example.arrrienda3.R
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_log_in.*
import java.util.regex.Pattern

class logInActivity : AppCompatActivity() {


    private val mAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    // para el on activity result debemos tener una constante que especifique la activity por la que buscamos respuesta
    private val RC_SIGN_IN = 99


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        //BOTÓN CREAR CUENTA
        btnSingIn.setOnClickListener {
            goToSignUp()
        }

        //botón log in con correo, iniciar sesion
        btnLogInEmail.setOnClickListener {
            logIn()
        }

        //text view forgot password
        tvOlvideContraseña.setOnClickListener {
            goToForgotPassword()
        }

        // botón iniciar sesión con gmail
        btnLogInGmail.setOnClickListener {
            googleLogIn()
        }

        //botón iniciar sesión con facebook
        btnLogInFacebook.setOnClickListener {
            //falta programar esto
        }
    }


    private fun goToSignUp() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }


    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        //parqa que este activity no se quede guardado en el historial y al darle atrás no vuelva a este
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun goToInfoActivity() {
        val intent = Intent(this, InfoActivity::class.java)
        //parqa que este activity no se quede guardado en el historial y al darle atrás no vuelva a este
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
    private fun goToForgotPassword() {
        val intent = Intent(this, fotgotPasswordActivity::class.java)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }


    // la función de iniciar sesión con la cuenta creada directamente
    private fun logIn() {
        // si los dos edittext no estan vacios entonces continuamos
        if (etLogInEmail.text.isNotEmpty() && etLogInPassword.text.isNotEmpty()) {

            // si la longitud de la contraseña es menor a 8 mostramos el siguiente error en el edit text
            if (etLogInPassword.length() < 8) {
                etLogInPassword.error = "la contraseña debe tener un mínimo de 8 caracteres"
            } else {
                // si la contraseña tiene el numero de caracteres minimos requeridos entonces seguimos haciendo el sign in
                mAuth.signInWithEmailAndPassword(
                        etLogInEmail.text.toString(),
                        etLogInPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        // si el sign in se hizo de manera exitosa vamos a ver si el correo ha sido verificado
                        if (mAuth.currentUser!!.isEmailVerified) {
                            // si ha sido verificado vamos al MainActivity
                            Toast.makeText(this, "usuario verificado", Toast.LENGTH_LONG).show()

                            goToInfoOrMainActiviTy()
                        } else {
                            // si no ha sido verificado mostrará el siguiente toast para que lo verifique
                            Toast.makeText(this, "Verifique usuario en el correo electronico que ha sido enviado", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        // si hay un error en el sign in mostraremos el error del firebase en un toast
                        val message = it.exception!!.toString()
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                        mAuth.signOut()
                    }
                }
            }
        } else {
            // si están vacias las casillas mostraremos el siguiente toast
            Toast.makeText(this, "ingrese email y contraseña", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goToInfoOrMainActiviTy() {

        val currentUserId = mAuth.currentUser!!.uid
        // creamos la colección en el firestore
        val usersRef = FirebaseFirestore.getInstance().collection("users")
        usersRef.document(currentUserId).get().addOnSuccessListener {

            if (it.exists()) {

                val phone = it.get("phone").toString()

                if (phone == "null" || phone == "") {

                    goToInfoActivity()
                    finish()
                } else {
                    goToMainActivity()
                    finish()
                }
            }


        }
    }

    // para iniciar sesión con Google
    fun googleLogIn() {

        val providers = arrayListOf(AuthUI.IdpConfig.GoogleBuilder().build())
        //esta es la pantalla de google que me dará mis cuentas guardadas en el cel
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .build(),
                RC_SIGN_IN)

    }
    // en el on activity result miramos cual fue la respuesta del intent de google

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
// si obtuvimos tespuesta de los olicitado (para esto se usa el codigo numérico creado antes
        if (requestCode == RC_SIGN_IN) {
            //tomamos el valor de la respuesta (la cuenta seleccionada)
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in, si to do salió bien creamos una constante user que será my curent user y vamos al Main activity y guardamos la info en firestore
                saveinfo()
                val user = mAuth.currentUser
                // ...

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Toast.makeText(this, "Ocurrio un error ${response!!.error!!.errorCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveinfo() {

        //Tomamos el uid que se le acaba de dar a este usuario que actualmente ya está registrado
        val currentUserId = mAuth.currentUser!!.uid
        // creamos la colección en el firestore
        val usersRef = FirebaseFirestore.getInstance().collection("users")
        usersRef.document(currentUserId).get().addOnSuccessListener {

            if (it.exists()){

            val phone=it.get("phone").toString()

            if ( phone== "null" || phone== "") {

                goToInfoActivity()
                finish()
            }else{
                goToMainActivity()
                finish()
            }
        }else{

                val currentUser = mAuth.currentUser!!


                // para guardar los datos en el firestore debemos crear el siguiente hashmap con los valores que contendrá
                val userMap = HashMap<String, Any>()
                userMap["uid"] = currentUserId
                userMap["name"] = currentUser.displayName.toString()
                userMap["id"] = ""
                userMap["email"] = currentUser.email.toString()
                userMap["password"] = ""
                userMap["phone"] = currentUser.phoneNumber.toString()
                userMap["ubication"] = ""
                userMap["bio"] = ""
                if (currentUser.photoUrl.toString() == "") {
                    userMap["photo"] = "https://firebasestorage.googleapis.com/v0/b/arrienda3.appspot.com/o/profile%20default.png?alt=media&token=071099bf-47f8-4369-bb2f-e0637e4b3d8b"
                } else {
                    userMap["photo"] = currentUser.photoUrl.toString()
                }
                //ahora vamos a adicionar ese hashmap llamado en este caso user map al firebasestore
                usersRef.document(currentUserId).set(userMap).addOnCompleteListener { Task ->
                    if (Task.isSuccessful) {
                        Toast.makeText(this, "ingreso exitoso", Toast.LENGTH_SHORT).show()
                    } else {
                        // si no fue exitoso o hubo un error, mostraremos el error en un toast
                        val message = Task.exception!!.toString()
                        Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                        mAuth.signOut()
                    }
                }
                goToInfoActivity()
                finish()

            }

        }

    }


}
