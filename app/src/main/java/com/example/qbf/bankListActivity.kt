package com.example.qbf

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot


class bankListActivity : AppCompatActivity() {
    private var db = FirebaseFirestore.getInstance()
    private lateinit var bankRecyclerView: RecyclerView
    private lateinit var bankArrayList: ArrayList<Bank>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_list)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = getColor(R.color.white) // Replace with your desired color
        }
        val bank = FirebaseFirestore.getInstance().collection("Banks")
        val bankId = bank.id
        bankRecyclerView = findViewById(R.id.banklist)
        bankRecyclerView.layoutManager = LinearLayoutManager(this)
        bankRecyclerView.setHasFixedSize(true)
        bankArrayList = arrayListOf() // we would store all the objects (children) in this arraylist
        getBankData()
        Log.d(
            "Availability Button",
            "Button Id ${R.id.btn_avail}}"
        )

        if (!isUserAuthenticated()) {
            redirectToLoginPage();
            return;
        }


    }

    private fun isUserAuthenticated(): Boolean {
        // Check if the user is authenticated based on your logic
        // For example, you might check if a session token exists in SharedPreferences
        val auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        // If user is not null, the user is authenticated

        // If user is not null, the user is authenticated
        return user != null
    }

    private fun redirectToLoginPage() {
        // Implement logic to redirect to the login/signup page
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish() // Optional: Finish the current activity to prevent going back to it
    }


    private fun getBankData() {
        val collectionReference = db.collection("Banks")

        collectionReference.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val querySnapshot: QuerySnapshot? = task.result
                if (querySnapshot != null) {
                    for (documentSnapshot: DocumentSnapshot in querySnapshot.documents) {
                        val bank = documentSnapshot.toObject(Bank::class.java)
                        if (bank != null) {
                            bankArrayList.add(bank)
                        }
                    }
                    bankRecyclerView.adapter = MyAdapter(bankArrayList)
                }
            } else {
                Log.w("Firestore", "Error getting documents.", task.exception)
            }
        }
    }


    override fun onBackPressed() {
        // Do nothing or handle it as per your requirement
        // For example, you can show a message saying "Press logout to exit" or something similar
    }


}
