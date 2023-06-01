package com.example.volunteerku.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.core.widget.doAfterTextChanged
import com.example.volunteerku.R
import com.example.volunteerku.VolunteerKUApplication
import com.example.volunteerku.VolunteerKUApplication.Companion.user
import com.example.volunteerku.data.EmailCertifyCodeResponse
import com.example.volunteerku.data.EmailResponse
import com.example.volunteerku.data.ExistEmailResponse
import com.example.volunteerku.databinding.ActivityPasswordChangeCertifyBinding
import com.example.volunteerku.dialog.LoadingDialog
import com.example.volunteerku.dialog.SlideUpDialog
import com.example.volunteerku.service.UserService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Timer
import kotlin.concurrent.timer

class PasswordChangeCertifyActivity : AppCompatActivity() {
    lateinit var binding: ActivityPasswordChangeCertifyBinding

    private var email: String = ""
    private var certifyCode: String = ""
    private var certifyTimer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPasswordChangeCertifyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        // 이메일 입력 들어옴
        binding.emailGetEmailEt.doAfterTextChanged {
            binding.emailRequestCodeBtn.isEnabled = !it.isNullOrBlank()
        }

        // 인증요청 버튼 클릭
        binding.emailRequestCodeBtn.setOnClickListener {
            email = binding.emailGetEmailEt.text.toString() + "@konkuk.ac.kr"
            requestCode(email)

            binding.emailRequestCountTv.visibility = View.INVISIBLE
            binding.emailGotoKonkukEmailTv.visibility = View.INVISIBLE

            binding.emailInputCodeCl.visibility = View.INVISIBLE
            binding.emailInputCodeEt.text.clear()
            binding.emailCertifyBtn.visibility = View.INVISIBLE

            binding.emailRequestCodeBtn.isEnabled = false

            if (certifyTimer != null)
                certifyTimer?.cancel()
        }

        // 건국대학교 이메일 이동 클릭
        binding.emailGotoKonkukEmailTv.setOnClickListener {
            gotoKonkukEmail()
        }

        // 인증코드 입력 들어옴
        binding.emailInputCodeEt.doAfterTextChanged {
            binding.emailCertifyBtn.isEnabled = !it.isNullOrBlank()
            binding.emailWarnNotEqualTv.visibility = View.GONE
            binding.emailInputCodeCl.background =
                ContextCompat.getDrawable(this, R.drawable.edit_text)
        }

