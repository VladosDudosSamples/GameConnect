package diplom.project.gameconnect.view.fragment

import android.content.Intent
import android.graphics.drawable.GradientDrawable.Orientation
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.FirebaseFirestore
import diplom.project.gameconnect.R
import diplom.project.gameconnect.app.App
import diplom.project.gameconnect.databinding.FragmentAdditionalBinding
import diplom.project.gameconnect.model.Platform
import diplom.project.gameconnect.model.UserInfo
import diplom.project.gameconnect.statik.SelectedPlatforms
import diplom.project.gameconnect.statik.SelectedPlatforms.listPlatforms
import diplom.project.gameconnect.statik.SelectedPlatforms.selectedPlatform
import diplom.project.gameconnect.view.activity.MainActivity
import diplom.project.gameconnect.view.adapter.PlatformsAdapter

class AdditionalIfoFragment : Fragment() {

    private val binding: FragmentAdditionalBinding by lazy {
        FragmentAdditionalBinding.inflate(
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
            if (checkInput()) {
                setUserInfo()
            }
        }
        binding.switchGender.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) binding.textGender.text = getString(R.string.women)
            else binding.textGender.text = resources.getText(R.string.man)
        }
    }

    private fun checkInput(): Boolean {
        when {
            binding.nickEdit.text.length <= 5 -> makeToast(getString(R.string.short_nick))
            binding.telegramId.text.toString()
                .isEmpty() -> makeToast(getString(R.string.no_telegram_id))

            selectedPlatform.isEmpty() -> makeToast(getString(R.string.empty_list_platforms))
            else -> return true
        }
        return false
    }

    private fun makeToast(m: String) {
        Toast.makeText(activity, m, Toast.LENGTH_SHORT).show()
    }

    private fun setUserInfo() {
        val newInfo: UserInfo = UserInfo(
            binding.nickEdit.text.toString(),
            selectedPlatform,
            binding.telegramId.text.toString(),
            binding.switchGender.isChecked,
            50,
            "https://",
            listOf(),
            ""
        )

        store.collection("Users").document("user:${App.dm.getUserKey()}")
            .set(newInfo)
            .addOnCompleteListener { d ->
                if (d.isSuccessful) {
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    selectedPlatform.clear()
                } else makeToast(d.exception!!.message.toString())
            }

    }

    private fun setAdapter() {
        binding.rvPlatforms.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvPlatforms.adapter = PlatformsAdapter(
            listPlatforms, requireContext()
        )
    }
}