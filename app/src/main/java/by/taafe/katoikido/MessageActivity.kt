package by.taafe.katoikido

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.taafe.katoikido.adapters.MessageAdapter
import by.taafe.katoikido.classes.Chat
import by.taafe.katoikido.classes.Message
import by.taafe.katoikido.utils.Loader
import by.taafe.katoikido.utils.Noty
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class MessageActivity : AppCompatActivity() {

    private lateinit var sendToPhoneNumber: String
    private lateinit var sendToName: String
    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var messageInput : TextInputLayout
    lateinit var listLoader: ImageView
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val messageList = ArrayList<Message>()
    private val context : Context = this

    override fun onStart() {
        super.onStart()
        if(sendToPhoneNumber.isNotEmpty()){
            Noty.notDisplayedPhone = sendToPhoneNumber
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Noty.init(currentUser?.phoneNumber.toString(), applicationContext)
        Noty.notDisplayedPhone = "undef"
    }

    override fun onPause(){
        super.onPause()
        Noty.notDisplayedPhone = "undef"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        messageRecyclerView = findViewById(R.id.messageRecyclerView)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true;
        messageRecyclerView.layoutManager = layoutManager

        messageInput = findViewById(R.id.messageInput)
        listLoader = findViewById(R.id.listLoader)

        Glide.with(this).load("").error(Loader.create(this, 86f, 12f)).into(listLoader)

        val arguments = intent.extras
        if(arguments != null){
            sendToName = arguments.getString("sendTo").toString()
            sendToPhoneNumber = arguments.getString("sendToPhone").toString()
            Noty.notDisplayedPhone = sendToPhoneNumber
        }

        this@MessageActivity.title = sendToName

        val phoneFrom = currentUser?.phoneNumber?.replace("+", "")?.toLong()!!
        val phoneTo = sendToPhoneNumber.replace("+", "").toLong()
        val chatPartReference = (phoneFrom + phoneTo).toString()

        val database = Firebase.database("https://katoikido-default-rtdb.europe-west1.firebasedatabase.app/")
        val chatFullReference = database.getReference("chats").child(chatPartReference)

        val messageListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                messageList.clear()
                for (postSnapshot in dataSnapshot.children) {
                    val message: Message = postSnapshot.getValue<Message>()!!
                    messageList.add(message)
                }

                val adapter = MessageAdapter(messageList, context)
                messageRecyclerView.adapter = adapter
                listLoader.visibility = View.GONE
                messageRecyclerView.smoothScrollToPosition(messageRecyclerView.adapter!!.itemCount)

            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        chatFullReference.addValueEventListener(messageListener)

        val chatInfoReference = database.getReference("chats").child("info").child(chatPartReference)
        val chat = Chat()
        chat.id = chatPartReference
        chat.members[currentUser.phoneNumber.toString()] = currentUser.displayName.toString()
        chat.members[sendToPhoneNumber] = sendToName
        chatInfoReference.setValue(chat)
        
        messageInput.setEndIconOnClickListener {

            if(!messageInput.editText?.text.toString().isEmpty()){
                val key = chatFullReference.child(chatPartReference).push().key
                if (key == null) {
                    Log.w("TAG", "Couldn't get push key for posts")
                }

                val message = Message()
                message.id = key!!
                message.sender = currentUser.displayName.toString()
                message.text = messageInput.editText?.text.toString()
                message.phone = currentUser.phoneNumber.toString()
                message.chatId = chatPartReference

                messageInput.editText?.setText("")
                chatFullReference.child(key).setValue(message)

                val noty = Noty()
                val notyRef = Firebase.database("https://katoikido-default-rtdb.europe-west1.firebasedatabase.app/")
                    .getReference("notifications").child(sendToPhoneNumber)
                noty.message = message.text
                noty.phoneTo = sendToPhoneNumber
                noty.phoneFrom = message.phone
                noty.sender = message.sender
                noty.sendTo = sendToName
                notyRef.setValue(noty)
                notyRef.setValue(null)
            }

        }
    }

}