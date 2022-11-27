package by.taafe.katoikido

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cloudinary.Transformation
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.getCredential
import com.google.firebase.auth.ktx.userProfileChangeRequest

class EditProfileActivity : AppCompatActivity() {

    lateinit var imageSrc : String
    lateinit var nameInput: TextInputLayout
    val pickImage = 100
    lateinit var imageView: ImageView
    lateinit var phoneInput: TextInputLayout
    var imageUri : Uri? = null
    var imageURL : String = ""
    var imageID : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        nameInput = findViewById(R.id.nameInput)
        imageView = findViewById(R.id.imageView)
        phoneInput = findViewById(R.id.phoneInput)

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
            user!!.updateProfile(profileUpdates)
                .addOnCompleteListener{ task ->
                    if(task.isSuccessful){
                        Toast.makeText(this, "Профиль изменен", Toast.LENGTH_SHORT).show()
                        val mainIntent = Intent(this, MainActivity::class.java)
                        mainIntent.setFlags(FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(mainIntent)
                    }
                }
        }
    }

    fun SelectImage(view: View) {
        val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(gallery, pickImage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == pickImage) {
            imageUri = data?.data
            imageView.setImageURI(imageUri)
            imageSrc = imageUri?.path.toString()
            Toast.makeText(this, imageSrc, Toast.LENGTH_SHORT).show()

            /*val requestId: String =
                MediaManager.get().upload(imageUri).callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                        // your code here
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {

                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>?) {
                        Log.d("cloudinary", resultData?.get("url") as String);
                        Log.d("cloudinary", resultData?.get("public_id") as String)
                        imageURL = resultData?.get("url") as String
                        imageID = resultData?.get("public_id") as String

                        val user = FirebaseAuth.getInstance().currentUser
                        val profileUpdates = userProfileChangeRequest {
                            photoUri = Uri.parse(imageURL)
                        }
                        user!!.updateProfile(profileUpdates)
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        // your code here
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        // your code here
                    }
                })
                    .dispatch()*/
        }
    }

}