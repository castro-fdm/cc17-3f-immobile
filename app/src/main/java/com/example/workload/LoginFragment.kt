package com.example.workload

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs

class LoginFragment : Fragment() {
    private val args: LoginFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_login, container, false)

        val signupButton = layout.findViewById<Button>(R.id.signupButton)
        val loginButton = layout.findViewById<Button>(R.id.loginButton)

        // Display the role (optional - for testing or UI feedback)
        val selectedRole = args.selectedRole
        println("Selected Role: $selectedRole") // Debugging log

        // Handle signup navigation
        signupButton.setOnClickListener {
            // Pass the selectedRole back to SignupFragment
            val action = LoginFragmentDirections.actionLoginFragmentToSignupFragment(selectedRole)
            view?.findNavController()?.navigate(action)
        }

        // Handle login logic
        loginButton.setOnClickListener {
            navigateToDashboard(selectedRole)
        }

        return layout
    }

    private fun navigateToDashboard(role: String) {
        val action = when (role) {
            "requestee" -> LoginFragmentDirections.actionLoginFragmentToRequesteeFragment()
            "servitor" -> LoginFragmentDirections.actionLoginFragmentToServitorFragment()
            else -> throw IllegalArgumentException("Invalid role: $role")
        }
        view?.findNavController()?.navigate(action)
    }
}
