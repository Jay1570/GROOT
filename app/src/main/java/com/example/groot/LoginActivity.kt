package com.example.groot

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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

        txtEmail=findViewById(R.id.txtEmail)
        txtPassword=findViewById(R.id.txtPassword)
        btnLogin=findViewById(R.id.btnLogin)
        reg = findViewById(R.id.btnCreate)

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
                Snackbar.make(findViewById(R.id.main),getString(R.string.invalid_email),Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(!password.isValidPassword()) {
                Snackbar.make(findViewById(R.id.main),getString(R.string.invalid_password),Snackbar.LENGTH_SHORT).show()
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