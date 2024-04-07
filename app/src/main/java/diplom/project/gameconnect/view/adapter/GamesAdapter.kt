package diplom.project.gameconnect.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import diplom.project.gameconnect.R
import diplom.project.gameconnect.databinding.GameLayoutBinding
import diplom.project.gameconnect.databinding.PlatformLayoutBinding

class GamesAdapter(
    private var list: List<String>,
    private val context: Context,
    private val onClickListener: OnClick,
    private val isDeleting: Boolean
) :
    RecyclerView.Adapter<GamesAdapter.GamesVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamesVH {
        return GamesVH(
            GameLayoutBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: GamesVH, position: Int) {
        holder.binding.textView2.text = list[position]
        holder.binding.reduceBtn.visibility = if (isDeleting) View.VISIBLE else View.GONE
        holder.binding.reduceBtn.setOnClickListener {
            onClickListener.click(holder.binding.textView2)
        }
    }

    override fun getItemCount(): Int = list.size

    interface OnClick {
        fun click(textView: TextView)
    }

    class GamesVH(val binding: GameLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)
}