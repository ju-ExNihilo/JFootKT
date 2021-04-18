package com.jgdeveloppement.jg_foot.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jgdeveloppement.jg_foot.R
import com.jgdeveloppement.jg_foot.models.Comment
import com.jgdeveloppement.jg_foot.models.FinalComment

class CommentAdapter(private val context: DetailsActivity,
                     private val commentList: List<FinalComment>,
                     private val onCommentClicked: OnCommentClicked)
    : RecyclerView.Adapter<CommentAdapter.ViewHolder>(){

    interface OnCommentClicked{
        fun onClickedLike(commentId: String, haveLiked: Boolean)
        fun onClickedComment(commentId: String)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = commentList[position]

        holder.countComment.text = comment.countComment.toString()
        holder.countLike.text = comment.countLike.toString()

        if (comment.haveLiked){
            holder.likeButton.setImageDrawable(context.resources.getDrawable(R.drawable.ic_like, null))
        }else{
            holder.likeButton.setImageDrawable(context.resources.getDrawable(R.drawable.ic_unlike, null))
        }

        holder.likeButton.setOnClickListener { onCommentClicked.onClickedLike(comment.id, comment.haveLiked) }

    }

    override fun getItemCount(): Int = commentList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val likeButton : ImageView = view.findViewById(R.id.like_image)
        val countComment : TextView = view.findViewById(R.id.cart_badge_comment)
        val countLike : TextView = view.findViewById(R.id.cart_badge_like)
    }
}