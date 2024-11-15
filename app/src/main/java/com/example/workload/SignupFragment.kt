package com.example.workload

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs

class SignupFragment : Fragment() {

    // Retrieve arguments passed to this fragment (if any)
    private val args: SignupFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_signup, container, false)

        val confirmButton = layout.findViewById<Button>(R.id.confirmButton)

        // Get the role passed from the LoginFragment
        val selectedRole = args.selectedRole

        confirmButton.setOnClickListener {
            // Pass the selectedRole back to LoginFragment
            val action = SignupFragmentDirections.actionSignupFragmentToLoginFragment(selectedRole)
            view?.findNavController()?.navigate(action)
        }

        return layout
    }
}
