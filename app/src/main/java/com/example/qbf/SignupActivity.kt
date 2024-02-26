package com.example.qbf

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.qbf.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var banklist: ArrayList<Bank>
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = getColor(R.color.white) // Replace with your desired color
        }
        firebaseAuth = FirebaseAuth.getInstance()

        // Check if the user is already authenticated
        val user = firebaseAuth.currentUser
        if (user != null) {
            // User is already authenticated, navigate to BankListActivity
            val intent = Intent(this@SignupActivity, bankListActivity::class.java)
            startActivity(intent)
            finish() // Optional: Finish SignUpActivity to prevent going back to it
        }

        binding.signupButton.setOnClickListener{
            var email = binding.signupemail2.text.toString()
            val password = binding.signupPassword2.text.toString()
            val confirmpassword = binding.signupconfirm2.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmpassword.isNotEmpty()){
                if (password == confirmpassword){
                    firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener{
                        if (it.isSuccessful){
                            // User successfully created, navigate to BankListActivity
                            val intent = Intent(this, bankListActivity::class.java)
                            startActivity(intent)
                            finish() // Optional: Finish SignUpActivity to prevent going back to it
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Password does not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.alreadyhaveacc.setOnClickListener{
            val loginIntent = Intent (this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
    }

}