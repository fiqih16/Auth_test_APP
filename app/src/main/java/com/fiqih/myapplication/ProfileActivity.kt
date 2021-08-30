package com.fiqih.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ProfileActivity : AppCompatActivity() {
    private lateinit var  backImageButton: ImageButton
    private lateinit var  editImageButton: ImageButton
    private lateinit var  imageView: ImageView
    private lateinit var  nameTextView: TextView
    private lateinit var  bioTextView: TextView
    private lateinit var  emailTextView: TextView
    private lateinit var  firestore: FirebaseFirestore
    private lateinit var  auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        firestore = Firebase.firestore
        auth = Firebase.auth

        backImageButton = findViewById(R.id.profileBackImageButton)
        editImageButton = findViewById(R.id.profileEditImageButton)
        imageView = findViewById(R.id.profileImageView)
        bioTextView = findViewById(R.id.profileBioText)
        emailTextView = findViewById(R.id.profileEmailText)


        backImageButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        currentUser?.let {
            val uid = it.uid
            val reference = firestore.collection("users").document(uid)


            reference.get().addOnCompleteListener {
                it.result?.let {
                    if (it.exists()){
                        val name = it.getString("name")
                        val bio = it.getString("bio")
                        val email = it.getString("email")
                        val url = it.getString("url")

                        Glide.with(this).load(url).into(imageView)
                        nameTextView.text = name
                        bioTextView.text = bio
                        emailTextView.text = email
                    } else {
                        val intent = Intent(this, CreateProfileActivity::class.java)
                        startActivity(intent)
                    }
                }
            }



        }
    }
}