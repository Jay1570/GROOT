package com.example.groot

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.groot.viewmodel.AuthViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.regex.Pattern

class RegistrationActivity : AppCompatActivity() {
    private lateinit var userImage: ImageView
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
        userImage=findViewById(R.id.imageView3)
        txtUsername=findViewById(R.id.rg_username)
        txtEmail=findViewById(R.id.rg_email)
        txtPassword=findViewById(R.id.rg_password)
        txtConfirmPassword=findViewById(R.id.rg_con_password)
        btnRegister=findViewById(R.id.rg_Register)

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
            var checkUserName: Boolean
            runBlocking { checkUserName = authViewModel.checkUsername(username) }
            if (checkUserName) {
                Snackbar.make(findViewById(R.id.main), getString(R.string.username_exists), Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch{
                authViewModel.signup(email, password, imgUrl, username)
            }
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