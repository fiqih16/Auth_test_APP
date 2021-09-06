package com.fiqih.myapplication

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.fiqih.myapplication.model.Post

class PostViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    private lateinit var nameTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var textView: TextView
    private lateinit var imageView: ImageView
    lateinit var deleteButton: Button

    fun setPost(activity: FragmentActivity, post: Post, userId: String?) {
        nameTextView = itemView.findViewById(R.id.postNameTextView)
        timeTextView = itemView.findViewById(R.id.postTimeTextView)
        textView = itemView.findViewById(R.id.postTextView)
        imageView = itemView.findViewById(R.id.postImageView)
        deleteButton = itemView.findViewById(R.id.postDeleteButton)

        nameTextView.text = post.uid
        timeTextView.text = post.time
        textView.text = post.text

        when (post.type) {
            "image" -> {
                imageView.visibility = View.VISIBLE

                Glide.with(activity).load(post.postUrl).into(imageView)
            }
        }

        if (post.uid != userId) {
            deleteButton.visibility = View.GONE
        }
    }
}