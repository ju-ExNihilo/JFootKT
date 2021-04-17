package com.jgdeveloppement.jg_foot.details

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.jgdeveloppement.jg_foot.R
import com.jgdeveloppement.jg_foot.models.Comment

class CommentAdapter(private val context: DetailsActivity,
                     private val commentList: List<Comment>,
                     private val onCommentClicked: OnCommentClicked)
    : RecyclerView.Adapter<CommentAdapter.ViewHolder>(){

    interface OnCommentClicked{
        fun onClickedLike(commentId: String)
        fun onClickedComment(commentId: String)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        var like = false

        holder.likeButton.setOnClickListener {
            if (like){
                holder.likeButton.setImageDrawable(context.resources.getDrawable(R.drawable.ic_like, null))
            }else{
                holder.likeButton.setImageDrawable(context.resources.getDrawable(R.drawable.ic_unlike, null))
            }
            like = !like

        }

    }

    override fun getItemCount(): Int = commentList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val likeButton : ImageView = view.findViewById(R.id.like_image)
    }
}