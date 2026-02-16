package com.example.smartblindstick

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: return view

        database = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(uid)

        val nameText = view.findViewById<TextView>(R.id.profileName)
        val emailText = view.findViewById<TextView>(R.id.profileEmail)
        val phoneText = view.findViewById<TextView>(R.id.profilePhone)
        val cityText = view.findViewById<TextView>(R.id.profileCity)
        val addressText = view.findViewById<TextView>(R.id.profileAddress)
        val contact1Text = view.findViewById<TextView>(R.id.profileContact1)
        val contact2Text = view.findViewById<TextView>(R.id.profileContact2)
        val logoutButton = view.findViewById<Button>(R.id.logoutButton)

        // 🔥 REAL-TIME LISTENER
        database.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                nameText.text = "Name: ${snapshot.child("name").value ?: "Not Set"}"
                emailText.text = "Email: ${snapshot.child("email").value ?: "Not Set"}"
                phoneText.text = "Phone: ${snapshot.child("phone").value ?: "Not Set"}"
                cityText.text = "City: ${snapshot.child("city").value ?: "Not Set"}"
                addressText.text = "Address: ${snapshot.child("address").value ?: "Not Set"}"
                contact1Text.text = "Emergency 1: ${snapshot.child("emergencyContact1").value ?: "Not Set"}"
                contact2Text.text = "Emergency 2: ${snapshot.child("emergencyContact2").value ?: "Not Set"}"
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        return view
    }
}