package diplom.project.gameconnect.view.fragment

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import diplom.project.gameconnect.app.App
import diplom.project.gameconnect.databinding.DialogGiveInfoBinding
import diplom.project.gameconnect.databinding.DialogNewsBinding
import diplom.project.gameconnect.databinding.FragmentTeammatesBinding
import diplom.project.gameconnect.model.Teammate
import diplom.project.gameconnect.view.activity.AnotherProfileActivity
import diplom.project.gameconnect.view.adapter.TeammatesAdapter
import diplom.project.gameconnect.viewmodel.TeammatesFragmentViewModel

class TeammatesFragment : Fragment(), TeammatesAdapter.OnClickListener {

    override fun openDialog(tgId: String) {
        val dialogBinding: DialogGiveInfoBinding by lazy {
            DialogGiveInfoBinding.inflate(
                layoutInflater
            )
        }
        val dialog = Dialog(requireContext()).apply {
            setCancelable(true)
            setContentView(dialogBinding.root)
            dialogBinding.tgId.text = "@$tgId"
            dialogBinding.tgId.setOnClickListener {
                val clipboard: ClipboardManager =
                    requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("telegramId", "@$tgId")
                clipboard.setPrimaryClip(clip)
                makeToast("telegram id скопирован в буфер обмена")
                this.cancel()
            }
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialog.show()
    }

    override fun changeListTeammates(list: List<Teammate>) {
        storage.document("user:${App.dm.getUserKey()}")
            .update("listToAccept", Gson().toJson(list))
    }

    override fun openProfile(teammate: Teammate) {
        startActivity(Intent(requireContext(), AnotherProfileActivity::class.java))
    }

    override fun onClick(teammate: Teammate, isLike: Boolean, position: Int) {
        val delta = if (isLike) 5 else -5
        storage.document("user:${teammate.userId}")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    storage.document("user:${teammate.userId}").update(
                        "rating",
                        it.result.data?.get("rating").toString().toInt() + delta
                    )
                }
            }
    }

    private val storage = FirebaseFirestore.getInstance().collection("Users")
    private val teammateViewModel: TeammatesFragmentViewModel by viewModels()
    val dialogBinding: DialogGiveInfoBinding by lazy {
        DialogGiveInfoBinding.inflate(
            layoutInflater
        )
    }


    private val binding: FragmentTeammatesBinding by lazy {
        FragmentTeammatesBinding.inflate(
            layoutInflater
        )
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setAdapter()
        val dialogBinding: DialogNewsBinding by lazy {
            DialogNewsBinding.inflate(
                layoutInflater
            )
        }
        teammateViewModel.observeListToAccept(FirebaseFirestore.getInstance(), requireContext(),
            Dialog(requireContext()).apply {
                setCancelable(true)
                setContentView(dialogBinding.root)
                window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            })
        setObservers()
    }

    private fun setAdapter() {
        binding.rvTeammates.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, true)
        binding.rvTeammates.adapter = TeammatesAdapter(App.dm.getListLastTeammates(), requireContext(), this)
    }
    private fun setObservers(){
        teammateViewModel.listToAccept.observe(viewLifecycleOwner){
            App.dm.setListTeammates(it)
            setAdapter()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        teammateViewModel.stopListening()
    }

    private fun makeToast(m: String) {
        Toast.makeText(activity, m, Toast.LENGTH_SHORT).show()
    }
}