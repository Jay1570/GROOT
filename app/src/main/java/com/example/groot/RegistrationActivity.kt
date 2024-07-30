package com.example.groot

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.groot.viewmodel.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import java.util.regex.Pattern

class RegistrationActivity : AppCompatActivity() {
    private lateinit var txtUsername: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var txtConfirmPassword:EditText
    private lateinit var btnRegister:Button

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        txtUsername=findViewById(R.id.txtUsername)
        txtEmail=findViewById(R.id.txtEmail)
        txtPassword=findViewById(R.id.txtPassword)
        txtConfirmPassword=findViewById(R.id.txtConPassword)
        btnRegister=findViewById(R.id.btnRegister)

        authViewModel.authStatus.observe(this) { status ->
            Snackbar.make(findViewById(R.id.main), status, Snackbar.LENGTH_SHORT).show()
            if (status.contains("Successful")) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            }
        }
        btnRegister.setOnClickListener {
            val email=txtEmail.text.toString()
            val password=txtPassword.text.toString()
            val imgUrl=""
            val username=txtUsername.text.toString()
            val cof=txtConfirmPassword.text.toString()

            if(!email.isValidEmail()) {
                Snackbar.make(findViewById(R.id.main), getString(R.string.invalid_email), Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(!password.isValidPassword()) {
                Snackbar.make(findViewById(R.id.main), getString(R.string.invalid_password), Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != cof){
                Snackbar.make(findViewById(R.id.main), getString(R.string.confirm_password), Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (username.isBlank()) {
                Snackbar.make(findViewById(R.id.main), getString(R.string.empty_username), Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (authViewModel.checkUsername(username)) {
                Snackbar.make(findViewById(R.id.main), getString(R.string.username_exists), Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            authViewModel.signup(email, password, imgUrl, username)
        }
    }
}
fun String.isValidEmail(): Boolean {
    return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
    return this.isNotBlank() &&
            this.length >= 8 &&
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}\$").matcher(this).matches()
}