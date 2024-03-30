package diplom.project.gameconnect.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import diplom.project.gameconnect.model.Request

class RequestListViewModel : ViewModel() {
    val requestList = MutableLiveData<MutableList<Request>>()
    var isFunDone = MutableLiveData<Boolean>()
    private var resultList = mutableListOf<Request>()
    private var requestsCount = 0
    private var deleteNum = 0

    fun getRequestList(store: FirebaseFirestore) {
        resultList.clear()
        store.collection("Requests").document("RequestCount").get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    requestsCount = it.result.get("count").toString().toInt()
                    if (requestsCount == 0) return@addOnCompleteListener
                    deleteNum = it.result.get("lastDelete").toString().toInt()
                    getRequestsWithCount(store)
                }
            }
    }

    fun getCountAndDelete(): Pair<Int, Int> = Pair(requestsCount, deleteNum)

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

    private fun documentToRequest(doc: DocumentSnapshot): Request = Request(
        doc.data?.get("userNick").toString(),
        doc.data?.get("needUsers").toString(),
        doc.data?.get("gender").toString().toBoolean(),
        doc.data?.get("gameName").toString(),
        doc.data?.get("comment").toString(),
        doc.data?.get("userRating").toString(),
        doc.data?.get("telegramId").toString(),
        doc.data?.get("date").toString()
    )

    fun changeCountRequests(store: FirebaseFirestore, changeNum: Int) {
        store.collection("Requests").document("RequestCount").update(
            "count", requestsCount + changeNum
        )
    }
}