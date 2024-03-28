package diplom.project.gameconnect.view.adapter

import android.content.Context
import android.content.res.Resources.Theme
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import diplom.project.gameconnect.R
import diplom.project.gameconnect.databinding.PlatformLayoutBinding
import diplom.project.gameconnect.model.Platform
import diplom.project.gameconnect.statik.SelectedPlatforms.selectedPlatform

class PlatformsAdapter(
    private var list: List<Platform>,
    private val context: Context
) :
    RecyclerView.Adapter<PlatformsAdapter.PlatformVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlatformVH {
        return PlatformVH(PlatformLayoutBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PlatformVH, position: Int) {
        val item = holder.binding.root
        item.text = list[position].name
        item.setOnClickListener {
            if (!selectedPlatform.contains(list[position])) {
                item.setTextColor(context.resources.getColor(R.color.green, context.theme))
                selectedPlatform.add(list[position])
            } else {
                item.setTextColor(context.resources.getColor(R.color.white, context.theme))
                selectedPlatform.remove(list[position])
            }
        }

    }

    override fun getItemCount(): Int = list.size
    class PlatformVH(val binding: PlatformLayoutBinding) :
        RecyclerView.ViewHolder(binding.root)
}