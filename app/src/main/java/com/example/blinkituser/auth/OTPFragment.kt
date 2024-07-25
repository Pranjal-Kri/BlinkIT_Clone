package com.example.blinkituser.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.blinkituser.R
import com.example.blinkituser.Utils
import com.example.blinkituser.activity.MainActivity
import com.example.blinkituser.databinding.FragmentOTPBinding
import com.example.blinkituser.viewmodels.AuthViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class OTPFragment : Fragment() {
    private val viewModel : AuthViewModel by viewModels()
    private lateinit var binding : FragmentOTPBinding
    private lateinit var userNumber: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentOTPBinding.inflate(layoutInflater)
        getUserNumber()
        sendOTP()
        onBackButtonClick()
        customizingOTP()
        onLoginButtonClick()
        return binding.root
    }

    private fun onLoginButtonClick() {
        binding.btnLogin.setOnClickListener {
            Utils.showDialog(requireContext(), "Signing In...")
            val editTexts = arrayOf(binding.etOTP1, binding.etOTP2, binding.etOTP3, binding.etOTP4, binding.etOTP5, binding.etOTP6)
            val otp = editTexts.joinToString (""){ it.text.toString() }
             if(otp.length < 6){
                 Utils.showToast(requireContext(), "Please enter valid OTP")
             }
             else{
                 editTexts.forEach { it.text?.clear(); it.clearFocus() }
                 verifyOTP(otp)
             }
        }
    }

    private fun verifyOTP(otp: String) {
        viewModel.signInWithPhoneAuthCredential(otp, userNumber)
        lifecycleScope.launch{
            viewModel.isSignedInSuccessfully.collect{
                if(it){
                    Utils.hideDialog()
                    Utils.showToast(requireContext(), "Signed In Successfully...")
//                    (activity as MainActivity).navigateToHome()
                }
            }
        }
    }

    private fun sendOTP() {
        Utils.showDialog(requireContext(), "Sending OTP...")
        viewModel.apply{
            sendOTP(userNumber, requireActivity())
            lifecycleScope.launch{
                otpSent.collect{
                    if(it){
                        Utils.hideDialog()
                        Utils.showToast(requireContext(), "OTP Sent...")
                    }
                }
            }
        }

    }

    private fun onBackButtonClick() {
        binding.tpOTPFragment.setNavigationOnClickListener{
            findNavController().navigate(R.id.action_OTPFragment_to_sighInFragment)
        }
    }

    private fun customizingOTP() {
        val editTexts = arrayOf(binding.etOTP1, binding.etOTP2, binding.etOTP3, binding.etOTP4, binding.etOTP5, binding.etOTP6)
        for(i in editTexts.indices){
            editTexts[i].addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(s: Editable?) {
                    if(s?.length == 1){
                        if(i<editTexts.size-1){
                            editTexts[i+1].requestFocus()
                        }
                    }
                    else if(s?.length == 0){
                        if(i>0){
                            editTexts[i-1].requestFocus()
                        }
                    }
                }

            })
        }
    }

    private fun getUserNumber() {
        val bundle = arguments
        userNumber = bundle?.getString("number").toString()
        binding.tvUserNumber.text = userNumber
    }


}