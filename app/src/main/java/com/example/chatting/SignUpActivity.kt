package com.example.chatting

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.chatting.databinding.ActivitySignUpBinding
import com.example.chatting.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.util.UUID

class SignUpActivity : AppCompatActivity() {

    private lateinit var signUpActivity: ActivitySignUpBinding
    private var auth = FirebaseAuth.getInstance()
    private lateinit var databaseReference: DatabaseReference
    lateinit var activityResult: ActivityResultLauncher<Intent>
    var imageUri: Uri? = null
    var storage: FirebaseStorage = FirebaseStorage.getInstance()
    val storageRef : StorageReference = storage.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signUpActivity = ActivitySignUpBinding.inflate(layoutInflater)
        val view = signUpActivity.root
        setContentView(view)

        signUpActivity.signup.setOnClickListener {
            val name = signUpActivity.nameEdit.text.toString()
            val email = signUpActivity.emailEdit.text.toString()
            val password = signUpActivity.passwordEdit.text.toString()
            signUp(name, email, password)
        }

        registerActivityForResult()

        signUpActivity.profile.setOnClickListener {
            chooseImage()
        }
    }

    private fun signUp(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        uploadImageToDatabase() // Call uploadImageToDatabase regardless of image selection
                        val intent = Intent(this@SignUpActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    // Handle the error here and show a toast message with the error details
                    val errorMessage = task.exception?.message ?: "Unknown error"
                    Toast.makeText(applicationContext, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun addUserToDatabase(name: String, email: String,uid: String, imageUrl: String){
        databaseReference = FirebaseDatabase.getInstance().reference
        // Uid is passed as a unique database for every user.
        databaseReference.child("user").child(uid).setValue(User(name,email,uid,imageUrl))
    }

    private fun chooseImage() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        } else {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResult.launch(intent)
        }
    }


    private fun uploadImageToDatabase() {
        val name = signUpActivity.nameEdit.text.toString()
        val email = signUpActivity.emailEdit.text.toString()
        val password = signUpActivity.passwordEdit.text.toString()

        if (imageUri != null) {
            val img = UUID.randomUUID().toString()
            val imgRef = storageRef.child("user").child("image").child(img)

            imgRef.putFile(imageUri!!).addOnSuccessListener {
                Toast.makeText(applicationContext, "File Uploaded", Toast.LENGTH_SHORT).show()
                val uploadedImage = storageRef.child("user").child("image").child(img)
                uploadedImage.downloadUrl.addOnSuccessListener { url ->
                    val imageUrl = url.toString()
                    addUserToDatabase(name, email, auth.currentUser?.uid!!,imageUrl)

                }.addOnFailureListener {
                    Toast.makeText(
                        applicationContext,
                        "Error occurred while uploading the photo",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            // Handle the case where no image is selected.
            addUserToDatabase(name, email, auth.currentUser?.uid!!,"")
        }
    }

    private fun registerActivityForResult() {
        activityResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback { result ->
                val resultCode = result.resultCode
                val imageData = result.data

                if (resultCode == RESULT_OK && imageData != null) {
                    imageUri = imageData.data
                    imageUri?.let {
                        Picasso.get().load(it).into(signUpActivity.profile)
                    }
                }
            }
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            activityResult.launch(intent)
        } else {
            Toast.makeText(applicationContext, "Unexpected Error", Toast.LENGTH_SHORT).show()
        }
    }
}
