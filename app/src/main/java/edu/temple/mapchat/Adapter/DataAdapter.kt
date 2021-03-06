package edu.temple.mapchat.Adapter

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import edu.temple.mapchat.Model.Model
import edu.temple.mapchat.R
import kotlinx.android.synthetic.main.recycler_view_row.view.*
import org.jetbrains.anko.textColor

class DataAdapter(private val userList: ArrayList<Model.User>, private val listener: Listener) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {

    interface Listener {
        fun onItemClick(user : Model.User)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.recycler_view_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = userList.count()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(user = userList[position], listener = listener)
    }

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        fun bind(user: Model.User, listener: Listener) {
            itemView.user_name.text = user.username
            itemView.user_name.textSize = 30F
            itemView.user_name.textColor = Color.parseColor("black")
            itemView.setOnClickListener { listener.onItemClick(user) }
        }
    }
}