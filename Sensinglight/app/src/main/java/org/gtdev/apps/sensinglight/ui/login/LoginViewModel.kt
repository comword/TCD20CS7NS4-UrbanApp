package org.gtdev.apps.sensinglight.ui.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth

import org.gtdev.apps.sensinglight.R

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>().applicationContext

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(auth: FirebaseAuth, username: String, password: String) {
        // can be launched in a separate asynchronous job
//        val result = loginRepository.login(username, password)
//
//        if (result is Result.Success) {
//            _loginResult.value =
//                LoginResult(success = LoggedInUserView(displayName = result.data.displayName))
//        } else {
//            _loginResult.value = LoginResult(error = R.string.login_failed)
//        }
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
//                    // [START_EXCLUDE]
//                    checkForMultiFactorFailure(task.exception!!)
//                    // [END_EXCLUDE]
                }

                // [START_EXCLUDE]
                if (!task.isSuccessful) {
//                    binding.status.setText(R.string.auth_failed)
                }
            }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value = LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value = LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value = LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    companion object {
        private val TAG = LoginViewModel::class.java.simpleName
    }
}