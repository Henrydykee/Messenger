package com.example.messenger

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.example.messenger.Login
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_signup.*
import java.util.*

class Signup : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        progress_bar.visibility = View.GONE
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        val progressBar=progress_bar

        // referencing my signup details
        val email= etEmail.text.toString()
        val password =etPassword.text.toString()
        val  username =etUsername.text.toString()

        if (email.isEmpty() || password.isEmpty() || username.isEmpty())
            //AlertDialog

            buSignup.setOnClickListener {
                performRegister()
            }

        //firebase create new user
       FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
           .addOnCompleteListener{
              if (!it.isSuccessful) return@addOnCompleteListener
               // if successful
           }

        // selcting photo from gallery
        selectPhoto.setOnClickListener {
            val intent = Intent (Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent,0)
        }
    }
        // making the photo to display on the button
        var selectedPhotoUri: Uri? = null
     override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==0 && resultCode ==Activity.RESULT_OK && data !=null){
            // proceed and check what the selected image was
            selectedPhotoUri = data.data
            val bitmap= MediaStore.Images.Media.getBitmap(contentResolver,selectedPhotoUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            selectPhoto.setBackgroundDrawable(bitmapDrawable)
        }
    }

    private fun performRegister() {
        val email= etEmail.text.toString()
        val password =etPassword.text.toString()
        val  username =etUsername.text.toString()

       if (email.isEmpty() || password.isEmpty() || username.isEmpty()){
           Toast.makeText(this,"Please enter text in email",
               Toast.LENGTH_SHORT).show()
            return
       }
                //AlertDialog

        //firebase create new user
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                if (!it.isSuccessful) return@addOnCompleteListener
                // if successful
                Log.d("main","successfully created user")
             }.addOnFailureListener {
                Log.d("main","failed to create user")
                Toast.makeText(this,"failed to create user :${it.message}",
                    Toast.LENGTH_SHORT).show()
                }
        uploadImageToFireBaseStorage()
    }
    // function to upload image to fire base
    private fun uploadImageToFireBaseStorage() {
        if (selectedPhotoUri == null) return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/image/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener{
              Log.d("Signup","Successfully uploaded image")
                ref.downloadUrl.addOnSuccessListener{
                  it.toString()
                    Log.d("Signup","file location: $it")

                    saveUserToFirebaseDatabase(it.toString())
                }
            }
    }
        // to save user to firebase
    private fun saveUserToFirebaseDatabase(profileImageUrl: String){
      val uid = FirebaseAuth.getInstance().uid ?:""
      val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid , etUsername.text.toString() ,profileImageUrl)

        ref.setValue(user)
            .addOnSuccessListener{
                Log.d("Signup","saved to firebase")
                // moving it to the next activity
                val intent= Intent (this,LatestMessagesActivity::class.java)
                intent.flags =Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
    }

    fun buLogin(view: View) {
        progress_bar.visibility =View.VISIBLE
        intent = Intent(applicationContext,Login::class.java)
        startActivity(intent)
        // making progress bar diappeaer after the intent is passed
        progress_bar.visibility = View.GONE
    }
}
class User(val uid:String, val username: String, val profileImageUrl: String  ){
    constructor():this("","","")
}

