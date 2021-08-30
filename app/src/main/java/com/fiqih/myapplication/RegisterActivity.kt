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

class RegisterActivity : AppCompatActivity() {
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var loginButton: Button
    private lateinit var checkBox: CheckBox
    private lateinit var progressBar: ProgressBar
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth
        emailEditText = findViewById(R.id.registerEmailEditText)
        passwordEditText = findViewById(R.id.registerPasswordEditText)
        confirmPasswordEditText = findViewById(R.id.registerConfirmPasswordEditText)
        registerButton = findViewById(R.id.registerButton)
        loginButton = findViewById(R.id.registerLoginButton)
        checkBox = findViewById(R.id.registerShowCheckBox)
        progressBar = findViewById(R.id.registerProgressBar)

        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                passwordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                confirmPasswordEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
                confirmPasswordEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (email.isNotEmpty() || password.isNotEmpty() || confirmPassword.isNotEmpty()) {
                if (password.equals(confirmPassword)) {
                    progressBar.visibility = View.VISIBLE
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
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
                    progressBar.visibility = View.INVISIBLE
                    Toast.makeText(this,"Password and confirm password not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
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