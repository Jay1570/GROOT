package com.example.groot

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.groot.viewmodel.AuthViewModel
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private lateinit var txt_email:EditText
    private lateinit var txt_password:EditText
    private lateinit var reg:EditText
    private lateinit var btn_login:Button

    private  val PASS_PATTERN = "^(?=.[0-9])(?=.[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$"

    fun String.isValidEmail(): Boolean {
        return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    fun String.isValidPassword(): Boolean {
        return this.isNotBlank() &&
                this.length >= 8 &&
                Pattern.compile(PASS_PATTERN).matcher(this).matches()
    }

    val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        txt_email=findViewById(R.id.ln_email)
        txt_password=findViewById(R.id.ln_password)
        btn_login=findViewById(R.id.ln_Login)

        authViewModel.authStatus.observe(this) { status ->
            Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
        }

        btn_login.setOnClickListener(){
            val email=txt_email.text.toString()
            val password=txt_password.text.toString()

            if(!email.isValidEmail()) {
                Toast.makeText(this,"Invalid Email...!",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(!password.isValidPassword()) {
                Toast.makeText(this,"Invalid Password...!",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            authViewModel.login(email,password)
        }

        reg.setOnClickListener(){
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}