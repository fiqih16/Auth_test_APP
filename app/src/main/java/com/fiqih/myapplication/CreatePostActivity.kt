package com.fiqih.myapplication

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.fiqih.myapplication.model.Post
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.lang.Exception
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class CreatePostActivity : AppCompatActivity() {
    private lateinit var playerView: PlayerView
    private lateinit var storage: FirebaseStorage
    private lateinit var attachButton: Button
    private lateinit var imageView: ImageView
    private lateinit var deleteCardView: CardView
    private lateinit var editText: EditText
    private lateinit var postButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var post: Post
    private var type = "text"
    private var selectedUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        //playerView = findViewById(R.id.createPostImageView)

        editText = findViewById(R.id.createPostEditText)
        postButton = findViewById(R.id.createPostButton)
        attachButton = findViewById(R.id.createPostAttachButton)
        imageView = findViewById(R.id.createPostImageView)
        playerView = findViewById(R.id.createPostPlayerView)
        deleteCardView = findViewById(R.id.createPostDeleteCardView)
        auth = Firebase.auth
        database = Firebase.database
        storage = Firebase.storage
        post = Post()

        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            try {
                if (result?.resultCode == Activity.RESULT_OK) {
                    result.data?.let {
                        selectedUri = it.data
                        val contentResolver: ContentResolver = getContentResolver()
                        val fileType = contentResolver.getType(selectedUri!!)

                        if (fileType!!.contains("image")) {
                            playerView.visibility = View.GONE
                            imageView.visibility = View.VISIBLE
                            deleteCardView.visibility = View.VISIBLE

                            Glide.with(this).load(selectedUri!!).into(imageView)
                            type = "image"
                        } else if (fileType!!.contains("video")) {
                            playerView.visibility = View.VISIBLE
                            imageView.visibility = View.GONE
                            deleteCardView.visibility = View.VISIBLE

                            val exoPlayer = SimpleExoPlayer.Builder(this).build()
                            val dataSourceFactory = DefaultDataSourceFactory(this)
                            val mediaItem = MediaItem.fromUri(selectedUri!!)
                            val mediaSource = ProgressiveMediaSource
                                .Factory(dataSourceFactory)
                                .createMediaSource(mediaItem)

                            exoPlayer.setMediaSource(mediaSource)
                            exoPlayer.prepare()
                            exoPlayer.playWhenReady = true
                            playerView.player = exoPlayer
                            type = "video"
                        } else if (fileType!!.contains("audio")) {
                            playerView.visibility = View.VISIBLE
                            imageView.visibility = View.GONE
                            deleteCardView.visibility = View.VISIBLE

                            val exoPlayer = SimpleExoPlayer.Builder(this).build()
                            val dataSourceFactory = DefaultDataSourceFactory(this)
                            val mediaItem = MediaItem.fromUri(selectedUri!!)
                            val mediaSource = ProgressiveMediaSource
                                .Factory(dataSourceFactory)
                                .createMediaSource(mediaItem)

                            exoPlayer.setMediaSource(mediaSource)
                            exoPlayer.prepare()
                            exoPlayer.playWhenReady = true
                            playerView.player = exoPlayer
                            type = "audio"
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        postButton.setOnClickListener {
            val currentUser = auth.currentUser
            currentUser?.let {
                val uid = it.uid

                val today = Calendar.getInstance().time

                val formatter = SimpleDateFormat("dd-MMMM-yyyy hh:mm:ss")
                val time = formatter.format(today)

                selectedUri?.let {
                    val contentResolver: ContentResolver = getContentResolver()
                    val mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()
                    val fileExtension = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(it))

                    val storageReferenece = storage.getReference("posts").child(System.currentTimeMillis().toString() + ".${fileExtension}")
                    val uploadTask = storageReferenece.putFile(it)
                    uploadTask.continueWithTask {
                        if (!it.isSuccessful) {
                            throw it.exception!!.cause!!
                        }

                        storageReferenece.downloadUrl
                    }.addOnCompleteListener {
                        if (it.isSuccessful) {
                            it.result?.let {
                                val downloadedUri = it

                            post.text = editText.text.toString()
                            post.type = type
                            post.time = time
                            post.uid = uid
                            post.postUrl = downloadedUri.toString()

                            val references = database.getReference("posts")
                            val postid = references.push().key!!
                            references.child(postid).setValue(post).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(this,"Berhasil", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                        }
                    }
                }
            }

                if (selectedUri == null) {
                    post.text = editText.text.toString()
                    post.type = type
                    post.time = time
                    post.uid = uid
                    post.postUrl = ""

                    val reference = database.getReference("posts")
                    val postId = reference.push().key!!
                    reference.child(postId).setValue(post).addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(this, "Posted", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        attachButton.setOnClickListener {
            val intent = Intent()
            intent.setType("image/* video/* audio/*")
            intent.putExtra(
                Intent.EXTRA_MIME_TYPES,
                arrayOf("image/*", "video/*", "audio/*")
            )
            intent.setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher.launch(intent)
        }

        deleteCardView.setOnClickListener {
            selectedUri = null
            type = "text"

            imageView.visibility = View.GONE
            playerView.visibility = View.GONE
            it.visibility = View.GONE
        }
    }
}