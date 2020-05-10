package com.example.wallettracker.ui.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import com.example.wallettracker.R
import com.example.wallettracker.databinding.FragmentAccountBinding
import com.example.wallettracker.logic.interfaces.UpdateEmailResult
import com.example.wallettracker.logic.interfaces.UpdatePasswordResult
import com.example.wallettracker.ui.MainActivity
import com.example.wallettracker.viewModels.AccountViewModel
import com.example.wallettracker.viewModels.ViewModelFactory

class AccountFragment : Fragment() {
    private lateinit var viewModel: AccountViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding : FragmentAccountBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_account, container, false
        )
        val mainActivity = activity as MainActivity
        mainActivity.setSupportActionBar(binding.toolBar)
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        viewModel = ViewModelFactory.of(this).get(AccountViewModel::class.java)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.oldEmail = viewModel.email
        binding.EditButton.setOnClickListener {
            viewModel.changeCredentials(
                binding.newEmailEditText.text.toString(), binding.newPasswordEditText.text.toString()
            )
        }
        viewModel.errorEmail.observe(viewLifecycleOwner, Observer {
            when(it) {
                UpdateEmailResult.EmailExists -> {
                    binding.errorText.visibility = View.VISIBLE
                    binding.errorText.text = resources.getText(R.string.error_register_email_exists)
                }
                UpdateEmailResult.InvalidEmail -> {
                    binding.errorText.visibility = View.VISIBLE
                    binding.errorText.text = resources.getText(R.string.error_register_email_invalid)
                }
                else -> binding.errorText.visibility = View.GONE
            }
        })
        viewModel.errorPassword.observe(viewLifecycleOwner, Observer {
            when(it) {
                UpdatePasswordResult.WeakPassword -> {
                    binding.errorText.visibility = View.VISIBLE
                    binding.errorText.text = resources.getText(R.string.error_register_password)
                }
                else -> binding.errorText.visibility = View.GONE
            }
        })
        viewModel.loading.observe(viewLifecycleOwner, Observer {
            if(it) {
                binding.newEmailEditText.isEnabled = false
                binding.newPasswordEditText.isEnabled = false
                binding.EditButton.isEnabled = false
                binding.loadingIndicator.visibility = View.VISIBLE
            } else {
                binding.newEmailEditText.isEnabled = true
                binding.newPasswordEditText.isEnabled = true
                binding.EditButton.isEnabled = true
                binding.loadingIndicator.visibility = View.GONE
            }
        })
        viewModel.navigateBack.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                findNavController().popBackStack()
                viewModel.navigateBackComplete()
            }
        })
        viewModel.navigateToLoginFail.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                findNavController().navigate(
                    AccountFragmentDirections.actionAccountFragmentToLoginFragment(true)
                )
                viewModel.navigateToLoginFailComplete()
            }
        })
        viewModel.editSuccess.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                Toast.makeText(context, "Account Edited Successfully!", Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
                viewModel.editSuccessComplete()
            }
        })
        return binding.root
    }
    override fun onStart() {
        super.onStart()
        viewModel.handleOnStart()
    }
}
