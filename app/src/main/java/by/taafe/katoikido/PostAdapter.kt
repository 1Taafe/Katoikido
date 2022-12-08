package by.taafe.katoikido

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
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
    }

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

        val storageReference = Firebase.storage.reference.child(post.imageUrl)

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