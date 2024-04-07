package diplom.project.gameconnect.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import diplom.project.gameconnect.R
import diplom.project.gameconnect.databinding.AlternativePlatformElementBinding
import diplom.project.gameconnect.model.UserInfo
import diplom.project.gameconnect.statik.SelectedPlatforms

class AlternativePlatformAdapter(
    private var list: List<String>,
    private val context: Context,
    private val userInfo: UserInfo
) :
    RecyclerView.Adapter<AlternativePlatformAdapter.PlatformVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatformVH {
        return PlatformVH(
            AlternativePlatformElementBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PlatformVH, position: Int) {
        val item = holder.binding
        item.alternativePlatformElement.text = list[position]
        if (list[position] in userInfo.listPlatform) {
            holder.binding.root.setBackgroundResource(R.drawable.alternative_platform_form)
        }
    }

    override fun getItemCount(): Int = list.size
    class PlatformVH(val binding: AlternativePlatformElementBinding) :
        RecyclerView.ViewHolder(binding.root)
}