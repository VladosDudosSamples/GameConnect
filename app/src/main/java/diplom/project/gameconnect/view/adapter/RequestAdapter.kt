package diplom.project.gameconnect.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import diplom.project.gameconnect.R
import diplom.project.gameconnect.databinding.RequestElementBinding
import diplom.project.gameconnect.model.Request

class RequestAdapter(
    private var list: List<Request>,
    private val context: Context
) :
    RecyclerView.Adapter<RequestAdapter.RequestVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestVH {
        return RequestVH(
            RequestElementBinding.inflate(
            LayoutInflater.from(context),
            parent,
            false
        )
        )
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: RequestVH, position: Int) {
        val item = holder.binding
        item.gender.text = context.resources.getString(R.string.gender) + " " + if (list[position].gender) "Ж" else "М"
        item.gameName.text = list[position].gameName
        item.needUsers.text = context.resources.getString(R.string.need_users) + " " + list[position].needUsers
        item.userNick.text = list[position].userNick
        if (list[position].comment.isEmpty()) item.comment.visibility = View.GONE
        else item.comment.text = context.resources.getString(R.string.comment) + " " + list[position].comment

//        if (list[position].userRating.toInt() <= 40) item.root.background =
//            context.resources.getDrawable(R.drawable.round_border_form_red, context.theme)
//        else if (list[position].userRating.toInt() < 75) item.root.background =
//            context.resources.getDrawable(R.drawable.round_border_form_green, context.theme)
//        else item.root.background =
//            context.resources.getDrawable(R.drawable.round_border_form, context.theme)

        item.root.background = when(list[position].userRating.toInt()){
            in 0..40 ->  context.resources.getDrawable(R.drawable.round_border_form_red, context.theme)
            in 75..100 ->  context.resources.getDrawable(R.drawable.round_border_form_green, context.theme)
            else -> context.resources.getDrawable(R.drawable.round_border_form, context.theme)
        }
    }

    override fun getItemCount(): Int = list.size
    class RequestVH(val binding: RequestElementBinding) :
        RecyclerView.ViewHolder(binding.root)
}