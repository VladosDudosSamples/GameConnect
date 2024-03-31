package diplom.project.gameconnect.view.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import diplom.project.gameconnect.R
import diplom.project.gameconnect.databinding.DialogChangeNeedUsersBinding
import diplom.project.gameconnect.databinding.DialogRequestBinding
import diplom.project.gameconnect.databinding.FragmentRequestBinding
import diplom.project.gameconnect.model.Request
import diplom.project.gameconnect.model.SortType
import diplom.project.gameconnect.statik.SelectedPlatforms.listPlatforms
import diplom.project.gameconnect.statik.SelectedPlatforms.selectedPlatform
import diplom.project.gameconnect.statik.User.userData
import diplom.project.gameconnect.view.adapter.PlatformsAdapter
import diplom.project.gameconnect.view.adapter.RequestAdapter
import diplom.project.gameconnect.viewmodel.RequestListViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RequestFragment : Fragment(), RequestAdapter.OnClickListener {

    override fun onClick(data: Request) {
        if (userData.nick == data.userNick) {
            showChangeCountDialog(data.id, data.needUsers.toInt())
        } else {
            makeToast("Не совпало")
        }
        //СДЕЛАТЬ ВЫВОД telegramId 
    }

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
            RequestAdapter(list, requireContext(), this)
    }

    private fun setAdapter() {
        setLayoutManager()
        sortListBySortType()
        binding.rvRequests.adapter =
            RequestAdapter(requestListViewModel.requestList.value!!, requireContext(), this)
    }

    private fun createRequest(needUsers: String, gameName: String, comment: String) {
        val id = requestListViewModel.getCount() + 1
        store.collection("Requests")
            .document("Request${id}")
            .set(
                Request(
                    id,
                    userData.nick,
                    needUsers,
                    userData.gender,
                    gameName,
                    comment,
                    userData.rating.toString(),
                    userData.telegramId,
                    LocalDateTime.now().format(format),
                    selectedPlatform
                )
            )
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
            dialogBinding.rvPlatformsDialog.adapter =
                PlatformsAdapter(listPlatforms, requireContext())
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

    private fun showChangeCountDialog(requestNum: Int, count: Int) {
        val dialogBinding: DialogChangeNeedUsersBinding by lazy {
            DialogChangeNeedUsersBinding.inflate(
                layoutInflater
            )
        }
        val dialog = Dialog(requireContext()).apply {
            val deleteString = store.collection("Requests")
                .document("RequestCount")
            setCancelable(true)
            setContentView(dialogBinding.root)
            var newCount = count
            dialogBinding.needUsersTxt.text = count.toString()
            dialogBinding.okBtn.setOnClickListener {
                store.collection("Requests")
                    .document("Request${requestNum}")
                    .update("needUsers", newCount.toString())
                this.cancel()
                requestListViewModel.getRequestList(store)
            }
            dialogBinding.deleteBtn.setOnClickListener {
                deleteString.get()
                    .addOnCompleteListener {
                        val requestsCount = it.result.data?.get("count").toString().toInt()
                        if (it.isSuccessful && requestsCount != requestNum) {
                            deleteString.update("count", requestsCount - 1)
                            deleteRequest(requestNum, requestsCount)
                        } else if (requestsCount == requestNum) {
                            deleteString.update("count", requestsCount - 1)
                            deleteRequest(requestNum)
                        }
                    }
                this.cancel()
            }
            dialogBinding.imgReduce.setOnClickListener {
                if (newCount > 1) {
                    newCount--
                    dialogBinding.needUsersTxt.text = newCount.toString()
                }
            }
            dialogBinding.imgIncrease.setOnClickListener {
                if (newCount < 5) {
                    newCount++
                    dialogBinding.needUsersTxt.text = newCount.toString()
                }
            }
            window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        dialog.show()
    }

    private fun deleteRequest(requestNum: Int, listSize: Int) {
        store.collection("Requests")
            .document("Request${listSize}").get()
            .addOnCompleteListener {
                store.collection("Requests")
                    .document("Request${requestNum}").set(
                        requestListViewModel.documentToRequest(it.result)
                    )
                store.collection("Requests")
                    .document("Request${listSize}").delete()
                requestListViewModel.getRequestList(store)
            }
    }

    private fun deleteRequest(requestNum: Int) {
        store.collection("Requests")
            .document("Request${requestNum}").delete()
        requestListViewModel.getRequestList(store)
    }
}