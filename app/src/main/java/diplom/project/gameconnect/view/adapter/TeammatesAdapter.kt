package diplom.project.gameconnect.view.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import diplom.project.gameconnect.R
import diplom.project.gameconnect.databinding.TeammatesElementBinding
import diplom.project.gameconnect.model.Request
import diplom.project.gameconnect.model.UserInfo

class TeammatesAdapter(
    private val list: List<Request>,
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
        Glide.with(item.imageTeammate)
            .load(list[position].profileImage)
            .error(R.drawable.baseline_person_24)
            .into(item.imageTeammate)
        item.gameName.text = list[position].gameName
        item.nickName.text = list[position].userNick
        item.telegramId.text = "@${list[position].telegramId}"

        item.likeBtn.setOnClickListener {
            try {
                onClickListener.onClick(list[position], true, position)
                Toast.makeText(context, context.getString(R.string.increase_rating), Toast.LENGTH_SHORT).show()
            } catch (e:Exception){
                Log.d("error", e.message.toString())
            }
        }
        item.dislikeBtn.setOnClickListener {
            try {
                onClickListener.onClick(list[position], false,  position)
                Toast.makeText(context, context.getString(R.string.decrease_rating), Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.d("error", e.message.toString())
            }
        }
    }

    override fun getItemCount(): Int = list.size

    interface OnClickListener{
        fun onClick(request: Request, isLike: Boolean, position: Int)
    }
    class TeammatesViewHolder(val binding: TeammatesElementBinding) :
        RecyclerView.ViewHolder(binding.root)
}