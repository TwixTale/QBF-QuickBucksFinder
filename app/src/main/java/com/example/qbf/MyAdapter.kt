package com.example.qbf

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qbf.Bank
import com.example.qbf.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration


class MyAdapter(private var banklist: ArrayList<Bank>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    private var db = FirebaseFirestore.getInstance()
    private var listenerRegistration: ListenerRegistration? = null
    private val auth:FirebaseAuth = FirebaseAuth.getInstance()


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)

        // Attach a Firestore snapshot listener when the adapter is attached to the RecyclerView
        val query = db.collection("Banks")

        listenerRegistration = query.addSnapshotListener { querySnapshot, _ ->
            querySnapshot?.let {
                // Handle the snapshot changes
                banklist.clear()
                for (document in it.documents) {
                    val bank = document.toObject(Bank::class.java)
                    bank?.let { banklist.add(it) }
                }
                notifyDataSetChanged()
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)

        // Detach the listener when the adapter is detached from the RecyclerView
        listenerRegistration?.remove()
    }




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.bank_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return banklist.size
    }



    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentBank = banklist[position]
        val currentUser = FirebaseAuth.getInstance().currentUser
        holder.Name.text = currentBank.Name
        holder.Address.text = currentBank.Address

        val btnAvail: ImageButton = holder.itemView.findViewById(R.id.btn_avail)
        btnAvail.setOnClickListener {
            Log.e("Button Clicked", "btnAvail")

            val bankId = currentBank.Docid
            val userId = currentUser?.uid
            val availabilityImage = holder.itemView.findViewById<ImageView>(R.id.availability_status)

            // Toggle the newStatus based on availabilityStatus
            val newStatus = !currentBank.availabilityStatus

            // Update both Firebase and the UI
            updateAvailabilityStatus(bankId, userId,newStatus, availabilityImage)
            Log.d("AvailabilityStatusLog", "updateAvailabilityStatus called for bankId: $bankId, newStatus: $newStatus}")
            Log.e("Success", "Method executed")
        }

        val availabilityImage = holder.itemView.findViewById<ImageView>(R.id.availability_status)
        availabilityImage.setImageResource(
            if (currentBank.availabilityStatus == false) R.drawable.cash_unavailabe
            else R.drawable.cash_availabe
        )
    }


    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val Name: TextView = itemView.findViewById(R.id.bankName)
        val Address: TextView = itemView.findViewById(R.id.bankAddress)
    }

    fun updateAvailabilityStatus(bankId: String?,userId: String?, newStatus: Boolean, availabilityImage: ImageView) {
        Log.d("AvailabilityStatusLog", "updateAvailabilityStatus called for bankId: $bankId,userId: $userId , newStatus: $newStatus}")

        val bankRef = db.collection("Banks").document(bankId!!)

        // Update Firebase with the newStatus
        bankRef
            .update("availabilityStatus", newStatus)
            .addOnSuccessListener {
                // Update the availability image based on newStatus
                availabilityImage.setImageResource(
                    if (newStatus) R.drawable.cash_availabe
                    else R.drawable.cash_unavailabe
                )

                Log.d("UpdateStatus", "Availability status updated successfully")
                // Update UI or perform any other actions here if needed
            }
            .addOnFailureListener { e ->
                // Handle failure, e.g., show an error message
                Log.e("UpdateStatus", "Error updating availabilityStatus", e)
            }
    }

}