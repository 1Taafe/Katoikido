package by.taafe.katoikido

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewGroup.MarginLayoutParams
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.core.view.marginLeft
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class ChatAdapter(private val chats: List<Chat>, private val context: Context) :
    RecyclerView.Adapter<ChatAdapter.PostViewHolder>() {

    val currentUser = FirebaseAuth.getInstance().currentUser!!

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chatTitle: TextView = itemView.findViewById(R.id.chatTitle)
        val chatImage: ImageView = itemView.findViewById(R.id.chatImage)
        val chatCard: CardView = itemView.findViewById(R.id.chatCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.chat_item, parent, false)
        return PostViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val chat = chats[position]

        val phoneTo = "+" + (chat.id.toLong() - currentUser.phoneNumber.toString().replace("+", "").toLong())
        val phoneToName = chat.members[phoneTo]
        holder.chatTitle.text = phoneToName

        holder.chatCard.setOnClickListener{
            val messageActivityIntent = Intent(context, MessageActivity::class.java)
            messageActivityIntent.putExtra("sendTo", phoneToName)
            messageActivityIntent.putExtra("sendToPhone", phoneTo)
            context.startActivity(messageActivityIntent)
        }

    }

    override fun getItemCount() = chats.size
}