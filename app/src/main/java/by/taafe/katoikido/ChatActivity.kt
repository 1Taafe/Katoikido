package by.taafe.katoikido

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.taafe.katoikido.adapters.ChatAdapter
import by.taafe.katoikido.classes.Chat
import by.taafe.katoikido.utils.Loader
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ChatActivity : AppCompatActivity() {

    private val chatList = ArrayList<Chat>()
    private val currentUser = FirebaseAuth.getInstance().currentUser!!
    lateinit var listLoader: ImageView
    lateinit var chatStatus: TextView
    private val context : Context = this
    private lateinit var chatRecyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        listLoader = findViewById(R.id.listLoader)
        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        chatStatus = findViewById(R.id.chatStatus)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)

        this@ChatActivity.title = "Чаты"
        Glide.with(this).load("").error(Loader.create(this, 86f, 12f)).into(listLoader)

        val database = Firebase.database("https://katoikido-default-rtdb.europe-west1.firebasedatabase.app/")
        val chatInfoReference = database.getReference("chats").child("info")

        chatInfoReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                chatList.clear()
                for (chatSnapshot in dataSnapshot.children) {
                    val chat = chatSnapshot.getValue<Chat>()!!
                    if(chat.members.containsKey(currentUser.phoneNumber)){
                        chatList.add(chat)
                    }

                }
                val adapter = ChatAdapter(chatList, context)
                chatRecyclerView.adapter = adapter
                listLoader.visibility = View.GONE
                if(chatList.size > 0){
                    chatStatus.visibility = View.GONE
                }
                else{
                    chatStatus.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                //
            }
        })

    }
}