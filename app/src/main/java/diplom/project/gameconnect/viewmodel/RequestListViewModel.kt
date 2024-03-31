package diplom.project.gameconnect.viewmodel

import android.content.Context
import android.util.Log
import android.view.MenuItem
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import diplom.project.gameconnect.R
import diplom.project.gameconnect.model.FilterType
import diplom.project.gameconnect.model.Request
import diplom.project.gameconnect.model.SortType

class RequestListViewModel : ViewModel() {
    val requestList = MutableLiveData<MutableList<Request>>()
    var isFunDone = MutableLiveData<Boolean>()
    private var resultList = mutableListOf<Request>()
    private var requestsCount = 0
    private var currentFilter: FilterType = FilterType.NO_FILTERS

    fun getRequestList(store: FirebaseFirestore) {
        resultList.clear()
        store.collection("Requests").document("RequestCount").get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    requestsCount = it.result.get("count").toString().toInt()
                    if (requestsCount == 0) return@addOnCompleteListener
                    getRequestsWithCount(store)
                }
            }
    }

    fun getCount(): Int = requestsCount

    private fun getRequestsWithCount(store: FirebaseFirestore) {
        for (i in 1..requestsCount) {
            store.collection("Requests").document("Request${i}").get()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        resultList.add(documentToRequest(it.result))
                        requestList.value = resultList
                        if (resultList.size == requestsCount) isFunDone.value = true
                    }
                }
        }
        isFunDone.value = false
    }

    fun documentToRequest(doc: DocumentSnapshot): Request = Request(
        doc.data?.get("id").toString().toInt(),
        doc.data?.get("userNick").toString(),
        doc.data?.get("needUsers").toString(),
        doc.data?.get("gender").toString().toBoolean(),
        doc.data?.get("gameName").toString(),
        doc.data?.get("comment").toString(),
        doc.data?.get("userRating").toString(),
        doc.data?.get("telegramId").toString(),
        doc.data?.get("date").toString(),
        doc.data?.get("platformList").toString().replace("[", "").replace("]", "").replace(",", "").split(" ")
    )

    fun changeCountRequests(store: FirebaseFirestore, changeNum: Int) {
        store.collection("Requests").document("RequestCount").update(
            "count", requestsCount + changeNum
        )
    }

    fun filterList(mi: MenuItem, context: Context): List<Request> {
        currentFilter = when (mi.title) {
            context.getString(R.string.pc) -> FilterType.PC
            context.getString(R.string.playstation) -> FilterType.PS
            context.getString(R.string.xbox) -> FilterType.XBOX
            context.getString(R.string.mobile) -> FilterType.MOBILE
            context.getString(R.string.nintendoswitch) -> FilterType.NS
            context.getString(R.string.man) -> FilterType.MALES
            context.getString(R.string.women) -> FilterType.FEMALES
            else -> FilterType.NO_FILTERS
        }
        return getFilteredList(context)
    }

    private fun getFilteredList(context: Context): List<Request> {
        requestList.value = resultList
        println(resultList)
        return when (currentFilter) {
            FilterType.PC -> requestList.value!!.filter {
                context.resources.getString(
                    R.string.pc
                ) in it.platformList
            }

            FilterType.PS -> requestList.value!!.filter {
                context.resources.getString(
                    R.string.playstation
                ) in it.platformList
            }

            FilterType.XBOX -> requestList.value!!.filter {
                context.resources.getString(
                    R.string.xbox
                ) in it.platformList
            }

            FilterType.MOBILE -> requestList.value!!.filter {
                context.resources.getString(
                    R.string.mobile
                ) in it.platformList
            }

            FilterType.NS -> requestList.value!!.filter {
                context.resources.getString(
                    R.string.nintendoswitch
                ) in it.platformList
            }

            FilterType.MALES -> requestList.value!!.filter {
                !it.gender
            }

            FilterType.FEMALES -> requestList.value!!.filter {
                it.gender
            }

            else -> requestList.value!!
        }
    }
}