package diplom.project.gameconnect.view.adapter

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import diplom.project.gameconnect.R
import diplom.project.gameconnect.app.App
import diplom.project.gameconnect.databinding.DialogAddGameBinding
import diplom.project.gameconnect.databinding.DialogGiveInfoBinding
import diplom.project.gameconnect.databinding.TeammatesElementBinding
import diplom.project.gameconnect.model.Request
import diplom.project.gameconnect.model.Teammate
import diplom.project.gameconnect.model.TypeTeammate
import diplom.project.gameconnect.model.UserInfo
import diplom.project.gameconnect.statik.User
import diplom.project.gameconnect.statik.User.userIdForAnotherProfile

class TeammatesAdapter(
    private val list: MutableList<Teammate>,
    private val context: Context,
    private val onClickListener: OnClickListener
) : RecyclerView.Adapter<TeammatesAdapter.TeammatesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeammatesViewHolder {
        return TeammatesViewHolder(
            TeammatesElementBinding.inflate(
                LayoutInflater.from(
                    context
                ),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TeammatesViewHolder, position: Int) {
        val item = holder.binding
        when(list[position].type){
            TypeTeammate.WAITING -> {
                item.acceptBtn.visibility = View.VISIBLE
                item.rejectBtn.visibility = View.VISIBLE
                item.likeBtn.visibility = View.GONE
                item.dislikeBtn.visibility = View.GONE
            }
            TypeTeammate.SUCCESS -> {
                item.likeBtn.visibility = View.VISIBLE
                item.dislikeBtn.visibility = View.VISIBLE
                item.acceptBtn.visibility = View.GONE
                item.rejectBtn.visibility = View.GONE
            }
            TypeTeammate.RATED -> {
                item.root.background = context.resources.getDrawable(R.color.midi_green, context.theme)
                item.acceptBtn.visibility = View.GONE
                item.rejectBtn.visibility = View.GONE
                item.likeBtn.visibility = View.GONE
                item.dislikeBtn.visibility = View.GONE
            }
        }
        Glide.with(item.imageTeammate)
            .load(list[position].profileImage)
            .error(R.drawable.baseline_person_24)
            .into(item.imageTeammate)
        item.gameName.text = list[position].gameName
        item.nickName.text = list[position].userNick
        item.telegramId.text = "@${list[position].telegramId}"

        item.root.setOnClickListener {
            userIdForAnotherProfile = list[position].userId
            onClickListener.openProfile(list[position])
        }

        item.acceptBtn.setOnClickListener {
            list[position].type = TypeTeammate.SUCCESS
            this.notifyItemChanged(position)
            App.dm.setListTeammates(list)
            onClickListener.changeListTeammates(list as List<Teammate>)

            onClickListener.openDialog(list[position].telegramId)
        }
        item.rejectBtn.setOnClickListener {
            list.remove(list[position])
            onClickListener.changeListTeammates(list as List<Teammate>)
        }
        item.likeBtn.setOnClickListener {
            try {
                onClickListener.onClick(list[position], true,  position)
                Toast.makeText(context, context.getString(R.string.increase_rating), Toast.LENGTH_SHORT).show()
                list[position].type = TypeTeammate.RATED
                this.notifyItemChanged(position)
                App.dm.setListTeammates(list)
                onClickListener.changeListTeammates(list as List<Teammate>)
            } catch (e:Exception){
                Log.d("error", e.message.toString())
            }
        }
        item.dislikeBtn.setOnClickListener {
            try {
                onClickListener.onClick(list[position], false,  position)
                list.remove(list[position])
                Toast.makeText(context, context.getString(R.string.decrease_rating), Toast.LENGTH_SHORT).show()
                onClickListener.changeListTeammates(list as List<Teammate>)
            } catch (e: Exception) {
                Log.d("error", e.message.toString())
            }
        }
    }

    override fun getItemCount(): Int = list.size

    interface OnClickListener{
        fun onClick(teammate: Teammate, isLike: Boolean, position: Int)
        fun openProfile(teammate: Teammate)

        fun changeListTeammates(list: List<Teammate>)
        fun openDialog(tgId: String)
    }
    class TeammatesViewHolder(val binding: TeammatesElementBinding) :
        RecyclerView.ViewHolder(binding.root)
}