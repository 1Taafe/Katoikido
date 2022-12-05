package by.taafe.katoikido

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text


class MainActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var editProfileButton : Button
    private lateinit var helloName : TextView
    lateinit var homeImage : ImageView
    lateinit var welcomeText : TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth
        loginButton = findViewById(R.id.loginButton)
        editProfileButton = findViewById(R.id.editProfileButton)
        helloName = findViewById(R.id.helloName)
        homeImage = findViewById(R.id.homeImageView)
        welcomeText = findViewById(R.id.welcomeText)

        homeImage.setOnClickListener {
            if(auth.currentUser != null){
                homeImage.isClickable = false
                val circularProgressDrawable = CircularProgressDrawable(this)
                circularProgressDrawable.strokeWidth = 14f
                circularProgressDrawable.centerRadius = 424f
                circularProgressDrawable.setColorSchemeColors(Color.parseColor("#FF6200EE"), Color.parseColor("#C700FE"))
                circularProgressDrawable.start()

                Glide.with(this).load("https://thiscatdoesnotexist.com/")
                    .listener(object : RequestListener<Drawable?> {
                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            homeImage.isClickable = true
                            return false
                        }

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            homeImage.isClickable = true
                            Toast.makeText(applicationContext, "Не удалось загрузить котика :( \n Нажмите на картинку, чтобы повторить попытку.", Toast.LENGTH_SHORT).show()
                            return false
                        }
                    })
                    .placeholder(circularProgressDrawable)
                    .apply(RequestOptions.circleCropTransform()
                        .signature(ObjectKey(System.currentTimeMillis())))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_baseline_catching_pokemon_24)
                    .into(homeImage)


            }
        }

        CloudinaryManager.init(this)
    }

    public override fun onStart() {
        super.onStart()
        homeImage.isClickable = false
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
                editProfileButton.isEnabled = false;
                helloName.text = "Незнакомец"
                homeImage.setImageResource(R.drawable.ic_baseline_catching_pokemon_24)
                welcomeText.text = getString(R.string.welcomeUnknown);
            }
            UI.LogoutState -> {
                loginButton.text = "Выйти"
                editProfileButton.isEnabled = true;
                helloName.text = auth.currentUser?.displayName
                welcomeText.text = getString(R.string.welcomeUser);

                val circularProgressDrawable = CircularProgressDrawable(this)
                circularProgressDrawable.strokeWidth = 14f
                circularProgressDrawable.centerRadius = 424f
                circularProgressDrawable.setColorSchemeColors(Color.parseColor("#FF6200EE"), Color.parseColor("#C700FE"))
                circularProgressDrawable.start()

                Glide.with(this).load("https://thiscatdoesnotexist.com/")
                    .listener(object : RequestListener<Drawable?> {
                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            homeImage.isClickable = true
                            return false
                        }

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable?>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            homeImage.isClickable = true
                            Toast.makeText(applicationContext, "Не удалось загрузить котика :( \n Нажмите на картинку, чтобы повторить попытку.", Toast.LENGTH_SHORT).show()
                            return false
                        }
                    })
                    .placeholder(circularProgressDrawable)
                    .apply(RequestOptions.circleCropTransform()
                        .signature(ObjectKey(System.currentTimeMillis())))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_baseline_catching_pokemon_24)
                    .into(homeImage)
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