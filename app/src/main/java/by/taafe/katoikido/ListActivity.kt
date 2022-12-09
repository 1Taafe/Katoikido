package by.taafe.katoikido

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class ListActivity : AppCompatActivity() {

    val postList = ArrayList<Post>()
    lateinit var postRecyclerView: RecyclerView
    lateinit var listLoader: ImageView
    lateinit var addPostButton: FloatingActionButton
    lateinit var searchInput: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        postRecyclerView = findViewById(R.id.postRecyclerView)
        postRecyclerView.layoutManager = LinearLayoutManager(this)
        listLoader = findViewById(R.id.listLoader)
        addPostButton = findViewById(R.id.addPostButton)
        searchInput = findViewById(R.id.searchInput)

        searchInput.editText?.doOnTextChanged { inputText, _, _, _ ->
            //val searchEx = Regex(inputText.toString(), RegexOption.IGNORE_CASE)
            val searchPostList = ArrayList<Post>()
            for (post in postList){
                if(post.toString().contains(inputText.toString(), true)){
                    searchPostList.add(post)
                }
            }
            val adapter = PostAdapter(searchPostList, applicationContext)
            postRecyclerView.adapter = adapter
            this@ListActivity.title = "Объявления (${searchPostList.size})"
        }

        addPostButton.isEnabled = false

        Glide.with(this).load("").error(Loader.create(this, 86f, 12f)).into(listLoader)


        val postReference = Firebase.database("https://katoikido-default-rtdb.europe-west1.firebasedatabase.app/").reference.child("posts")

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                postList.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val post: Post = postSnapshot.getValue<Post>()!!
                    postList.add(post)
                }
                postList.reverse()
                val adapter = PostAdapter(postList, applicationContext)
                listLoader.isVisible = false
                postRecyclerView.adapter = adapter
                this@ListActivity.title = "Объявления (${postList.size})"
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        postReference.addValueEventListener(postListener)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort -> {
                Toast.makeText(this, "скоро сделаем", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    

    override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser != null){
            updateUI(UI.LogoutState)
        }
        else{
            updateUI(UI.LoginState)
        }
    }

    enum class UI {
        LoginState, LogoutState
    }

    private fun updateUI(state: UI){
        when (state) {
            UI.LoginState -> {
                addPostButton.isEnabled = false
            }
            UI.LogoutState -> {
                addPostButton.isEnabled = true
            }
        }
    }

    fun openAddActivity(view: View) {
        val addPostIntent = Intent(this, AddPostActivity::class.java)
        addPostIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(addPostIntent)
    }

}