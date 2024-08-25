package com.example.groot

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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

class LoginActivity : AppCompatActivity() {

    private lateinit var txtEmail:EditText
    private lateinit var txtPassword:EditText
    private lateinit var reg:TextView
    private lateinit var btnLogin:Button
    private lateinit var progressBar: CircularProgressIndicator

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        txtEmail=findViewById(R.id.txtEmail)
        txtPassword=findViewById(R.id.txtPassword)
        btnLogin=findViewById(R.id.btnLogin)
        reg = findViewById(R.id.btnCreate)
        progressBar = findViewById(R.id.progressBar)

        progressBar.indicatorDirection = CircularProgressIndicator.INDICATOR_DIRECTION_CLOCKWISE

        authViewModel.authStatus.observe(this) { status ->
            if (status.contains("Successful")) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
                return@observe
            }
            showSnackBar(status)
        }

        authViewModel.isLoading.observe(this) { isLoading ->
            progressBar.isVisible = isLoading
            findViewById<View>(R.id.loadingOverlay).isVisible = isLoading
            txtEmail.isEnabled = !isLoading
            txtPassword.isEnabled = !isLoading
            btnLogin.isEnabled = !isLoading
            reg.isEnabled = !isLoading
        }

        btnLogin.setOnClickListener {
            val email=txtEmail.text.toString()
            val password=txtPassword.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
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
            authViewModel.login(email, password)
        }

        reg.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

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
    }
    private fun showSnackBar(message: String) {
        Snackbar.make(findViewById(R.id.main), message, Snackbar.LENGTH_SHORT).show()
    }
}