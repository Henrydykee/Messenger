package com.example.messenger

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_signup.*
import com.google.firebase.auth.FirebaseAuth
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.Toast


class Login : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        progress_bar.visibility = View.GONE
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val progressBar= progress_bar1

        val username = etEmail_login.text.toString()
        val password =etPassword_login.text.toString()

         FirebaseAuth.getInstance().signInWithEmailAndPassword(username,password)
             .addOnCompleteListener {
             }

        if ( password.isEmpty() || username.isEmpty()){
            Toast.makeText(this,"Please enter text in email",
                Toast.LENGTH_SHORT).show()
            return
        }
    }

    fun buSignup(view: View) {
        progress_bar1.visibility= View.VISIBLE
        intent = Intent(applicationContext,Signup::class.java)
        startActivity(intent)
        // making progress bar diappeaer after the intent is passed
        progress_bar1.visibility = View.GONE
    }
}
