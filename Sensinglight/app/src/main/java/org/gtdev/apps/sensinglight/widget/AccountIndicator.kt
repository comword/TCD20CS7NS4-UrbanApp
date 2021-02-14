package org.gtdev.apps.sensinglight.widget

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.gtdev.apps.sensinglight.R
import org.gtdev.apps.sensinglight.ui.login.LoginActivity

class AccountIndicator @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null) :
    RelativeLayout(context, attrs), OnTouchListener {
    var avatar: ImageView
    var txAccEmail: TextView
    var txAccName: TextView

    private var auth: FirebaseAuth = Firebase.auth

    fun updateAccount(accEmail: String?, accName: String?) {
        txAccEmail.text = accEmail
        txAccName.text = accName
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_UP -> {
                val itLogin = Intent(context, LoginActivity::class.java)
                context.startActivity(itLogin)
            }
        }
        return false
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.account_row, this)
        avatar = findViewById(R.id.account_image)
        txAccEmail = findViewById(R.id.account_id)
        txAccName = findViewById(R.id.account_name)
        isFocusable = true
        isClickable = true
        setOnTouchListener(this)

        val user = auth.currentUser
        if (user != null) {
            updateAccount(user.email, user.displayName)
        }
    }
}