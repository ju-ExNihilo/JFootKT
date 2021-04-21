package com.jgdeveloppement.jg_foot.notification

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.jgdeveloppement.jg_foot.R
import com.jgdeveloppement.jg_foot.models.Comment
import com.jgdeveloppement.jg_foot.models.Notification
import com.jgdeveloppement.jg_foot.utils.Utils

class NotificationAdapter(private val context: Context?,
                          private val onNotificationClicked: OnNotificationClicked,
                          options: FirestoreRecyclerOptions<Notification>)
    : FirestoreRecyclerAdapter<Notification, NotificationAdapter.ViewHolder>(options){

    interface OnNotificationClicked{
        fun onClickedDelete(notificationId: String)
        fun onClickedItem(notification: Notification, imageTransition: View)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Notification) {

        holder.userName.text = model.userName
        holder.date.text = "le ${Utils.getFormatDateTime(model.createdAt)}"

        if (model.liked){
            holder.image.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_like, null))
            holder.defaultText.text = context?.resources?.getString(R.string.like_notification)
        }else{
            holder.image.setImageDrawable(context?.resources?.getDrawable(R.drawable.ic_comment, null))
            holder.defaultText.text = context?.resources?.getString(R.string.reply_notification)
        }

        holder.deleteButton.setOnClickListener { onNotificationClicked.onClickedDelete(model.id) }

        holder.itemView.setOnClickListener { onNotificationClicked.onClickedItem(model, holder.image) }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val userName : TextView = view.findViewById(R.id.notification_item_user_name)
        val date : TextView = view.findViewById(R.id.notification_item_date)
        val defaultText : TextView = view.findViewById(R.id.notification_item_default_text)
        val image : ImageView = view.findViewById(R.id.notification_item_image)
        val deleteButton : ImageButton = view.findViewById(R.id.notification_item_delete_button)
    }
}