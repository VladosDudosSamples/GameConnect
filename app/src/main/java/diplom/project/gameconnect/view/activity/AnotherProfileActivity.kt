package diplom.project.gameconnect.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import diplom.project.gameconnect.R
import diplom.project.gameconnect.app.App
import diplom.project.gameconnect.databinding.ActivityAnotherProfileBinding
import diplom.project.gameconnect.model.UserInfo
import diplom.project.gameconnect.statik.SelectedPlatforms
import diplom.project.gameconnect.statik.User
import diplom.project.gameconnect.statik.User.userIdForAnotherProfile
import diplom.project.gameconnect.view.adapter.AlternativePlatformAdapter
import diplom.project.gameconnect.view.adapter.AnotherGamesAdapter
import diplom.project.gameconnect.view.adapter.GamesAdapter

class AnotherProfileActivity : AppCompatActivity() {

    private val binding: ActivityAnotherProfileBinding by lazy {
        ActivityAnotherProfileBinding.inflate(
            layoutInflater
        )
    }
    private lateinit var anotherUser: UserInfo
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        onClick()
        getAnotherUser()
    }

    private fun onClick() {
        binding.backBtn.setOnClickListener {
            this.finish()
        }
    }

    private fun setAdapters() {
        setPlatformAdapter()
        setGamesAdapter()
    }

    private fun getAnotherUser(){
        anotherUser = UserInfo("", listOf(), "", false, 0, "https://", listOf() , "")
        FirebaseFirestore.getInstance().collection("Users")
            .document("user:${userIdForAnotherProfile}")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val list = it.result.data?.get("gamesList").toString().replace("[", "")
                        .replace("]", "").split(",")
                    println(list)

                    anotherUser.nick = it.result.data?.get("nick").toString()
                    anotherUser.listPlatform =
                        it.result.data?.get("listPlatform").toString().replace("[", "")
                            .replace("]", "").replace(",", "").split(" ")
                    anotherUser.gender = it.result.data?.get("gender").toString().toBoolean()
                    anotherUser.rating = it.result.data?.get("rating").toString().toInt()
                    anotherUser.gamesList = if (list[0] == "") mutableListOf() else list
                    anotherUser.profileImage = it.result.data?.get("profileImage").toString()

                    setUser(anotherUser)

                    setAdapters()
                }
            }
    }

    private fun setPlatformAdapter() {
        binding.rvPlatformsProfile.layoutManager = LinearLayoutManager(this)
        binding.rvPlatformsProfile.adapter =
            AlternativePlatformAdapter(SelectedPlatforms.listPlatforms, this, anotherUser)
    }

    private fun setGamesAdapter() {
        binding.rvGames.layoutManager = LinearLayoutManager(this)
        binding.rvGames.adapter =
            AnotherGamesAdapter(anotherUser.gamesList, this)
    }

    private fun setUser(userInfo: UserInfo) {
        binding.nickName.text = userInfo.nick
        binding.genderTxt.text =
            "${resources.getString(R.string.gender)} ${if (userInfo.gender) "Ж" else "М"}"
        binding.ratingTxt.text = "${resources.getString(R.string.poradok)} ${userInfo.rating}%"

        Glide.with(binding.imageUser)
            .load(userInfo.profileImage)
            .error(R.drawable.baseline_person_24)
            .into(binding.imageUser)
    }
}