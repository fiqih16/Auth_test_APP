package com.fiqih.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fiqih.myapplication.model.Post
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var postButton: ImageView
    private lateinit var menuImageView: ImageView
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        menuImageView = findViewById(R.id.mainMenuImageButton)

        postButton = findViewById(R.id.mainPostButton)
        recyclerView = findViewById(R.id.mainRecyclerView)
        database = Firebase.database

        menuImageView.setOnClickListener {
            val bottomMenu = BottomMenu()
            bottomMenu.show(supportFragmentManager, "bottomSheet")
        }

        postButton.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivity(intent)
        }
    }

//    override fun onStart() {
//        super.onStart()
//        val currentUser = auth.currentUser
//        currentUser?.let {
//            val uid = it.uid
//            val firestore = Firebase.firestore
//            val reference = firestore.collection("users").document(uid)
//
//            reference.get().addOnCompleteListener {
//                it.result?.let {
//                    if(!it.exists()) {
//                        val intent = Intent(this, CreateProfileActivity::class.java)
//                        startActivity(intent)
//                    }
//                }
//            }
//        }
//    }

    override fun onStart() {
        super.onStart()

        val reference = database.getReference("posts")
        val options = FirebaseRecyclerOptions.Builder<Post>()
            .setQuery(reference, Post::class.java)
            .build()

        val currentUser = auth.currentUser
        currentUser?.let {
            val uid = it.uid
            val firestore = Firebase.firestore
            val getReference = firestore.collection("users").document(uid)

            getReference.get().addOnCompleteListener {
                it.result?.let {
                    if (!it.exists()) {
                        val intent = Intent(this, CreateProfileActivity::class.java)
                        startActivity(intent)
                    }
                }
            }

            val adapter = object : FirebaseRecyclerAdapter<Post, PostViewHolder>(options) {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
                    val view = LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.post_layout, parent, false)

                    return PostViewHolder(view)
                }

                override fun onBindViewHolder(holder: PostViewHolder, position: Int, model: Post) {
                    holder.setPost(this@MainActivity, model, auth.currentUser!!.uid)

                    val postKey = getRef(position).key!!

                    holder.deleteButton.setOnClickListener {
                        val reference = database.getReference("posts").child(postKey)
                        reference.removeValue()
                    }
                }

            }

            adapter.startListening()

            recyclerView.setHasFixedSize(true)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
        }

    }
}
