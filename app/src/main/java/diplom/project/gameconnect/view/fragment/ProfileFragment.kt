package diplom.project.gameconnect.view.fragment

import android.Manifest.permission.CAMERA
import android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import diplom.project.gameconnect.R
import diplom.project.gameconnect.app.App
import diplom.project.gameconnect.databinding.DialogAddGameBinding
import diplom.project.gameconnect.databinding.DialogRequestBinding
import diplom.project.gameconnect.databinding.FragmentProfileBinding
import diplom.project.gameconnect.model.UserInfo
import diplom.project.gameconnect.statik.SelectedPlatforms.listPlatforms
import diplom.project.gameconnect.statik.User.userData
import diplom.project.gameconnect.view.activity.StartActivity
import diplom.project.gameconnect.view.adapter.AlternativePlatformAdapter
import diplom.project.gameconnect.view.adapter.GamesAdapter
import diplom.project.gameconnect.view.adapter.PlatformsAdapter
import java.io.File
import java.util.jar.Manifest

class ProfileFragment : Fragment(), GamesAdapter.OnClick {

    private val storage = FirebaseFirestore.getInstance()
    private lateinit var takePicture: ActivityResultLauncher<Uri>
    private lateinit var imageResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var imageUri: Uri
    private val store =
        FirebaseStorage.getInstance().reference.child("user:${App.dm.getUserKey()}_img")

    override fun click(textView: TextView) {
        val list = mutableListOf<String>()
        for (i in userData.gamesList) {
            if (i != textView.text) list.add(i)
        }
        storage.collection("Users").document("user:${App.dm.getUserKey()}")
            .update("gamesList", list)
            .addOnCompleteListener {
                userData.gamesList = list
                setGamesAdapter(false)
            }
            .addOnFailureListener {
                makeToast(resources.getString(R.string.some_problems_went))
                setGamesAdapter(false)
            }
    }

