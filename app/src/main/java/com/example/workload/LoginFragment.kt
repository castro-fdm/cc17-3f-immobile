package com.example.workload

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController

class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_login, container, false)

        val signupButton = layout.findViewById<Button>(R.id.signupButton)

        signupButton.setOnClickListener {
            view -> view.findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }

        return layout
    }
}