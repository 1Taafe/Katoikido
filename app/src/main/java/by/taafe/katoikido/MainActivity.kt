package by.taafe.katoikido

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var editProfileButton : Button
    private lateinit var helloName : TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth
        loginButton = findViewById(R.id.loginButton)
        editProfileButton = findViewById(R.id.editProfileButton)
        helloName = findViewById(R.id.helloName)

        CloudinaryManager.init(this)
    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null){
            //Toast.makeText(this, "Welcome " + currentUser.displayName, Toast.LENGTH_SHORT).show()
            updateLoginButton(UI.LogoutState)
        }
        else{
            updateLoginButton(UI.LoginState)
        }
    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser
            Toast.makeText(this, "Вход выполнен", Toast.LENGTH_SHORT).show()
            if(user?.displayName.isNullOrEmpty()){
                val editProfileIntent = Intent(this, EditProfileActivity::class.java)
                startActivity(editProfileIntent)
                Toast.makeText(this, "Похоже, что вы здесь впервые", Toast.LENGTH_SHORT).show()
            }
            updateLoginButton(UI.LogoutState)
        } else {
            Toast.makeText(this, "Не удалось авторизоваться", Toast.LENGTH_SHORT).show()
        }
    }

    fun LoginClick(view: View) {
        if(auth.currentUser != null){
            AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener {
                    Toast.makeText(this, "Выход выполнен", Toast.LENGTH_SHORT).show()
                    updateLoginButton(UI.LoginState)
                }
        }
        else{
            val providers = arrayListOf(
                AuthUI.IdpConfig.PhoneBuilder().build())

            val signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build()
            signInLauncher.launch(signInIntent)
        }
    }

    enum class UI {
        LoginState, LogoutState
    }

    fun updateLoginButton(state: UI){
        when (state) {
            UI.LoginState -> {
                loginButton.text = "Войти"
                editProfileButton.isVisible = false;
                helloName.text = "Незнакомец"
            }
            UI.LogoutState -> {
                loginButton.text = "Выйти"
                editProfileButton.isVisible = true;
                helloName.text = auth.currentUser?.displayName
            }
        }
    }

    fun EditProfileClick(view: View) {
        val currentUser = auth.currentUser
        if(currentUser != null && !currentUser.displayName.isNullOrEmpty()){
            val editProfileIntent = Intent(this, EditProfileActivity::class.java)
            startActivity(editProfileIntent)
        }
    }

    fun ListActivityClick(view: View) {
        val editProfileIntent = Intent(this, ListActivity::class.java)
        editProfileIntent.flags = FLAG_ACTIVITY_SINGLE_TOP
        startActivity(editProfileIntent)
    }
}