    private val binding: FragmentProfileBinding by lazy {
        FragmentProfileBinding.inflate(
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

        setUser(userData)
        onClick()
        setAdapters()
        imageUri = createUri()
        registerPL()
        registerIRL()
    }

    private fun onClick() {
        binding.logoutBtn.setOnClickListener {
            App.dm.logout()
            startActivity(Intent(requireActivity(), StartActivity::class.java))
            requireActivity().finish()
        }
        binding.imgReduce.setOnClickListener {
            setGamesAdapter(true)
        }
        binding.imgIncrease.setOnClickListener {
            setGamesAdapter(false)
            val dialogBinding: DialogAddGameBinding by lazy {
                DialogAddGameBinding.inflate(
                    layoutInflater
                )
            }
            val dialog = Dialog(requireContext()).apply {
                setCancelable(true)
                setContentView(dialogBinding.root)
                dialogBinding.okBtn.setOnClickListener {
                    if (checkInputDialog(dialogBinding)) {
                        val list = mutableListOf<String>()
                        for (i in userData.gamesList) {
                            list.add(i)
                        }
                        list.add(dialogBinding.gameNameTxt.text.toString())
                        storage.collection("Users").document("user:${App.dm.getUserKey()}")
                            .update("gamesList", list)
                            .addOnFailureListener {
                                makeToast(resources.getString(R.string.some_problems_went))
                            }
                        this.cancel()
                        userData.gamesList = list
                        setGamesAdapter(false)
                    }
                }
                window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            dialog.show()
        }
        binding.editImgLayout.setOnClickListener {
            getPic()
        }
    }

    private fun setAdapters() {
        setPlatformAdapter()
        setGamesAdapter(false)
    }

    private fun setPlatformAdapter() {
        binding.rvPlatformsProfile.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPlatformsProfile.adapter =
            AlternativePlatformAdapter(listPlatforms, requireContext(), userData)
    }

    private fun setGamesAdapter(isDeleting: Boolean) {
        binding.rvGames.layoutManager = LinearLayoutManager(requireContext())
        binding.rvGames.adapter =
            GamesAdapter(userData.gamesList, requireContext(), this, isDeleting)
    }

    private fun setUser(userInfo: UserInfo) {
        binding.tgId.text = userInfo.telegramId
        binding.nickName.text = userInfo.nick
        binding.genderTxt.text =
            "${resources.getString(R.string.gender)} ${if (userInfo.gender) "Ж" else "М"}"
        binding.ratingTxt.text = "${resources.getString(R.string.poradok)} ${userInfo.rating}%"

        Glide.with(binding.imageUser)
            .load(userInfo.profileImage)
            .error(R.drawable.baseline_person_24)
            .into(binding.imageUser)
    }

    private fun checkInputDialog(b: DialogAddGameBinding): Boolean {
        when {
            b.gameNameTxt.text.toString().length < 2 -> makeToast(getString(R.string.game_name_too_short))
            else -> return true
        }
        return false
    }

    private fun makeToast(m: String) {
        Toast.makeText(activity, m, Toast.LENGTH_SHORT).show()
    }

    private fun getPic() {
        MaterialDialog(requireContext())
            .title(text = "Choose from ")
            .listItemsSingleChoice(
                items = listOf("Gallery", "Camera"),
                selection = { dialog, index, text ->
                    if (index == 0) gallery() else openCamera()
                    dialog.cancel()
                }).show {}
    }

    private fun gallery() {
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                MANAGE_EXTERNAL_STORAGE
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                ActivityCompat.requestPermissions(requireActivity(), arrayOf(MANAGE_EXTERNAL_STORAGE), 11)
//            }
//            else{
//                ActivityCompat.requestPermissions(requireActivity(), arrayOf(READ_EXTERNAL_STORAGE), 11)
//            }
//        } else {
        val gi = Intent(Intent.ACTION_PICK)
        gi.setType("image/*")
        imageResultLauncher.launch(gi)
//        }
    }


    private fun createUri(): Uri {
        val imageFile = File(requireContext().applicationContext.filesDir, "camera_photo.jpg")
        return FileProvider.getUriForFile(
            requireContext().applicationContext,
            "diplom.project.gameconnect.fileprovider",
            imageFile
        )
    }

    private fun registerIRL() {
        imageResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            try {
                imageUri = it.data?.data!!
                Glide.with(binding.imageUser)
                    .load(imageUri)
                    .into(binding.imageUser)
                store.putFile(imageUri)
                store.downloadUrl.addOnCompleteListener { uri ->
                    FirebaseFirestore.getInstance().collection("Users")
                        .document("user:${App.dm.getUserKey()}")
                        .update(
                            "profileImage",
                            uri.result
                        )
                }
            } catch (e: Exception) {
                e.stackTrace
            }
        }
    }

    private fun registerPL() {
        takePicture = registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) {
            try {
                if (it) {
                    Glide.with(binding.imageUser)
                        .load(imageUri)
                        .into(binding.imageUser)
                    store.putFile(imageUri)
                    store.downloadUrl.addOnCompleteListener { uri ->
                        FirebaseFirestore.getInstance().collection("Users")
                            .document("user:${App.dm.getUserKey()}")
                            .update(
                                "profileImage",
                                uri.result
                            )
                    }

                }
            } catch (e: Exception) {
                e.stackTrace
            }
        }
    }

    private fun openCamera() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(CAMERA), 10)
        } else {
            takePicture.launch(imageUri)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10) {
            if (grantResults.isNotEmpty() && grantResults.any { it == PackageManager.PERMISSION_GRANTED }) {
                takePicture.launch(imageUri)
            } else {
                makeToast(getString(R.string.make_app_use_camera))
            }
        } else if (requestCode == 11) {
            if (grantResults.isNotEmpty() && grantResults.any { it == PackageManager.PERMISSION_GRANTED }) {
                gallery()
            } else {
                makeToast(getString(R.string.make_app_use_gallery))
            }
        }
    }
}