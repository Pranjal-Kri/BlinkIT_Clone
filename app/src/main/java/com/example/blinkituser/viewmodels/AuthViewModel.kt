package com.example.blinkituser.viewmodels

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.example.blinkituser.Utils
import com.example.blinkituser.activity.MainActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.TimeUnit

class AuthViewModel : ViewModel() {

    private val _verificationID = MutableStateFlow<String?>(null)
    private val _otpSent = MutableStateFlow(false)
    private val _isSignedInScuccessfully = MutableStateFlow(false)
    val isSignedInSuccessfully = _isSignedInScuccessfully
    val otpSent = _otpSent

    fun sendOTP(userNumber : String, activity : Activity){
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

            }

            override fun onVerificationFailed(e: FirebaseException) {

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                _verificationID.value = verificationId
                _otpSent.value = true
            }
        }
        val options = PhoneAuthOptions.newBuilder(Utils.getAuthentication())
            .setPhoneNumber("+91$userNumber") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(activity) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

     fun signInWithPhoneAuthCredential(otp: String, userNumber: String) {
        val credential = PhoneAuthProvider.getCredential(_verificationID.value.toString(), otp)
        Utils.getAuthentication().signInWithCredential(credential)
            .addOnCompleteListener{ task ->
                if (task.isSuccessful) {
                    _isSignedInScuccessfully.value = true
                } else {

                }
            }
    }
}