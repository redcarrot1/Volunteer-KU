package com.example.volunteerku.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.volunteerku.VolunteerKUApplication.Companion.user
import com.example.volunteerku.data.JWT
import com.example.volunteerku.databinding.ActivityLoginBinding
import com.example.volunteerku.dialog.LoadingDialog
import com.example.volunteerku.service.UserService

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    private var email: String = ""
    private var password: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    fun init() {
        binding.goChangePassword.setOnClickListener {
            var Intent = Intent(this, PasswordChangeCertifyActivity::class.java)
            startActivity(Intent)
        }
        binding.goSignup.setOnClickListener {
            var Intent = Intent(this, EmailCertifyActivity::class.java)
            finishAffinity()
            startActivity(Intent)
        }
        binding.loginButton.setOnClickListener {
            email = binding.getEmail.text.toString() + "@konkuk.ac.kr"
            password = binding.getPassword.text.toString()
            LoginResponse(email, password)
            binding.getEmail.text.clear()
            binding.getPassword.text.clear()
        }
    }

    fun LoginResponse(email: String, password: String) {
        val loading = LoadingDialog(this)
        loading.show()

        val userService = UserService()
        userService.setOnResponseListener(object : UserService.OnResponseListener() {
            @SuppressLint("SetTextI18n")
            override fun <T> getResponseBody(body: T, isSuccess: Boolean, err: String) {
                loading.dismiss()

                if (isSuccess) {
                    Log.d("login code", "LoginResponse: code is correct")
                    if (body is JWT) {
                        user.jwt = body.accessToken
                        user.nickname = body.nickname
                        user.major = body.major
                        user.email = body.email
                        user.introduce = body.introduce
                        user.currentVolunteerTime = body.currentVolunteerTime
                        goToRoomActivity()
                    } else {

                        Toast.makeText(applicationContext, "서버의 응답이 올바르지 않습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(applicationContext, "계정이 올바르지 않습니다!", Toast.LENGTH_SHORT).show()
                }
            }
        })

        userService.signIn(email, password)
    }

    private fun goToRoomActivity() {
        var Intent = Intent(this, MainActivity::class.java)
        startActivity(Intent)
    }

}