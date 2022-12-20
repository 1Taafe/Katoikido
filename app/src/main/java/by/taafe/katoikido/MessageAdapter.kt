package by.taafe.katoikido

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
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
import com.google.android.material.divider.MaterialDivider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class MessageAdapter(private val messages: List<Message>, private val context: Context) :
    RecyclerView.Adapter<MessageAdapter.PostViewHolder>() {

    val currentUser = FirebaseAuth.getInstance().currentUser!!

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

    }

    override fun getItemCount() = messages.size
}