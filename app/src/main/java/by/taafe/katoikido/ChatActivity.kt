package by.taafe.katoikido

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private val context : Context = this
    private lateinit var chatRecyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        chatRecyclerView.layoutManager = LinearLayoutManager(this)

        val database = Firebase.database("https://katoikido-default-rtdb.europe-west1.firebasedatabase.app/")
        val chatInfoReference = database.getReference("chats").child("info")

        chatInfoReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (chatSnapshot in dataSnapshot.children) {
                    val chat = chatSnapshot.getValue<Chat>()!!
                    chatList.add(chat)
                }
                val adapter = ChatAdapter(chatList, context)
                chatRecyclerView.adapter = adapter
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                //Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
                // ...
            }
        })



//        val chat = Chat()
//        chat.id = 750894906677L.toString()
//        chat.members.put("+375447127778", "Dmitro")
//        chat.members.put("+375447778899", "text")
//        chatList.add(chat)
//        val adapter = ChatAdapter(chatList, context)
//        chatRecyclerView.adapter = adapter

    }
}