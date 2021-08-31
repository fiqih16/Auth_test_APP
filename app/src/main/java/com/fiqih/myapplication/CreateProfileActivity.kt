package com.fiqih.myapplication

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import androidx.activity.result.contract.ActivityResultContracts
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import com.bumptech.glide.Glide
import com.fiqih.myapplication.model.Profile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.lang.Exception

class CreateProfileActivity : AppCompatActivity() {

    private lateinit var  imageView: ImageView
    private lateinit var  nameEditText: EditText
    private lateinit var  bioEditText: EditText
    private lateinit var  emailEditText: EditText
    private lateinit var  createButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var database: FirebaseDatabase
    private lateinit var firestore: FirebaseFirestore
    private lateinit var  storage: FirebaseStorage
    private lateinit var auth: FirebaseAuth

    private var imageUri: Uri? = null
    private var uid: String? = null

    private lateinit var profile: Profile


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)

        database = Firebase.database
        firestore = Firebase.firestore
        storage = Firebase.storage
        auth = Firebase.auth

        profile = Profile()


        imageView = findViewById(R.id.createProfileImageView)
        nameEditText = findViewById(R.id.createProfilNameEditText)
        bioEditText = findViewById(R.id.createProfilBioEditText)
        emailEditText = findViewById(R.id.createprofileEmailEditText)
        createButton = findViewById(R.id.createProfileButton)
        progressBar = findViewById(R.id.createProfileProgressBar)


        val currentUSer = auth.currentUser
        currentUSer?.let{
            uid = it.uid
        }
        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            try {
                if (it?.resultCode == Activity.RESULT_OK) {
                    it.data?.let {
                        imageUri = it.data
                        Glide.with(this).load(imageUri).into(imageView)
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        imageView.setOnClickListener {
            val intent = Intent().apply {
                setType("image/*")
                setAction(Intent.ACTION_GET_CONTENT)
            }
            resultLauncher.launch(intent)
        }

        createButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val bio = bioEditText.text.toString()
            val email = emailEditText.text.toString()

            if (name.isNotEmpty() && bio.isNotEmpty() && email.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE
                imageUri?.let {
                    val contentResolver: ContentResolver = getContentResolver()
                    val mimeTypemap: MimeTypeMap = MimeTypeMap.getSingleton()
                    val fileExtension = mimeTypemap.getExtensionFromMimeType(contentResolver.getType(it))

                    val reference = storage.getReference("profile_images").child(System.currentTimeMillis().toString() + ".${fileExtension}")
                    val uploadTask = reference.putFile(it)
                    uploadTask.continueWithTask {
                        if(!it.isSuccessful) {
                            throw  it.exception!!.cause!!
                        }
                        reference.downloadUrl
                    } .addOnCompleteListener {
                        if (it.isSuccessful){
                            it.result?.let {
                                val downLoadUri = it
                                val profileMap = HashMap<String, String>()
                                profileMap.put("name", name)
                                profileMap.put("bio", bio)
                                profileMap.put("email", email)
                                profileMap.put("url", downLoadUri.toString())
                                profileMap.put("uid", uid!!)

                                profile.name = name
                                profile.url = downLoadUri.toString()
                                profile.uid = uid

                                uid?.let {
                                    database.getReference("users").child(it).setValue(profile)
                                    firestore.collection("users").document(it).set(profileMap).addOnSuccessListener {
                                        progressBar.visibility = View.INVISIBLE
                                        Toast.makeText(this, "profile Created", Toast.LENGTH_SHORT).show()


                                        val handler = Handler(Looper.getMainLooper())
                                        handler.postDelayed({
                                            val intent = Intent(this, ProfileActivity::class.java)
                                            startActivity(intent)
                                        }, 2000)
                                    }
                                }
                            }
                        }
                    }
                }
            } else{
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}