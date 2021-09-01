package com.fiqih.myapplication

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.fiqih.myapplication.model.Post

class PostViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
    private lateinit var nameTextView: TextView
    private lateinit var timeTextView: TextView
    private lateinit var textView: TextView
    lateinit var deleteButton: Button

    fun setPost(activity: FragmentActivity, post: Post, userId: String?) {
        nameTextView = itemView.findViewById(R.id.postNameTextView)
        timeTextView = itemView.findViewById(R.id.postTimeTextView)
        textView = itemView.findViewById(R.id.postTextView)
        deleteButton = itemView.findViewById(R.id.postDeleteButton)

        nameTextView.text = post.uid
        timeTextView.text = post.time
        textView.text = post.text

        if (post.uid != userId) {
            deleteButton.visibility = View.GONE
        }
    }
}