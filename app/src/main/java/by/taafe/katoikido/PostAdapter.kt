package by.taafe.katoikido

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class PostAdapter(private val posts: List<Post>, private val context: Context) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val postImageView: ImageView = itemView.findViewById(R.id.postImageView)
        val postTypeView: TextView = itemView.findViewById(R.id.typeView)
        val petTypeView: TextView = itemView.findViewById(R.id.petView)
        val postTitleView: TextView = itemView.findViewById(R.id.titleView)
        val dateView: TextView = itemView.findViewById(R.id.dateView)
        val postOwnerNameView: TextView = itemView.findViewById(R.id.postOwnerName)
        val deleteButton: Button = itemView.findViewById(R.id.deleteButton)
        val editButton: Button = itemView.findViewById(R.id.editButton)
        val cardView: CardView = itemView.findViewById(R.id.postCard)
    }

    private val currentUser = FirebaseAuth.getInstance().currentUser
    val postsReference = Firebase.database("https://katoikido-default-rtdb.europe-west1.firebasedatabase.app/").reference.child("posts")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return PostViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        Glide.with(context).load("")
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(Loader.create(context, 100f, 10f))
            .into(holder.postImageView)
        val post = posts[position]
        holder.postTitleView.text = post.title
        holder.petTypeView.text = "Питомец: " + post.petType
        holder.postTypeView.text = "Категория: " + post.type
        holder.dateView.text = post.uploadDate
        holder.postOwnerNameView.text = post.ownerName

        holder.cardView.setOnClickListener(){
            if(currentUser != null){
                MaterialAlertDialogBuilder(context)
                    .setIcon(R.drawable.ic_baseline_article_24)
                    .setTitle(post.title)
                    .setMessage(post.text)
                    .setNeutralButton("Закрыть"){ dialog, which ->
                    }
                    .setPositiveButton("Написать"){ dialog, which ->
                        if(currentUser?.phoneNumber == post.ownerPhone){
                            Toast.makeText(context, "Вы не можете писать самому себе!", Toast.LENGTH_SHORT).show()
                        }
                        else{
                            val messageActivityIntent = Intent(context, MessageActivity::class.java)
                            messageActivityIntent.putExtra("sendTo", post.ownerName)
                            messageActivityIntent.putExtra("sendToPhone", post.ownerPhone)
                            context.startActivity(messageActivityIntent)
                        }

                    }
                    .show()
            }
            else{
                MaterialAlertDialogBuilder(context)
                    .setIcon(R.drawable.ic_baseline_article_24)
                    .setTitle(post.title)
                    .setMessage(post.text)
                    .setPositiveButton("Закрыть"){ dialog, which ->
                    }
                    .show()
            }

        }

        val storageReference = Firebase.storage.reference.child(post.imageUrl)

        if(currentUser?.phoneNumber == post.ownerPhone){
            holder.editButton.isVisible = true
            holder.editButton.setOnClickListener{
                Post.tempContainer = post
                val editPostIntent = Intent(context, EditPostActivity::class.java)
                editPostIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                context.startActivity(editPostIntent)
            }
            holder.deleteButton.isVisible = true
            holder.deleteButton.setOnClickListener {
                val postRef = postsReference.child(post.id)
                MaterialAlertDialogBuilder(context)
                    .setTitle("Удаление")
                    .setIcon(R.drawable.ic_baseline_delete_forever_24)
                    .setMessage("Вы уверены, что хотите удалить объявление?")
                    .setNegativeButton("Отмена") { dialog, which ->
                        // Respond to negative button press
                    }
                    .setPositiveButton("Удалить") { dialog, which ->
                        storageReference.delete().addOnSuccessListener {
                            postRef.removeValue()
                            MaterialAlertDialogBuilder(context)
                                .setTitle("Удаление")
                                .setMessage("Объявление успешно удалено без возможности восстановления")
                                .setPositiveButton("Ок"){ dialog, which ->

                                }
                                .show()
                        }.addOnFailureListener {
                            // Uh-oh, an error occurred!
                        }

                    }
                    .show()
            }
        }

        if(post.imageUrl == Post.undefined){
            Glide.with(context)
                .load(R.drawable.ic_baseline_catching_pokemon_24)
                .placeholder(Loader.create(context, 100f, 10f))
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.ic_baseline_catching_pokemon_24)
                .into(holder.postImageView)
        }
        else{
            storageReference.downloadUrl.addOnSuccessListener {
                Glide.with(context)
                    .load(it)
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(Loader.create(context, 100f, 10f))
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .error(R.drawable.ic_baseline_catching_pokemon_24)
                    .into(holder.postImageView)
            }.addOnFailureListener {
                // Handle any errors
            }
        }

    }

    override fun getItemCount() = posts.size
}