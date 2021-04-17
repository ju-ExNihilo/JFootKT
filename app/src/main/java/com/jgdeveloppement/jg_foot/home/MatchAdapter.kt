package com.jgdeveloppement.jg_foot.home

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jgdeveloppement.jg_foot.R
import com.jgdeveloppement.jg_foot.models.match.Match
import com.jgdeveloppement.jg_foot.utils.Utils

class MatchAdapter(private val context: HomeActivity,
                   private val matchesList: List<Match>,
                   private val layoutId: Int,
                   private val onCardMatchClicked: OnCardMatchClicked)
    : RecyclerView.Adapter<MatchAdapter.ViewHolder>() {

    interface OnCardMatchClicked{
        fun onClickedMatch(matchUrl: String, matchTitle: String, matchId: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val match = matchesList[position]
        val matchId = match.date + "_" + match.title

        holder.domTeam.text = match.side1.name
        holder.extTeam.text = match.side2.name
        Glide.with(context).load(Uri.parse(match.thumbnail)).into(holder.matchImage)

        holder.itemView.setOnClickListener { onCardMatchClicked.onClickedMatch(Utils.getUrl(match.embed), match.title, matchId) }
    }

    override fun getItemCount(): Int = matchesList.size

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val domTeam : TextView = view.findViewById(R.id.dom_team)
        val extTeam : TextView = view.findViewById(R.id.ext_team)
        val matchImage : ImageView = view.findViewById(R.id.match_item_image)
    }
}