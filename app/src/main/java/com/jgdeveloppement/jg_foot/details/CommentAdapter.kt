package com.jgdeveloppement.jg_foot.details

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jgdeveloppement.jg_foot.R
import com.jgdeveloppement.jg_foot.models.FinalComment
import com.jgdeveloppement.jg_foot.utils.Utils

class CommentAdapter(private val context: DetailsActivity,
                     private val commentList: List<FinalComment>,
                     private val userId: String,
                     private val onCommentClicked: OnCommentClicked)
    : RecyclerView.Adapter<CommentAdapter.ViewHolder>(){

    interface OnCommentClicked{
        fun onClickedLike(commentId: String, haveLiked: Boolean)
        fun onClickedComment(comment: FinalComment, imageTransition: View)
        fun onClickedDeleteButton(commentId: String)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = commentList[position]

        //Like And Comment
        if (comment.countLike == 0){
            holder.countLike.visibility = View.GONE
        }else{
            holder.countLike.text = comment.countLike.toString()
            holder.countLike.visibility = View.VISIBLE
        }

        if (comment.countComment == 0){
            holder.countComment.visibility = View.GONE
        }else{
            holder.countComment.text = comment.countComment.toString()
            holder.countComment.visibility = View.VISIBLE
        }

        if (comment.haveLiked){
            holder.likeImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_like, null))
        }else{
            holder.likeImage.setImageDrawable(context.resources.getDrawable(R.drawable.ic_unlike, null))
        }

        //Comment
        holder.userName.text = comment.userName
        if (comment.userUrlImage != "none") Glide.with(context).load(Uri.parse(comment.userUrlImage)).circleCrop().into(holder.avatar)
        holder.date.text = Utils.getFormatDateTime(comment.createdAt)
        holder.commentMessage.text = comment.comment

        //Click Listener
        holder.likeButton.setOnClickListener { onCommentClicked.onClickedLike(comment.id, comment.haveLiked) }
        holder.commentButton.setOnClickListener { onCommentClicked.onClickedComment(comment, holder.avatar) }
        //delete
        if (comment.userId == userId) {
            holder.deleteButton.visibility = View.VISIBLE
            holder.deleteButton.setOnClickListener { onCommentClicked.onClickedDeleteButton(comment.id) }
        }
    }

    override fun getItemCount(): Int = commentList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val likeImage : ImageView = view.findViewById(R.id.like_image)
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