package by.taafe.katoikido

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
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
    lateinit var infoText : TextView

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 100){
            //postImage.setImageURI(data?.data) // handle chosen image

            val selectedImage: Uri? = data?.data
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
            openGalleryForImage()
        }
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