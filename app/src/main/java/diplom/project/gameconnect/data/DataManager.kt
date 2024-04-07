package diplom.project.gameconnect.data

import android.content.Context
import com.google.common.reflect.TypeToken
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import com.google.gson.Gson
import diplom.project.gameconnect.R
import diplom.project.gameconnect.model.Request
import diplom.project.gameconnect.model.UserInfo

class DataManager(private val baseContext: Context) {
    private val appName = baseContext.resources.getString(R.string.app_name)
    private val preferences = baseContext.getSharedPreferences(appName, Context.MODE_PRIVATE)

    fun isOnBoardingPassed(): Boolean =
        preferences.getBoolean(baseContext.getString(R.string.onboard_passed), false)

    fun passOnBoarding() =
        preferences.edit().putBoolean(baseContext.getString(R.string.onboard_passed), true).apply()

    fun passLogin() =
        preferences.edit().putBoolean(baseContext.getString(R.string.login_passed), true).apply()

    fun isLoginPassed(): Boolean =
        preferences.getBoolean(baseContext.getString(R.string.login_passed), false)

    fun isAdditionalInfoEntered(userId: String): Boolean {
        var result = false
        try {
            FirebaseFirestore.getInstance().collection("Users").document("user:${userId}").get()
                .addOnCompleteListener {
                    result = true
                }
        }
        catch (e:Exception) {return result}
        return result
    }

    fun logout() {
        preferences.edit().putBoolean(baseContext.getString(R.string.login_passed), false).apply()
        setUserKey("")
        preferences.edit().putBoolean(baseContext.getString(R.string.login_passed), false).apply()
        preferences.edit().putString(
            baseContext.getString(R.string.listlistteammates), ""
        ).apply()
        preferences.edit()
            .putBoolean(baseContext.getString(R.string.isAdditionalInfoEntered), false)
            .apply()
    }


    fun setUserKey(key: String) =
        preferences.edit().putString(baseContext.getString(R.string.user_key), key).apply()

    fun getUserKey(): String =
        preferences.getString(baseContext.getString(R.string.user_key), "") ?: ""

    fun setListTeammates(list: MutableList<Request>) {
        return preferences.edit()
            .putString(baseContext.getString(R.string.listlistteammates), Gson().toJson(list))
            .apply()
    }

    fun getListLastTeammates(): MutableList<Request> {
        return try {
            Gson().fromJson(
                preferences.getString(baseContext.getString(R.string.listlistteammates), "")
                    ?: mutableListOf<Request>().toString(),
                object : TypeToken<MutableList<Request>>() {}.type
            )
        } catch (e: Exception) {
            mutableListOf()
        }
    }
}