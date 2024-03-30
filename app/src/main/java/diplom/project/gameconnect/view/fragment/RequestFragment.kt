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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import diplom.project.gameconnect.R
import diplom.project.gameconnect.app.App
import diplom.project.gameconnect.databinding.DialogRequestBinding
import diplom.project.gameconnect.databinding.FragmentRequestBinding
import diplom.project.gameconnect.model.Platform
import diplom.project.gameconnect.model.Request
import diplom.project.gameconnect.model.SortType
import diplom.project.gameconnect.statik.SelectedPlatforms.listPlatforms
import diplom.project.gameconnect.statik.SelectedPlatforms.selectedPlatform
import diplom.project.gameconnect.view.adapter.PlatformsAdapter
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
        binding.filterTxt.setOnClickListener {
            showFilterMenu()
        }
    }

    private fun setLayoutManager() {
        binding.rvRequests.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
    }

    private fun setAdapter(list: List<Request>) {
        sortListBySortType()
        binding.rvRequests.adapter =
            RequestAdapter(list, requireContext())
    }

    private fun setAdapter() {
        setLayoutManager()
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
                                LocalDateTime.now().format(format),
                                selectedPlatform
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
            var count = dialogBinding.needUsersTxt.text.toString().toInt()
            dialogBinding.rvPlatformsDialog.layoutManager = LinearLayoutManager(requireContext())
            dialogBinding.rvPlatformsDialog.adapter = PlatformsAdapter(listPlatforms, requireContext())
            dialogBinding.okBtn.setOnClickListener {
                if (checkInputDialog(dialogBinding)) {
                    createRequest(
                        count.toString(),
                        dialogBinding.gameNameTxt.text.toString(),
                        dialogBinding.comment.text.toString()
                    )

                    requestListViewModel.changeCountRequests(store, 1)
                    this.cancel()
                    requestListViewModel.getRequestList(store)
                }
            }
            dialogBinding.imgReduce.setOnClickListener {
                if (count > 1) {
                    count--
                    dialogBinding.needUsersTxt.text = count.toString()
                }
            }
            dialogBinding.imgIncrease.setOnClickListener {
                if (count < 5) {
                    count++
                    dialogBinding.needUsersTxt.text = count.toString()
                }
            }
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialog.show()
    }

    private fun checkInputDialog(b: DialogRequestBinding): Boolean {
        when {
            b.gameNameTxt.text.toString().length < 2 -> makeToast(getString(R.string.game_name_too_short))
            selectedPlatform.size == 0 -> makeToast(getString(R.string.empty_list_platforms))
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
                else -> SortType.RATING
            }
            setAdapter()
            binding.sortTxt.text =
                "${resources.getString(R.string.sorted_by)} ${it.title.toString()}"
            true
        }
        popup.show()
    }

    private fun showFilterMenu() {
        val wrapper: Context = ContextThemeWrapper(requireContext(), R.style.popupMenuStyle)
        val popup = PopupMenu(wrapper, binding.filterTxt)

        popup.inflate(R.menu.filter_menu)
        popup.setOnMenuItemClickListener {
            setAdapter(requestListViewModel.filterList(it, requireContext()))
            true
        }
        popup.show()
    }
}