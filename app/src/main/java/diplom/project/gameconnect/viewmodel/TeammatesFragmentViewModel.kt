package diplom.project.gameconnect.viewmodel

import android.app.Dialog
import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.gson.Gson
import diplom.project.gameconnect.app.App
import diplom.project.gameconnect.model.Teammate
import diplom.project.gameconnect.model.TypeTeammate

class TeammatesFragmentViewModel : ViewModel() {
    val listToAccept = MutableLiveData<List<Teammate>>()
    private lateinit var listener: ListenerRegistration

    fun observeListToAccept(store: FirebaseFirestore, context: Context, dialog: Dialog) {
        listToAccept.value = App.dm.getListLastTeammates()
        listener = store.collection("Users").document("user:${App.dm.getUserKey()}")
            .addSnapshotListener { value, error ->
                val newList: List<Teammate> = Gson().fromJson(
                    if (value!!.data?.get("listToAccept").toString().isEmpty() || value.data?.get(
                            "listToAccept"
                        ).toString() == ""
                    ) "[]" else value.data?.get("listToAccept").toString(),
                    object : TypeToken<List<Teammate>>() {}.type
                )

                if (error != null) {
                    Log.d(error.code.toString(), error.message.toString())
                } else if (
                    newList.size
                    >= listToAccept.value!!.size && (newList.any {
                        it.type == TypeTeammate.WAITING
                    })
                ) {
                    listToAccept.value = newList
                    dialog.show()
                }
                else if (newList.size
                    != listToAccept.value!!.size){
                    listToAccept.value = newList
                }
            }
    }
    fun stopListening() {
        listener.remove()
    }
}