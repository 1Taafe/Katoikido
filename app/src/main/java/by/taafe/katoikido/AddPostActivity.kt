package by.taafe.katoikido

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AddPostActivity : AppCompatActivity() {
    lateinit var titleInput: TextInputLayout
    lateinit var postTextInput : TextInputLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)

        titleInput = findViewById(R.id.titleInput)
        postTextInput = findViewById(R.id.postTextInput)
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


        val post : Post = Post(key, title, postText)
        post.ownerName = FirebaseAuth.getInstance().currentUser?.displayName.toString()
        post.ownerPhone = FirebaseAuth.getInstance().currentUser?.phoneNumber.toString()
        myRef.child(key).setValue(post)
    }
}