package by.taafe.katoikido

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.cloudinary.Transformation
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.firebase.ui.auth.data.model.User
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.getCredential
import com.google.firebase.auth.ktx.userProfileChangeRequest

class EditProfileActivity : AppCompatActivity() {

    lateinit var nameInput: TextInputLayout
    lateinit var imageView: ImageView
    lateinit var phoneInput: TextInputLayout
    lateinit var saveButton: Button
    lateinit var loader: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        nameInput = findViewById(R.id.nameInput)
        imageView = findViewById(R.id.imageView)
        phoneInput = findViewById(R.id.phoneInput)
        saveButton = findViewById(R.id.saveProfileButton)
        loader = findViewById(R.id.loaderView)

        if(!FirebaseAuth.getInstance().currentUser?.displayName.isNullOrEmpty()){
            nameInput.editText?.setText(FirebaseAuth.getInstance().currentUser?.displayName);
            phoneInput.editText?.setText(FirebaseAuth.getInstance().currentUser?.phoneNumber)
        }
    }

    fun saveProfile(view: View) {
        val user = FirebaseAuth.getInstance().currentUser
        val profileUpdates = userProfileChangeRequest {
                displayName = nameInput.editText?.text.toString();
                //photoUri = Uri.parse(imageURL)
        }

        if(nameInput.editText?.text?.length!! <= 1){
            nameInput.error = "Отображаемое имя не введено"
        }
        else{
            saveButton.isEnabled = false
            nameInput.isEnabled = false

            Glide.with(this).load("").error(Loader.create(this, 84f, 16f)).into(imageView)

            val mainIntent = Intent(this, MainActivity::class.java)
            mainIntent.flags = FLAG_ACTIVITY_CLEAR_TOP

            user!!.updateProfile(profileUpdates)
                .addOnCompleteListener{ task ->
                    if(task.isSuccessful){
                        Toast.makeText(this, "Профиль изменен", Toast.LENGTH_SHORT).show()
                        startActivity(mainIntent)
                    }
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Ошибка! Вероятно отсутствует подключение к интеренету.", Toast.LENGTH_SHORT).show()
                    startActivity(mainIntent)
                }
        }
    }

    fun SelectImage(view: View) {

    }


}