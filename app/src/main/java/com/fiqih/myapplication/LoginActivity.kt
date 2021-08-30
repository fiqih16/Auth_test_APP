package com.fiqih.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var checkBox: CheckBox
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = Firebase.auth
        emailEditText = findViewById(R.id.LoginEmailEditText)
        passwordEditText = findViewById(R.id.LoginPasswordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.loginRegisterButton)
        checkBox = findViewById(R.id.loginShowCheck)
        progressBar = findViewById(R.id.loginProgressBar)

        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                progressBar.visibility = View.VISIBLE
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val error = it.exception
                        error?.let {
                            Toast.makeText(this,"Error: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    progressBar.visibility = View.INVISIBLE
                }
            } else {
                Toast.makeText(this,"Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        registerButton.setOnClickListener {
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}