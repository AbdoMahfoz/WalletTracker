package com.example.wallettracker.ui.auth.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import com.example.wallettracker.R
import com.example.wallettracker.databinding.FragmentLoginBinding
import com.example.wallettracker.logic.interfaces.RegisterResult
import com.example.wallettracker.viewModels.LoginViewModel
import com.example.wallettracker.viewModels.ViewModelFactory

class LoginFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding : FragmentLoginBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login, container, false
        )
        val viewModel = ViewModelFactory.of(this).get(LoginViewModel::class.java)
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.loading.observe(viewLifecycleOwner, Observer {
            if(it) {
                binding.emailEditText.isEnabled = false
                binding.passwordEditText.isEnabled = false
                binding.loginButton.isEnabled = false
                binding.registerButton.isEnabled = false
                binding.loadingIndicator.visibility = View.VISIBLE
            } else {
                binding.emailEditText.isEnabled = true
                binding.passwordEditText.isEnabled = true
                binding.loginButton.isEnabled = true
                binding.registerButton.isEnabled = true
                binding.loadingIndicator.visibility = View.GONE
            }
        })
        viewModel.authComplete.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMainActivity())
                viewModel.authCompleteHandled()
                activity?.finish()
            }
        })
        viewModel.isLoginErred.observe(viewLifecycleOwner, Observer {
            if(it) {
                binding.errorText.text = resources.getText(R.string.error_login)
                binding.errorText.visibility = View.VISIBLE
            } else {
                binding.errorText.visibility = View.GONE
            }
        })
        viewModel.registerResult.observe(viewLifecycleOwner, Observer {
            when(it) {
                RegisterResult.Ok -> binding.errorText.visibility = View.GONE
                RegisterResult.EmailExists -> {
                    binding.errorText.text = resources.getText(R.string.error_register_email_exists)
                    binding.errorText.visibility = View.VISIBLE
                }
                RegisterResult.InvalidEmail -> {
                    binding.errorText.text = resources.getText(R.string.error_register_email_invalid)
                    binding.errorText.visibility = View.VISIBLE
                }
                RegisterResult.WeakPassword -> {
                    binding.errorText.text = resources.getText(R.string.error_register_password)
                    binding.errorText.visibility = View.VISIBLE
                }
                else -> binding.errorText.visibility = View.GONE
            }
        })
        binding.loginButton.setOnClickListener {
            viewModel.logIn(binding.emailEditText.text.toString(), binding.passwordEditText.text.toString())
        }
        binding.registerButton.setOnClickListener {
            viewModel.register(binding.emailEditText.text.toString(), binding.passwordEditText.text.toString())
        }
        return binding.root
    }

}
