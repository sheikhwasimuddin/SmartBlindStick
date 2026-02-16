package com.example.smartblindstick

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SettingsFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        val uid = user?.uid ?: return view

        val databaseRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(uid)

        val nameEdit = view.findViewById<EditText>(R.id.editName)
        val emailEdit = view.findViewById<EditText>(R.id.editEmail)
        val phoneEdit = view.findViewById<EditText>(R.id.editPhone)
        val cityEdit = view.findViewById<EditText>(R.id.editCity)
        val addressEdit = view.findViewById<EditText>(R.id.editAddress)
        val contact1Edit = view.findViewById<EditText>(R.id.contact1Edit)
        val contact2Edit = view.findViewById<EditText>(R.id.contact2Edit)
        val saveButton = view.findViewById<Button>(R.id.saveProfileButton)

        // 🔥 Load existing data from Firebase
        databaseRef.get().addOnSuccessListener { snapshot ->
            nameEdit.setText(snapshot.child("name").value?.toString() ?: "")
            emailEdit.setText(snapshot.child("email").value?.toString() ?: "")
            phoneEdit.setText(snapshot.child("phone").value?.toString() ?: "")
            cityEdit.setText(snapshot.child("city").value?.toString() ?: "")
            addressEdit.setText(snapshot.child("address").value?.toString() ?: "")
            contact1Edit.setText(snapshot.child("emergencyContact1").value?.toString() ?: "")
            contact2Edit.setText(snapshot.child("emergencyContact2").value?.toString() ?: "")
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
        }

        // 🔥 Save updated data
        saveButton.setOnClickListener {

            val name = nameEdit.text.toString().trim()
            val email = emailEdit.text.toString().trim()
            val phone = phoneEdit.text.toString().trim()
            val city = cityEdit.text.toString().trim()
            val address = addressEdit.text.toString().trim()
            val contact1 = contact1Edit.text.toString().trim()
            val contact2 = contact2Edit.text.toString().trim()

            // ✅ Simple Validation
            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(requireContext(), "Name & Email required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveButton.isEnabled = false
            saveButton.text = "Saving..."

            val userMap = mapOf(
                "name" to name,
                "email" to email,
                "phone" to phone,
                "city" to city,
                "address" to address,
                "emergencyContact1" to contact1,
                "emergencyContact2" to contact2
            )

            // 🔥 Update Realtime Database
            databaseRef.updateChildren(userMap)
                .addOnSuccessListener {

                    // 🔥 Update Email in FirebaseAuth (if changed)
                    if (user?.email != email) {
                        user?.updateEmail(email)
                    }

                    Toast.makeText(
                        requireContext(),
                        "Profile Updated Successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    saveButton.isEnabled = true
                    saveButton.text = "Save Profile"
                }
                .addOnFailureListener {
                    Toast.makeText(
                        requireContext(),
                        "Update Failed: ${it.message}",
                        Toast.LENGTH_LONG
                    ).show()

                    saveButton.isEnabled = true
                    saveButton.text = "Save Profile"
                }
        }

        return view
    }
}