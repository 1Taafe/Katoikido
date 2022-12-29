package by.taafe.katoikido.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.MenuRes
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import by.taafe.katoikido.classes.Message
import by.taafe.katoikido.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.divider.MaterialDivider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MessageAdapter(private val messages: List<Message>, private val context: Context) :
    RecyclerView.Adapter<MessageAdapter.PostViewHolder>() {

    val currentUser = FirebaseAuth.getInstance().currentUser!!
    val chatsReference = Firebase.database("https://katoikido-default-rtdb.europe-west1.firebasedatabase.app/").reference.child("chats")

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val displaName: TextView = itemView.findViewById(R.id.displayName)
        val messageView: TextView = itemView.findViewById(R.id.messageView)
        val dateView: TextView = itemView.findViewById(R.id.dateView)
        val messageCard : CardView = itemView.findViewById(R.id.messageCard)
        val messageDivider : MaterialDivider = itemView.findViewById(R.id.messageDivider)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.message_item, parent, false)
        return PostViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val message = messages[position]

        holder.displaName.text = message.sender
        holder.messageView.text = message.text
        holder.dateView.text = message.date

        val params = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        val dpRatio = context.resources.displayMetrics.density

        if(currentUser.phoneNumber == message.phone){
            params.leftMargin = (96 * dpRatio).toInt()
            params.rightMargin = (8 * dpRatio).toInt()
            params.topMargin = (10 * dpRatio).toInt()
            params.bottomMargin = (10 * dpRatio).toInt()
            holder.messageCard.layoutParams = params

            val typedValue = TypedValue()
            context.theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true)
            val color = typedValue.data
            holder.messageCard.setCardBackgroundColor(color)
            holder.displaName.setTextColor(Color.WHITE)
            holder.messageView.setTextColor(Color.WHITE)
            holder.dateView.setTextColor(Color.WHITE)

            holder.messageDivider.dividerColor = Color.WHITE
        }
        else{
            params.leftMargin = (8 * dpRatio).toInt()
            params.rightMargin = (96 * dpRatio).toInt()
            params.topMargin = (10 * dpRatio).toInt()
            params.bottomMargin = (10 * dpRatio).toInt()
            holder.messageCard.layoutParams = params
        }

        holder.messageCard.setOnLongClickListener{
            showMenu(it, R.menu.message_menu, message)
            true
        }

    }

    private fun showMenu(v: View, @MenuRes menuRes: Int, message: Message) {
        val popup = PopupMenu(context, v)
        popup.menuInflater.inflate(menuRes, popup.menu)
        popup.setOnMenuItemClickListener { menuItem: MenuItem ->
            when(menuItem.itemId){
                R.id.removeMessage -> {
                    MaterialAlertDialogBuilder(context)
                        .setIcon(R.drawable.ic_baseline_article_24)
                        .setTitle("Удаление")
                        .setMessage("Вы действительно хотите удалить сообщение без возможности восстановления")
                        .setNeutralButton("Отмена"){ dialog, which ->
                        }
                        .setPositiveButton("Удалить"){ dialog, which ->
                            val messageRef = chatsReference.child(message.chatId).child(message.id)
                            messageRef.setValue(null)
                        }
                        .show()
                }
            }
            true
        }
        popup.setOnDismissListener {

        }

        popup.show()
    }

    override fun getItemCount() = messages.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}