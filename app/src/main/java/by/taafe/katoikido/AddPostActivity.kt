package by.taafe.katoikido

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.File
import java.io.FileInputStream


class AddPostActivity : AppCompatActivity() {
    lateinit var titleInput: TextInputLayout
    private lateinit var postTextInput : TextInputLayout
    lateinit var postTypeText : AutoCompleteTextView
    lateinit var petTypeText : AutoCompleteTextView
    private lateinit var postImage : ImageView
    private var postImagePath = Post.undefined
    lateinit var savePostButton : Button
    private lateinit var infoText : TextView

    private fun openGalleryForImage() {
        val i = Intent();
        i.type = "image/*";
        i.action = Intent.ACTION_PICK;
        launchSomeActivity.launch(i);
    }

    private var launchSomeActivity = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode
            == RESULT_OK
        ) {
            val data = result.data
            val selectedImage = data!!.data
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor: Cursor? = contentResolver.query(selectedImage!!, filePathColumn, null, null, null)
            cursor?.moveToFirst()
            val columnIndex: Int? = cursor?.getColumnIndex(filePathColumn[0])
            postImagePath = cursor?.getString(columnIndex!!).toString()
            cursor?.close()

            Glide.with(this).load(postImagePath)
                .apply(RequestOptions.circleCropTransform())
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(postImage)

            Toast.makeText(this, postImagePath, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        titleInput = findViewById(R.id.titleInput)
        postTextInput = findViewById(R.id.postTextInput)
        postTypeText = findViewById(R.id.postTypeText)
        petTypeText = findViewById(R.id.petTypeText)
        postImage = findViewById(R.id.postImage)
        savePostButton = findViewById(R.id.savePostButton)
        infoText = findViewById(R.id.infoText)

        postImage.setOnClickListener(){
            if(checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)){
                openGalleryForImage()
            }
            else{
                requestPermissionsLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            }

        }
    }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                openGalleryForImage()
            } else Toast.makeText(
                this,
                "Не удалось получить разрешения.",
                Toast.LENGTH_LONG
            ).show()
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun AddPost(view: View) {
        val database = Firebase.database("https://katoikido-default-rtdb.europe-west1.firebasedatabase.app/")
        val myRef = database.getReference("posts")

        val title = titleInput.editText?.text.toString()
        val postText = postTextInput.editText?.text.toString()

        val key = myRef.child("posts").push().key
        if (key == null) {
            Log.w("TAG", "Couldn't get push key for posts")
            return
        }

        val post = Post(key, title, postText)
        post.type = postTypeText.text.toString()
        post.petType = petTypeText.text.toString()
        post.imageUrl = "images/${post.id}.jpg"

        if(post.type.isEmpty() || post.petType.isEmpty()
            || post.title.isEmpty() || post.text.isEmpty() || postImagePath == Post.undefined
        ){
            Toast.makeText(this, "Заполните все поля и повторите попытку!", Toast.LENGTH_SHORT).show()
        }

        else{

            postImage.isClickable = false
            titleInput.isEnabled = false
            postTypeText.isEnabled = false

            postTypeText.dropDownHeight = 0

            petTypeText.isEnabled = false

            petTypeText.dropDownHeight = 0

            postTextInput.isEnabled = false
            savePostButton.isEnabled = false

            post.ownerName = FirebaseAuth.getInstance().currentUser?.displayName.toString()
            post.ownerPhone = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()

            val postImageRef = Firebase.storage.reference.child("images/${post.id}.jpg")
            val stream = FileInputStream(File(postImagePath))
            var uploadTask = postImageRef.putStream(stream)

            Glide.with(this).load("").error(Loader.create(this, 84f, 16f)).into(postImage)

            uploadTask.addOnFailureListener {
                Toast.makeText(this, "Не удалось опубликовать объявление!", Toast.LENGTH_SHORT).show()
                Glide.with(this).load(postImagePath)
                    .apply(RequestOptions.circleCropTransform())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(postImage)
            }.addOnSuccessListener {
                myRef.child(key).setValue(post)

                Toast.makeText(this, "Объявление опубликовано", Toast.LENGTH_SHORT).show()

                val listActivityIntent = Intent(this, ListActivity::class.java)
                listActivityIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(listActivityIntent)
            }
                .addOnProgressListener() {
                    infoText.text = getText(R.string.postProgressText)
                }
        }
    }
}