package com.example.groot

import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.groot.viewmodel.AuthViewModel
import java.util.regex.Pattern

class RegistrationActivity : AppCompatActivity() {
    private lateinit var userImage: ImageView
    private lateinit var userNametxt: EditText
    private lateinit var emailtxt: EditText
    private lateinit var passwordtxt: EditText
    private lateinit var confromPasswordtxt:EditText
    private lateinit var btn_register:Button
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
        setContentView(R.layout.activity_registration)
        userImage=findViewById(R.id.imageView3)
        userNametxt=findViewById(R.id.rg_username)
        emailtxt=findViewById(R.id.rg_email)
        passwordtxt=findViewById(R.id.rg_password)
        confromPasswordtxt=findViewById(R.id.rg_con_password)
        btn_register=findViewById(R.id.rg_Register)

        authViewModel.authStatus.observe(this) { status ->
            Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
        }
        btn_register.setOnClickListener(){
            val email=emailtxt.text.toString()
            val password=passwordtxt.text.toString()
            val imgUrl=""
            val username=userNametxt.text.toString()
            val cof=confromPasswordtxt.text.toString()

            if(!email.isValidEmail()) {
                Toast.makeText(this,"Invalid Email...!",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(!password.isValidPassword()) {
            Toast.makeText(this,"Invalid Password...!",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!password.equals(cof)){
                Toast.makeText(this,"Confirm password is not match",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            authViewModel.signup(email,password,imgUrl,username)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}