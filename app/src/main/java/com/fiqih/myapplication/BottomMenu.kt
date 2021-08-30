package com.fiqih.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class BottomMenu : BottomSheetDialogFragment() {
    private lateinit var profileCardView: CardView
    private lateinit var logoutCardView: CardView
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.bottom_menu, container, false)

        auth = Firebase.auth
        profileCardView = view.findViewById(R.id.profileCardView)
        logoutCardView = view.findViewById(R.id.logoutCardView)

        profileCardView.setOnClickListener {
            val intent = Intent(requireActivity(), ProfileActivity::class.java)
            startActivity(intent)
        }

        logoutCardView.setOnClickListener {
            val builder = AlertDialog.Builder(requireActivity()).apply {
                setTitle("Logout")
                setMessage("Yakin ingin keluar?")
                setPositiveButton("logout") {_, _ ->
                    auth.signOut()
                    val intent = Intent(requireActivity(), LoginActivity::class.java)
                    startActivity(intent)
                }
                setNegativeButton("Cancel") { _, _ ->}
            }
            builder.create()
            builder.show()
        }

        return view
    }
}