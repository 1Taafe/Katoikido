package by.taafe.katoikido

import android.app.NotificationChannel
import android.app.NotificationManager
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
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


class MainActivity : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var editProfileButton : Button
    private lateinit var helloName : TextView
    private lateinit var chatActivityButton: Button
    lateinit var homeImage : ImageView
    lateinit var welcomeText : TextView
    private lateinit var auth: FirebaseAuth
    private var context = this

    override fun onCreate(savedInstanceState: Bundle?) {

        val channel = NotificationChannel("1", "msg", NotificationManager.IMPORTANCE_HIGH).apply {
            lightColor = Color.BLUE
            enableLights(true)
        }
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        auth = Firebase.auth
        loginButton = findViewById(R.id.loginButton)
        editProfileButton = findViewById(R.id.editProfileButton)
        helloName = findViewById(R.id.helloName)
        homeImage = findViewById(R.id.homeImageView)
        welcomeText = findViewById(R.id.welcomeText)
        chatActivityButton = findViewById(R.id.chatActivityButton)

        if(auth.currentUser != null){
            Noty.init(auth.currentUser?.phoneNumber.toString(), applicationContext)
        }

        homeImage.setOnClickListener {
            if(auth.currentUser != null){
                homeImage.isClickable = false
                loadRandomCatImage()
            }
        }

    }

    public override fun onStart() {
        super.onStart()
        homeImage.isClickable = false
        val currentUser = auth.currentUser
        if(currentUser != null){
            updateUI(UI.LogoutState)
        }
        else{
            updateUI(UI.LoginState)
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
            Noty.init(auth.currentUser?.phoneNumber.toString(), applicationContext)
            if(user?.displayName.isNullOrEmpty()){
                val editProfileIntent = Intent(this, EditProfileActivity::class.java)
                startActivity(editProfileIntent)
                Toast.makeText(this, "Похоже, что вы здесь впервые", Toast.LENGTH_SHORT).show()
            }
            updateUI(UI.LogoutState)
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
                    updateUI(UI.LoginState)
                    Noty.dispose()
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

    fun updateUI(state: UI){
        when (state) {
            UI.LoginState -> {
                loginButton.text = "Войти"
                editProfileButton.isEnabled = false
                helloName.text = "Незнакомец"
                homeImage.setImageResource(R.drawable.ic_baseline_catching_pokemon_24)
                welcomeText.text = getString(R.string.welcomeUnknown);
                chatActivityButton.isEnabled = false
            }
            UI.LogoutState -> {
                loginButton.text = "Выйти"
                editProfileButton.isEnabled = true
                chatActivityButton.isEnabled = true
                helloName.text = auth.currentUser?.displayName
                welcomeText.text = getString(R.string.welcomeUser);
                loadRandomCatImage()
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

    private fun loadRandomCatImage(){
        Glide.with(this).load("https://thiscatdoesnotexist.com/")
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .skipMemoryCache(true)
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
                    //Toast.makeText(applicationContext, "Не удалось загрузить котика :( \n Нажмите на картинку, чтобы повторить попытку.", Toast.LENGTH_SHORT).show()
                    return false
                }
            })
            .placeholder(Loader.create(this, 300f, 16f))
            .apply(RequestOptions.circleCropTransform()
                .signature(ObjectKey(System.currentTimeMillis())))
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(R.drawable.ic_baseline_catching_pokemon_24)
            .into(homeImage)
    }

    fun chatActivityClick(view: View) {
        val editProfileIntent = Intent(this, ChatActivity::class.java)
        editProfileIntent.flags = FLAG_ACTIVITY_SINGLE_TOP
        startActivity(editProfileIntent)
    }
}