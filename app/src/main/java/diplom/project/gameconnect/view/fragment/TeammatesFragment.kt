package diplom.project.gameconnect.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import diplom.project.gameconnect.R
import diplom.project.gameconnect.app.App
import diplom.project.gameconnect.databinding.FragmentTeammatesBinding
import diplom.project.gameconnect.model.Request
import diplom.project.gameconnect.view.adapter.TeammatesAdapter

class TeammatesFragment : Fragment(), TeammatesAdapter.OnClickListener {

    override fun onClick(request: Request, isLike: Boolean, position: Int) {
        val delta = if (isLike) 5 else -5
        storage.document("user:${request.userId}")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    storage.document("user:${request.userId}").update(
                        "rating",
                        it.result.data?.get("rating").toString().toInt() + delta
                    )
                }
                listTeammates.remove(request)
                binding.rvTeammates.adapter!!.notifyItemRemoved(position)
                App.dm.setListTeammates(listTeammates)
            }
    }

    private val storage = FirebaseFirestore.getInstance().collection("Users")

    private val binding: FragmentTeammatesBinding by lazy {
        FragmentTeammatesBinding.inflate(
            layoutInflater
        )
    }
    private var listTeammates = App.dm.getListLastTeammates()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setAdapter()
    }

    private fun setAdapter() {
        binding.rvTeammates.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTeammates.adapter = TeammatesAdapter(listTeammates, requireContext(), this)
    }
}