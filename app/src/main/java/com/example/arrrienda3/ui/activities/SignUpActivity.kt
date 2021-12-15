package com.example.arrrienda3.ui.activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.arrrienda3.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_fotgot_password.*
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_sign_up.btnGoBackLogIn
import java.util.regex.Pattern

class SignUpActivity : AppCompatActivity() {

    private val mAuth:FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        //para dar la alerta del el cambio en la casilla de la clave y mostrar el mensaje de que debe ser de minimo 8 caracteres
        passwordValidation()

        //que al dar click en el botón se registre y vaya al login
        btnSingUp.setOnClickListener {
            createAccount()
        }

        //volver al login
        btnGoBackLogIn.setOnClickListener {
            goToLogIn()
        }

    }
    //para dar la alerta del el cambio en el edit text de la clave y mostrar el mensaje de que debe ser de minimo 8 caracteres
    private fun passwordValidation() {
        etSignUpPassword.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                if (isValidatePassword(etSignUpPassword.text.toString())){
                }else{
                    etSignUpPassword.error="min 8 caracteres"
                }
            }
        })
    }

    //esta es la función de crear la cuenta que nos llevara a otra que creará el archivo de usuario

    private fun createAccount() {
        val email=etSignUpEmail.text.toString()
        val password = etSignUpPassword.text.toString()
        val confirmPassword = etSignUpPasswordAgain.text.toString()

        //validamos que el correo se valido, la contraseña tenga minimo 8n caracteres y que la confirmación de contraseña sea igual
        if (isValidateEmail(email)&&isValidatePassword(password)&&isValidateConfirmPassword(password,confirmPassword)) {
            //esto es para escoger el idioma
            mAuth.setLanguageCode("es")

            //vamos a crear la nueva cuenta
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //si fue creada con exito llamamos a esta función que nos guardará la info
                    saveUserInfo(email,password)
                }else{
                    // si algo salió mal mostraremos un toast con el error generado por firebase
                    val message=task.exception!!.toString()
                    Toast.makeText(this,"Error: $message",Toast.LENGTH_SHORT).show()
                    mAuth.signOut()


                }

            }

            //Ahora haremos la vealidación para que si alguien ha ingresado incorrectamente un valor se muestre una alerta de error
        }else{
            // si el email es valido
            if (isValidateEmail(email)){
                // recisamos la contraseña y si la contraseña es valida
                if (isValidatePassword(password)) {
                    //revisamos que la confirmacion de contraseña sea igual a la contraseña
                    if (isValidateConfirmPassword(password, confirmPassword)) {
                        // si están iguales quiere decir que to do está bien así que acá no se muestra ninguna alera
                    } else {
                        // si no, mostramos la siguiente alerta que le dice al usuario que las contraseñas no coinciden
                        showAlertConfirmPassword()
                    }
                }else {
                    // si llegamos acá es porque la contraseña no cumple con el requerimiento de min 8 caracteres así que mostramos su respectiva alerta
                    showAlertPassword()
                }

            }else
            // y si llegamos acá es porque el primer check, el del email, no es correcto así que mostramos su alerta
                showAlertEmail()
        }
    }

    // esta es la función para guardar la info
    private fun saveUserInfo(email: String, password: String) {
// vamos a progrmar un progress dialog que hará esperar al usuario mientras se guarda su info
        val progressDialogEmailEnviado= ProgressDialog(this)
        progressDialogEmailEnviado.setTitle("Email enviado")
        progressDialogEmailEnviado.setMessage("hemos enviado un correo de verificación a tu email")
        progressDialogEmailEnviado.setCanceledOnTouchOutside(false)
        progressDialogEmailEnviado.show()

        //Tomamos el uid que se le acaba de dar a este usuario que actualmente ya está registrado
        val currentUserId=mAuth.currentUser!!.uid
        // creamos la colección en el firestore
        val usersRef=FirebaseFirestore.getInstance().collection("users").document(currentUserId)

        // para guardar los datos en el firestore debemos crear el siguiente hashmap con los valores que contendrá
        val userMap= HashMap<String,Any>()
        userMap["uid"]=currentUserId
        userMap["name"]=""
        userMap["id"]=""
        userMap["email"]=email
        userMap["password"]=password
        userMap["phone"]=""
        userMap["ubication"]=""
        userMap["photo"]="https://firebasestorage.googleapis.com/v0/b/arrienda3.appspot.com/o/profile%20default.png?alt=media&token=071099bf-47f8-4369-bb2f-e0637e4b3d8b"
        userMap["bio"]=""

        //ahora vamos a adicionar ese hashmap llamado en este caso user map al firebasestore
        usersRef.set(userMap).addOnCompleteListener{ Task ->
            if (Task.isSuccessful) {
                //si esto fue exitoso enviaremos un correo de verificación de usuario
                mAuth.currentUser!!.sendEmailVerification()
                //mostramos el progress dialog de espera y vamos al login
                progressDialogEmailEnviado.dismiss()
                Toast.makeText(this, "La cuenta ha sido creada", Toast.LENGTH_SHORT).show()
                goToLogIn()
            }else{
                // si no fue exitoso o hubo un error, mostraremos el error en un toast
                val message=Task.exception!!.toString()
                Toast.makeText(this,"Error: $message",Toast.LENGTH_SHORT).show()
                mAuth.signOut()
            }
        }
    }

    fun goToLogIn(){
        val intent=Intent(this,logInActivity::class.java)
        //parqa que este activity no se quede guardado en el historial y al darle atrás no vuelva a este
        startActivity(intent)
        intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right)
        finish()
    }

    private fun isValidateEmail(email:String):Boolean{
        val pattern= Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

    private fun isValidatePassword(password:String):Boolean{
        //minimo 8 caracteres
        val passwordPattern= "^(?=\\S+$).{8,}$"
        val pattern= Pattern.compile(passwordPattern)

        return pattern.matcher(password).matches()
    }

    private fun isValidateConfirmPassword(passsword:String,confirmPasssword:String):Boolean{
        val password= etSignUpPassword.text.toString()
        val confirmPassword= etSignUpPasswordAgain.text.toString()
        return password==confirmPassword
    }

    private fun showAlertEmail(){
        val builder=AlertDialog.Builder(this)
        builder.setTitle("Correo invalido")
        builder.setMessage("porfavor ingrese los datos correctamente")
        builder.setPositiveButton("aceptar",null)
        val dialog:AlertDialog=builder.create()
        dialog.show()
    }

    private fun showAlertConfirmPassword(){
        val builder=AlertDialog.Builder(this)
        builder.setMessage("Correo inválido")
        builder.setPositiveButton("aceptar",null)
        val dialog:AlertDialog=builder.create()
        dialog.show()
    }

    private fun showAlertPassword(){
        val builder=AlertDialog.Builder(this)
        builder.setTitle("Contraseña inválida")
        builder.setMessage("La contraseña debe contener almenos 8 caracteres")
        builder.setPositiveButton("aceptar",null)
        val dialog:AlertDialog=builder.create()
        dialog.show()
    }
}