        // 인증하기 버튼 클릭
        binding.emailCertifyBtn.setOnClickListener {
            certifyCode = binding.emailInputCodeEt.text.toString()
            isCodeCorrect(email, certifyCode)
        }
    }


    private fun gotoKonkukEmail() {
        val intentURL = Intent(Intent.ACTION_VIEW, Uri.parse("http://kumail.konkuk.ac.kr/"))
        startActivity(intentURL)
    }

    private fun requestCode(email: String) {
        // send email to server
        val loading = LoadingDialog(this)
        loading.show()

        val userService = UserService()
        userService.setOnResponseListener(object : UserService.OnResponseListener() {
            @SuppressLint("SetTextI18n")
            override fun <T> getResponseBody(body: T, isSuccess: Boolean, err: String) {
                loading.dismiss()
                binding.emailRequestCodeBtn.text = resources.getText(R.string.request_code_again)
                binding.emailRequestCodeBtn.isEnabled = true

                if (isSuccess) {
                    if (body is EmailResponse) {
                        binding.emailGotoKonkukEmailTv.visibility = View.VISIBLE
                        binding.emailInputCodeCl.visibility = View.VISIBLE
                        binding.emailCertifyBtn.visibility = View.VISIBLE

                        binding.emailRequestCountTv.visibility = View.VISIBLE
                        binding.emailRequestCountTv.text =
                            "오늘 인증 요청 횟수가 " + (5 - body.sendingCount) + "회 남았습니다."
                        startTimer(body.expireAt)
                    } else {
                        Toast.makeText(applicationContext, "서버의 응답이 올바르지 않습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast.makeText(applicationContext, err, Toast.LENGTH_SHORT).show()
                }
            }
        })

        userService.sendEmail(email)
    }

    private fun isCodeCorrect(email: String, code: String): Boolean {
        Log.d("certify code", "isCodeCorrect: email : $email, code : $code")
        val loading = LoadingDialog(this)
        loading.show()
        val userService = UserService()
        userService.setOnResponseListener(object : UserService.OnResponseListener() {
            override fun <T> getResponseBody(body: T, isSuccess: Boolean, err: String) {
                loading.dismiss()
                if (isSuccess) { // if code is correct
                    Log.d("certify code", "isCodeCorrect: code is correct")

                    if (body is EmailCertifyCodeResponse) {
                        isExistEmail(email)
                    } else {
                        Toast.makeText(applicationContext, "아닙니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    binding.emailWarnNotEqualTv.visibility = View.VISIBLE
                    binding.emailInputCodeCl.background = ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.edit_text_wrong
                    )
                    Toast.makeText(applicationContext, "적합한 코드가 아닙니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })

        userService.certifyCode(email, code)
        return true
    }

    private fun isExistEmail(email: String) {
        val loading = LoadingDialog(this)
        loading.show()
        val userService = UserService()
        userService.setOnResponseListener(object : UserService.OnResponseListener() {
            override fun <T> getResponseBody(body: T, isSuccess: Boolean, err: String) {
                loading.dismiss()
                if (isSuccess) { // if code is correct
                    Log.d("certify code", "isCodeCorrect: code is correct")

                    if (body is ExistEmailResponse) {
                        if (body.isExist) goToPasswordChangeActivity()
                        else {
                            Toast.makeText(applicationContext, "존재하는 계정이 아닙니다.", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(applicationContext, "오류", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    binding.emailWarnNotEqualTv.visibility = View.VISIBLE
                    binding.emailInputCodeCl.background = ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.edit_text_wrong
                    )
                    Toast.makeText(applicationContext, "적합한 코드가 아닙니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }).isExistEmail(email)
    }

    private fun goToLoginActivity() {
        Log.d("TAG", "init: certifyEmail")
        certifyTimer?.cancel()

        var Intent = Intent(this, LoginActivity::class.java)
        startActivity(Intent)
    }

    private fun goToPasswordChangeActivity() {
        Log.d("TAG", "init: certifyEmail")
        certifyTimer?.cancel()

        user.email = email
        var Intent = Intent(this, PasswordChangeActivity::class.java)
        startActivity(Intent)

    }

    @SuppressLint("SimpleDateFormat")
    private fun startTimer(expiredAt: String) {
        val expiredTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(expiredAt)?.time
        val now = Date(System.currentTimeMillis()).time
        var time: Long = (expiredTime?.minus(now))?.div(10) ?: 0
        Log.d("timer", "startTimer: $time, $expiredAt, $expiredTime, $now")

        certifyTimer = timer(period = 10) {
            if (time < 0) {
                runOnUiThread {
                    showTimeoutDialog()
                }
                this.cancel()
                certifyTimer = null
            }
            time--
            val min = time / 100 / 60
            val sec = (time / 100) % 60
            runOnUiThread {
                binding.emailCertifyTimer.text = String.format("%02d:%02d", min, sec)
            }
        }
    }

    private fun showTimeoutDialog() {
        val contentView: View =
            (getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.dialog_certify_timeout, null
            )
        val slideupPopup = SlideUpDialog.Builder(this)
            .setContentView(contentView)
            .create()
        slideupPopup.setCancelable(false)

        val confirmBtn = slideupPopup.findViewById<Button>(R.id.timeout_confirm_btn)
        confirmBtn.setOnClickListener {
            binding.emailRequestCountTv.visibility = View.INVISIBLE
            binding.emailGotoKonkukEmailTv.visibility = View.INVISIBLE
            binding.emailInputCodeCl.visibility = View.INVISIBLE
            binding.emailCertifyBtn.visibility = View.INVISIBLE
            binding.emailInputCodeEt.text.clear()

            slideupPopup.dismissAnim()
        }
        slideupPopup.show()
    }

}