package com.fiqih.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {


    private lateinit var menuImageView: ImageView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        auth = Firebase.auth
        menuImageView = findViewById(R.id.mainMenuImageButton)
        menuImageView.setOnClickListener {
            val bottomMenu = BottomMenu()
            bottomMenu.show(supportFragmentManager, "bottomSheet")
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        currentUser?.let {
            val uid = it.uid
            val firestore = Firebase.firestore
            val reference = firestore.collection("users").document(uid)


            reference.get().addOnCompleteListener {
                it.result?.let {
                    if(!it.exists()) {
                        val intent = Intent(this, CreateProfileActivity::class.java)
                        startActivity(intent)
                    }
                }
            }
        }



    }



}