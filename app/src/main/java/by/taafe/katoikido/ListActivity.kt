package by.taafe.katoikido

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.taafe.katoikido.adapters.PostAdapter
import by.taafe.katoikido.classes.Post
import by.taafe.katoikido.database.DatabaseHelper
import by.taafe.katoikido.utils.Loader
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
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
    val currentUser = FirebaseAuth.getInstance().currentUser
    lateinit var sortView: NavigationView
    lateinit var drawer: DrawerLayout
    lateinit var currentPostsMenuItem: MenuItem
    lateinit var currentPetsMenuItem: MenuItem

    @SuppressLint("StaticFieldLeak")
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var Link: ListActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        postRecyclerView = findViewById(R.id.postRecyclerView)
        postRecyclerView.layoutManager = LinearLayoutManager(this)
        listLoader = findViewById(R.id.listLoader)
        addPostButton = findViewById(R.id.addPostButton)
        searchInput = findViewById(R.id.searchInput)
        sortView = findViewById(R.id.sortView)
        drawer = findViewById(R.id.drawerLayout)
        Link = this

        sortView.setNavigationItemSelectedListener { menuItem ->
            //menuItem.isChecked = true
            //drawer.closeDrawer(Gravity.RIGHT)

            val groupId: Int = menuItem.groupId
            if (groupId == R.id.sortGroup1) {
                if (currentPostsMenuItem != null) {
                    currentPostsMenuItem.isChecked = false
                }
                currentPostsMenuItem = menuItem
            } else if (groupId == R.id.sortGroup2) {
                if (currentPetsMenuItem != null) {
                    currentPetsMenuItem.isChecked = false
                }
                currentPetsMenuItem = menuItem
            }
            menuItem.isChecked = true

            val id: Int = menuItem.itemId
            when(id){
                R.id.sortAllPosts -> {
                    currentPostsMenuItem = menuItem
                }
                R.id.sortMyPosts -> {
                    currentPostsMenuItem = menuItem
                }
                R.id.sortFavPosts -> {
                    currentPostsMenuItem = menuItem
                }
                else -> {
                    currentPetsMenuItem = menuItem
                }
            }

            doSearch()

            //drawer.closeDrawer(GravityCompat.START)
            false
        }

        currentPostsMenuItem = sortView.menu.findItem(R.id.sortAllPosts)
        currentPostsMenuItem.isChecked = true
        currentPetsMenuItem = sortView.menu.findItem(R.id.sortAllPets)
        currentPetsMenuItem.isChecked = true

        searchInput.editText?.doOnTextChanged { _, _, _, _ ->
            doSearch()
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
                doSearch()
                listLoader.visibility = View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        postReference.addValueEventListener(postListener)

    }

    fun doSearch(){

        val inputText = searchInput.editText?.text.toString()
        val filterPostList = ArrayList<Post>()
        for (post in postList){
            if(post.toString().contains(inputText, true)){
                filterPostList.add(post)
            }
        }

        when(currentPostsMenuItem.itemId){
            R.id.sortAllPosts -> {

            }
            R.id.sortMyPosts -> {
                filterPostList.removeIf { post -> post.ownerPhone != currentUser?.phoneNumber}
            }
            R.id.sortFavPosts -> {
                Post.Favorites = DatabaseHelper.getFavoritePosts()
                filterPostList.clear()
                for (post in Post.Favorites){
                    filterPostList.add(post)
                }
            }
        }

        when(currentPetsMenuItem.itemId){
            R.id.sortAllPets -> {

            }
            R.id.sortCapybaraPets -> {
                filterPostList.removeIf {post -> post.petType != "Капибара"}
            }
            R.id.sortCatPets -> {
                filterPostList.removeIf {post -> post.petType != "Кот"}
            }
            R.id.sortDogPets -> {
                filterPostList.removeIf {post -> post.petType != "Собака"}
            }
            R.id.sortHamsterPets -> {
                filterPostList.removeIf {post -> post.petType != "Хомяк"}
            }
            R.id.sortParrotPets -> {
                filterPostList.removeIf {post -> post.petType != "Попугай"}
            }
            R.id.sortFishPets -> {
                filterPostList.removeIf {post -> post.petType != "Рыбка"}
            }
            R.id.sortOtherPets -> {
                filterPostList.removeIf {post -> post.petType != "Другой"}
            }
        }

        val adapter = PostAdapter(filterPostList, this)
        postRecyclerView.adapter = adapter
        this@ListActivity.title = "Объявления (${filterPostList.size})"

//        val searchPostList = ArrayList<Post>()
//        for (post in posts){
//            if(post.toString().contains(inputText, true)){
//                searchPostList.add(post)
//            }
//        }
//        val adapter = PostAdapter(searchPostList, applicationContext)
//        postRecyclerView.adapter = adapter
//        this@ListActivity.title = "Объявления (${searchPostList.size})"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.list_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sort -> {
                if(!drawer.isDrawerOpen(Gravity.RIGHT)){
                    drawer.openDrawer(Gravity.RIGHT)
                }
                else{
                    drawer.closeDrawer(Gravity.RIGHT)
                }

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