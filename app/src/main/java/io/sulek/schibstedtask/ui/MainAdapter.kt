package io.sulek.schibstedtask.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.sulek.schibstedtask.R
import io.sulek.schibstedtask.models.User
import kotlinx.android.synthetic.main.view_holder_user.view.*
import java.lang.StringBuilder

class MainAdapter(
    context: Context
) : RecyclerView.Adapter<MainAdapter.UserViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    private val glide = Glide.with(context)
    private var items = listOf<User>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        UserViewHolder(inflater.inflate(R.layout.view_holder_user, parent, false))


    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun updateItems(items: List<User>) {
        val old = this.items.toList()
        this.items = items
        DiffUtil.calculateDiff(
            DiffCallback(
                old,
                this.items
            )
        ).dispatchUpdatesTo(this)
    }


    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val stringBuilder = StringBuilder()

        fun bind(user: User) = with(itemView) {
            glide.load(user.avatarUrl)
                .centerCrop()
                .into(imageAvatar)
            textName.text = user.name

            stringBuilder.clear()
            val lastIndex = user.repos.size - 1
            user.repos.forEachIndexed { index, repo ->
                stringBuilder.append("- ${repo.name}")
                if (index != lastIndex) stringBuilder.append("\n")
            }
            textRepos.text = stringBuilder.toString()
        }

    }

    class DiffCallback(
        private val oldList: List<User>,
        private val newList: List<User>
    ) : DiffUtil.Callback() {

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
            return areSame(oldPosition, newPosition)
        }

        override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
            return areSame(oldPosition, newPosition)
        }

        private fun areSame(oldPosition: Int, newPosition: Int): Boolean {
            val old = oldList[oldPosition]
            val new = newList[newPosition]
            return old.id == new.id
        }
    }

}