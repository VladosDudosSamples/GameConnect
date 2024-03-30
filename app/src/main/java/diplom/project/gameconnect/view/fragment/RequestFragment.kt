package diplom.project.gameconnect.view.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import diplom.project.gameconnect.R
import diplom.project.gameconnect.app.App
import diplom.project.gameconnect.databinding.DialogRequestBinding
import diplom.project.gameconnect.databinding.FragmentRequestBinding
import diplom.project.gameconnect.model.Request
import diplom.project.gameconnect.model.SortType
import diplom.project.gameconnect.view.adapter.RequestAdapter
import diplom.project.gameconnect.viewmodel.RequestListViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RequestFragment : Fragment() {

    private val binding: FragmentRequestBinding by lazy {
        FragmentRequestBinding.inflate(
            layoutInflater
        )
    }
    private val requestListViewModel: RequestListViewModel by viewModels()
    private var store = FirebaseFirestore.getInstance()
    private val format: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private var currentSortType = SortType.DATE


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClick()
        requestListViewModel.getRequestList(store)
        setObservers()
    }

    private fun onClick() {
        binding.mainButton.setOnClickListener {
            openDialogForRequest()
        }
        binding.sortTxt.setOnClickListener {
            showSortMenu()
        }
    }

    private fun setAdapter() {
        binding.rvRequests.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        sortListBySortType()
        binding.rvRequests.adapter =
            RequestAdapter(requestListViewModel.requestList.value!!, requireContext())
    }

    private fun createRequest(needUsers: String, gameName: String, comment: String) {
        store.collection("Users").document("user:${App.dm.getUserKey()}").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    store.collection("Requests")
                        .document("Request${requestListViewModel.getCountAndDelete().first + 1}")
                        .set(
                            Request(
                                task.result.get("nick").toString(),
                                needUsers,
                                task.result.get("gender").toString().toBoolean(),
                                gameName,
                                comment,
                                task.result.get("rating").toString(),
                                task.result.get("telegramId").toString(),
                                LocalDateTime.now().format(format)
                            )
                        )
                }
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun openDialogForRequest() {
        val dialogBinding: DialogRequestBinding by lazy {
            DialogRequestBinding.inflate(
                layoutInflater
            )
        }
        val dialog = Dialog(requireContext()).apply {
            setCancelable(true)
            setContentView(dialogBinding.root)
            dialogBinding.okBtn.setOnClickListener {
                if (checkInputDialog(dialogBinding)) {
                    createRequest(
                        dialogBinding.needUsersTxt.text.toString(),
                        dialogBinding.gameNameTxt.text.toString(),
                        dialogBinding.comment.text.toString()
                    )
                    requestListViewModel.changeCountRequests(store, 1)

                    this.cancel()
                    requestListViewModel.getRequestList(store)
                }
            }
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialog.show()
    }

    private fun checkInputDialog(b: DialogRequestBinding): Boolean {
        var needUsers = 0
        try {
            needUsers = b.needUsersTxt.text.toString()
                .toInt()
        } catch (e: Exception) {
            Log.d("tag", "msg")
        }
        when {
            b.gameNameTxt.text.toString().length < 2 -> makeToast(getString(R.string.game_name_too_short))
            needUsers > 5 -> makeToast(getString(R.string.to_many_teammates))
            needUsers < 1 -> makeToast(getString(R.string.need_users_at_least_1_teammate))

            else -> return true
        }
        return false
    }

    private fun makeToast(m: String) {
        Toast.makeText(activity, m, Toast.LENGTH_SHORT).show()
    }

    private fun setObservers() {
        requestListViewModel.isFunDone.observe(viewLifecycleOwner) {
            if (it) {
                setAdapter()
                binding.progressWaitRequest.visibility = View.GONE
            } else binding.progressWaitRequest.visibility = View.VISIBLE
        }
    }

    private fun sortListBySortType() {
        when (currentSortType) {
            SortType.DATE -> requestListViewModel.requestList.value!!.sortByDescending { it.date }
            SortType.OLD_DATE -> requestListViewModel.requestList.value!!.sortBy { it.date }
            SortType.ALPHABET -> requestListViewModel.requestList.value!!.sortBy { it.gameName }
            SortType.ANTI_ALPHABET -> requestListViewModel.requestList.value!!.sortByDescending { it.gameName }
            else -> requestListViewModel.requestList.value!!.sortByDescending { it.userRating }
        }
    }

    private fun showSortMenu() {
        val wrapper: Context = ContextThemeWrapper(requireContext(), R.style.popupMenuStyle)
        val popup = PopupMenu(wrapper, binding.sortTxt)

        popup.inflate(R.menu.sort_menu)
        popup.setOnMenuItemClickListener {
            currentSortType = when (it.title) {
                resources.getString(R.string.new_at_start) -> SortType.DATE
                resources.getString(R.string.old_at_start) -> SortType.OLD_DATE
                resources.getString(R.string.alphabet_sort) -> SortType.ALPHABET
                resources.getString(R.string.alphabet_distinct_sort) -> SortType.ANTI_ALPHABET
                else -> SortType.RAITING
            }
            setAdapter()
            binding.sortTxt.text =
                "${resources.getString(R.string.sorted_by)} ${it.title.toString()}"
            true
        }
        popup.show()
    }
}