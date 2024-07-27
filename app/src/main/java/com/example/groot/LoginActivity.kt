package com.example.groot

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.groot.viewmodel.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var txtEmail:EditText
    private lateinit var txtPassword:EditText
    private lateinit var reg:TextView
    private lateinit var btnLogin:Button

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        txtEmail=findViewById(R.id.ln_email)
        txtPassword=findViewById(R.id.ln_password)
        btnLogin=findViewById(R.id.login)
        reg = findViewById(R.id.ln_Login)

        authViewModel.authStatus.observe(this) { status ->
            Snackbar.make(findViewById(R.id.main), status, Snackbar.LENGTH_SHORT).show()
            if (status.contains("Successful")) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }

        btnLogin.setOnClickListener {
            val email=txtEmail.text.toString()
            val password=txtPassword.text.toString()

            if(!email.isValidEmail()) {
                Toast.makeText(this,getString(R.string.invalid_email),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(!password.isValidPassword()) {
                Toast.makeText(this,getString(R.string.invalid_password),Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                authViewModel.login(email, password)
            }
        }

        reg.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}