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
import diplom.project.gameconnect.databinding.FragmentProfileBinding
import diplom.project.gameconnect.databinding.FragmentRequestBinding
import diplom.project.gameconnect.model.Request
import diplom.project.gameconnect.view.adapter.RequestAdapter

class RequestFragment : Fragment() {

    private val binding: FragmentRequestBinding by lazy {
        FragmentRequestBinding.inflate(
            layoutInflater
        )
    }
    private var store = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClick()
        setAdapter()
    }

    private fun onClick() {
        binding.mainButton.setOnClickListener {
            store.collection("Requests").document("Request${App.dm.getUserKey()}").get()
                .addOnFailureListener {
                    store.collection("Users").document("user:${App.dm.getUserKey()}").get()
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                store.collection("Requests")
                                    .document("Request${App.dm.getUserKey()}")
                                    .set(
                                        Request(
                                            it.result.get("nick").toString(),
                                            0,
                                            it.result.get("gender").toString().toBoolean(),
                                            "",
                                            "",
                                            it.result.get("rating").toString().toInt(),
                                            it.result.get("telegramId").toString()
                                        )
                                    )
                            }
                        }

                }

        }
    }

    private fun setAdapter() {
        binding.rvRequests.layoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
        binding.rvRequests.adapter = RequestAdapter(listOf(), requireContext())
    }
}