package com.example.workload

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController

class WelcomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val layout = inflater.inflate(R.layout.fragment_welcome, container, false)

        val requesteeCard = layout.findViewById<CardView>(R.id.requesteeCard)
        val servitorCard = layout.findViewById<CardView>(R.id.servitorCard)

        requesteeCard.setOnClickListener {
            navigateToLogin("requestee")
        }

        servitorCard.setOnClickListener {
            navigateToLogin("servitor")
        }

        return layout
    }

    private fun navigateToLogin(role: String) {
        val action = WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment(role)
        view?.findNavController()?.navigate(action)
    }
}
