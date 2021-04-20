package com.jgdeveloppement.jg_foot.details

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.jgdeveloppement.jg_foot.R
import com.jgdeveloppement.jg_foot.models.Comment
import com.jgdeveloppement.jg_foot.utils.Utils

class CommentAdapter(private val context: FragmentActivity,
                     options: FirestoreRecyclerOptions<Comment>,
                     private val userId: String,
                     private val onCommentClicked: OnCommentClicked)
    : FirestoreRecyclerAdapter<Comment, CommentAdapter.ViewHolder>(options){

    interface OnCommentClicked{
        fun onClickedLike(commentId: String)
        fun onClickedComment(comment: Comment, imageTransition: View)
        fun onClickedDeleteButton(comment: Comment)
        fun onClickedItem(comment: Comment, imageTransition: View)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Comment)  {

        //Count
        if (model.countLike == 0){
            holder.countLike.visibility = View.GONE
        }else{
            holder.countLike.text = model.countLike.toString()
            holder.countLike.visibility = View.VISIBLE
        }

        if (model.countComment == 0){
            holder.countComment.visibility = View.GONE
        }else{
            holder.countComment.text = model.countComment.toString()
            holder.countComment.visibility = View.VISIBLE
        }

        //Comment
        holder.userName.text = model.userName
        if (model.userUrlImage == "none"){
            holder.avatar.setImageDrawable(context.resources.getDrawable(R.drawable.avatar, null))
        }else{
            Glide.with(context).load(Uri.parse(model.userUrlImage)).circleCrop().into(holder.avatar)
        }
        holder.date.text = Utils.getFormatDateTime(model.createdAt)
        holder.commentMessage.text = model.comment

        //Click Listener
        holder.likeButton.setOnClickListener { onCommentClicked.onClickedLike(model.id) }
        holder.commentButton.setOnClickListener { onCommentClicked.onClickedComment(model, holder.avatar) }
        //delete
        if (model.userId == userId) {
            holder.deleteButton.visibility = View.VISIBLE
            holder.deleteButton.setOnClickListener { onCommentClicked.onClickedDeleteButton(model) }
        }else{
            holder.deleteButton.visibility = View.GONE
        }

        holder.itemView.setOnClickListener { onCommentClicked.onClickedItem(model, holder.avatar) }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val countComment : TextView = view.findViewById(R.id.cart_badge_comment)
        val countLike : TextView = view.findViewById(R.id.cart_badge_like)
        val avatar : ImageView = view.findViewById(R.id.avatar_image_view)
        val userName : TextView = view.findViewById(R.id.user_name_text_view)
        val commentMessage : TextView = view.findViewById(R.id.comment_text_view)
        val date : TextView = view.findViewById(R.id.date_text_view)
        val deleteButton : ImageButton = view.findViewById(R.id.delete_button)
        val likeButton : FrameLayout = view.findViewById(R.id.like_layout)
        val commentButton : FrameLayout = view.findViewById(R.id.comment_layout)
    }
}