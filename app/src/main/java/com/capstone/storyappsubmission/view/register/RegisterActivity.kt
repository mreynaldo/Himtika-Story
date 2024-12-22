package com.capstone.storyappsubmission.view.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.capstone.storyappsubmission.R
import com.capstone.storyappsubmission.customview.EmailEditText
import com.capstone.storyappsubmission.customview.PasswordEditText
import com.capstone.storyappsubmission.data.Results
import com.capstone.storyappsubmission.databinding.ActivityRegisterBinding
import com.capstone.storyappsubmission.view.ViewModelFactory
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerButton: Button
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EmailEditText
    private lateinit var passwordEditText: PasswordEditText

    private val registerViewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        playAnimation()

        registerButton = binding.signupButton
        nameEditText = binding.nameEditText
        emailEditText = binding.emailEditText
        passwordEditText = binding.passwordEditText

        registerButton.isEnabled = false

        nameEditText.addTextChangedListener(registerTextWatcher)
        emailEditText.addTextChangedListener(registerTextWatcher)
        passwordEditText.addTextChangedListener(registerTextWatcher)

        binding.signupButton.setOnClickListener {
            setupAction()
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(300)
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(700)
        val nameTv = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(300)
        val nameEdt =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(300)
        val emailTv = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(300)
        val emailEdt =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(300)
        val passTv =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(300)
        val passEdt =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(300)

        val nameTogether = AnimatorSet().apply {
            playTogether(nameTv, nameEdt)
        }

        val emailTogether = AnimatorSet().apply {
            playTogether(emailTv, emailEdt)
        }

        val passTogether = AnimatorSet().apply {
            playTogether(passTv, passEdt)
        }

        AnimatorSet().apply {
            playSequentially(title, nameTogether, emailTogether, passTogether, signup)
            start()
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private val registerTextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validateFields()
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    private fun validateFields() {
        val name = nameEditText.text.toString()
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        val isNameValid = name.isNotEmpty()
        val isEmailValid =
            email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = password.length >= 8

        registerButton.isEnabled = isNameValid && isEmailValid && isPasswordValid
    }

    private fun setupAction() {
        val name = binding.nameEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()
        lifecycleScope.launch {
            registerViewModel.register(name, email, password)
            registerViewModel.registerResult.collect { results ->
                when (results) {
                    is Results.Loading -> {
                        showLoading(true)
                    }

                    is Results.Success -> {
                        showLoading(false)
                        Log.d("RegisterActivity", "Register success: ${results.data.message}")
                        Toast.makeText(
                            this@RegisterActivity,
                            getString(R.string.register_success) + results.data.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        AlertDialog.Builder(this@RegisterActivity).apply {
                            setTitle("Yeah! ${results.data.message}")
                            setMessage("Akun dengan $email sudah jadi nih. \nYuk, login dan bagikan keseruan mu di Himtalks")
                            setPositiveButton("Lanjut") { _, _ ->
                                finish()
                            }
                            create()
                            show()
                        }
                    }

                    is Results.Error -> {
                        showLoading(false)
                        Log.e("RegisterActivity", "Error: ${results.error}")
                        Toast.makeText(
                            this@RegisterActivity,
                            getString(R.string.register_error) + results.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
            binding.signupButton.isEnabled = false
        } else {
            binding.progressBar.visibility = View.GONE
            binding.signupButton.isEnabled = true
        }
    }
}

