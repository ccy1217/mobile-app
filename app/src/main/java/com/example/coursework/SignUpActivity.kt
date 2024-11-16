package com.example.coursework

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {

    //play the music , need to use raw file, but where to put is a problem,
    //may be i need to create setting
    //do the notification when user create an account

    private lateinit var name:EditText
    private lateinit var email:EditText
    private lateinit var password:EditText
    private lateinit var password2:EditText
    private lateinit var createAccount: Button

    //create log testing
    private val myTag ="joanne"

    private var mAuth= FirebaseAuth.getInstance()
    private var currentUser = mAuth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(myTag,"in onCreate")
        setContentView(R.layout.activity_sign_up)

        //set those UI elements
        name= findViewById(R.id.create_name)
        email= findViewById(R.id.create_email)
        password= findViewById(R.id.SignUpTypePassword)
        password2 = findViewById(R.id.SignUpConfirmPassword)
        createAccount = findViewById(R.id.signup_button)

        createAccount.setOnClickListener{v -> registerClick(v)}



        //go to login page
        val signInText = findViewById<TextView>(R.id.txtSignIn)
        signInText.setOnClickListener {
            Log.i(myTag,"Button click and go to login page")
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun registerClick(view:View){
        Log.i(myTag,"register Click")

        if(mAuth.currentUser !=null){
            displayMessage(view, getString(R.string.register_while_logged_in))
        }
        else{
            mAuth.createUserWithEmailAndPassword(
                email.text.toString(),
                password2.text.toString()
            ).addOnCompleteListener(this) { task ->
                if(task.isSuccessful){
                    closeKeyBoard()
                    update()
                }

            }
        }
    }
    //handle logging in
    private fun loginClick(){
        Log.i(myTag, "Login Clicked")

        mAuth.signInWithEmailAndPassword(
            email.text.toString(),
            password2.text.toString()).addOnCompleteListener(this){
                task ->
                if(task.isSuccessful){
                    closeKeyBoard()
                    update()
                }
        }

    }

//    //logout function
//    private fun logoutClick(){
//        Log.i(myTag, "Logout Clicked")
//        currentUser = mAuth.currentUser
//        mAuth.signOut()
//        update()
//    }




    override fun onStart(){
        super.onStart()
        Log.i(myTag, "in OnStart")
        update()
    }

    //function which updates the UI with Login status
    private fun update(){
        Log.i(myTag,"in update")
        currentUser = mAuth.currentUser
        var currentEmail = currentUser?.email
        val greetingSpace = findViewById<TextView>(R.id.create_email)

        if(currentEmail == null){
            greetingSpace.text = getString(R.string.not_logged_in)
        }
        else{
            greetingSpace.text = getString(R.string.logged_in,currentEmail)
        }
    }

    override fun onStop(){
        super.onStop()
        Log.i(myTag,"in onStop")

    }
    private fun displayMessage(view: View, msgTxt: String){
        val sb = Snackbar.make(view, msgTxt, Snackbar.LENGTH_SHORT)
        sb.show()
    }

    private fun closeKeyBoard(){
        val view = this.currentFocus
        if(view !=null){
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken,0)
        }
    }
}