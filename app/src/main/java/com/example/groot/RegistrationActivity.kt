package com.example.groot

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updateMargins
import com.example.groot.utility.isValidEmail
import com.example.groot.utility.isValidPassword
import com.example.groot.viewmodel.AuthViewModel
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.google.android.material.snackbar.Snackbar

class RegistrationActivity : AppCompatActivity() {

    private lateinit var txtUsername: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtPassword: EditText
    private lateinit var txtConfirmPassword:EditText
    private lateinit var btnRegister:Button
    private lateinit var progressBar: CircularProgressIndicator

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val bottomPadding = if (!insets.isVisible(WindowInsetsCompat.Type.ime())) systemBarsInsets.bottom else 0
            v.setPadding(
                systemBarsInsets.left,
                systemBarsInsets.top,
                systemBarsInsets.right,
                bottomPadding
            )
            v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                updateMargins(bottom = imeInsets.bottom)
            }
            WindowInsetsCompat.CONSUMED
        }

        txtUsername=findViewById(R.id.txtUsername)
        txtEmail=findViewById(R.id.txtEmail)
        txtPassword=findViewById(R.id.txtPassword)
        txtConfirmPassword=findViewById(R.id.txtConPassword)
        btnRegister=findViewById(R.id.btnRegister)
        progressBar = findViewById(R.id.progressBar)


        authViewModel.authStatus.observe(this) { status ->
            if (status.contains("Successful")) {
                val intent = Intent(this, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                return@observe
            }
            showSnackBar(status)
        }

        authViewModel.isLoading.observe(this) { isLoading ->
            progressBar.isVisible = isLoading
            findViewById<View>(R.id.loadingOverlay).isVisible = isLoading
            txtUsername.isEnabled = !isLoading
            txtEmail.isEnabled = !isLoading
            txtPassword.isEnabled = !isLoading
            txtConfirmPassword.isEnabled = !isLoading
            btnRegister.isEnabled = !isLoading
        }

        btnRegister.setOnClickListener {
            val email=txtEmail.text.toString().trim()
            val password=txtPassword.text.toString().trim()
            val imgUrl=""
            val username=txtUsername.text.toString().trim()
            val cof=txtConfirmPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty() || username.isEmpty() || cof.isEmpty()) {
                showSnackBar(getString(R.string.empty_fields))
                return@setOnClickListener
            }

            if(!email.isValidEmail()) {
                showSnackBar(getString(R.string.invalid_email))
                return@setOnClickListener
            }
            if(!password.isValidPassword()) {
                showSnackBar(getString(R.string.invalid_password))
                return@setOnClickListener
            }
            if (password != cof){
                showSnackBar(getString(R.string.confirm_password))
                return@setOnClickListener
            }
            authViewModel.signup(email, password, imgUrl, username)
        }
    }

    private fun showSnackBar(message: String) {
        Snackbar.make(findViewById(R.id.main), message, Snackbar.LENGTH_SHORT).show()
    }
}