package com.example.workload

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController

class SignupFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_signup, container, false)

        val confirmButton = layout.findViewById<Button>(R.id.confirmButton)

        confirmButton.setOnClickListener {
            view -> view.findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }

        return layout
    }
}