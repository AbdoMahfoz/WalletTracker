package com.abdomahfoz.wallettracker.ui.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

import com.abdomahfoz.wallettracker.R
import com.abdomahfoz.wallettracker.databinding.FragmentLoginBinding
import com.abdomahfoz.wallettracker.logic.interfaces.RegisterResult
import com.abdomahfoz.wallettracker.viewModels.LoginViewModel
import com.abdomahfoz.wallettracker.viewModels.ViewModelFactory

class LoginFragment : Fragment() {
    private val args by navArgs<LoginFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding : FragmentLoginBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login, container, false
        )
        val viewModel = ViewModelFactory.of(this).get(LoginViewModel::class.java)
        viewModel.handleIsReLogin(args.isReLogin)
        if(args.isReLogin) {
            Toast.makeText(context, "Login with old account required", Toast.LENGTH_SHORT).show()
            binding.registerButton.isEnabled = false
        }
        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.loading.observe(viewLifecycleOwner, Observer {
            if(it) {
                binding.emailEditText.visibility = View.GONE
                binding.passwordEditText.visibility = View.GONE
                binding.loginButton.visibility = View.GONE
                binding.registerButton.visibility = View.GONE
                binding.loadingIndicator.visibility = View.VISIBLE
            } else {
                binding.emailEditText.visibility = View.VISIBLE
                binding.passwordEditText.visibility = View.VISIBLE
                binding.loginButton.visibility = View.VISIBLE
                binding.registerButton.visibility = View.VISIBLE
                binding.loadingIndicator.visibility = View.GONE
            }
        })
        viewModel.authComplete.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                if(args.isReLogin){
                    findNavController().popBackStack()
                } else {
                    findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToMainFragment())
                }
                viewModel.authCompleteHandled()
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
            hideKeyboard()
            viewModel.logIn(binding.emailEditText.text.toString(), binding.passwordEditText.text.toString())
        }
        binding.registerButton.setOnClickListener {
            hideKeyboard()
            viewModel.register(binding.emailEditText.text.toString(), binding.passwordEditText.text.toString())
        }
        return binding.root
    }
    private fun hideKeyboard() {
        val x : InputMethodManager? = getSystemService(requireContext(), InputMethodManager::class.java)
        x?.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}
