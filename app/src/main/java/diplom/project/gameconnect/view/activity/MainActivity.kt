package diplom.project.gameconnect.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import diplom.project.gameconnect.R
import diplom.project.gameconnect.app.App
import diplom.project.gameconnect.databinding.ActivityMainBinding
import diplom.project.gameconnect.statik.User
import diplom.project.gameconnect.statik.User.userData


class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        App.dm.passLogin()
        navigationControl()
        getUser()
    }

    private fun navigationControl() =
        binding.bottomNavigation.setupWithNavController((supportFragmentManager.findFragmentById(R.id.nav_main_fragment) as NavHostFragment).navController)

    private fun getUser() {
        FirebaseFirestore.getInstance().collection("Users").document("user:${App.dm.getUserKey()}")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val list =  it.result.data?.get("gamesList").toString().replace("[", "")
                        .replace("]", "").split(",")
                    userData.nick = it.result.data?.get("nick").toString()
                    userData.listPlatform =
                        it.result.data?.get("listPlatform").toString().replace("[", "")
                            .replace("]", "").replace(",", "").split(" ")
                    userData.telegramId = it.result.data?.get("telegramId").toString()
                    userData.gender = it.result.data?.get("gender").toString().toBoolean()
                    userData.rating = it.result.data?.get("rating").toString().toInt()
                    userData.gamesList = if (list[0] == "") mutableListOf() else list
                    userData.profileImage = it.result.data?.get("profileImage").toString()
                }
            }
    }
}