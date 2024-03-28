package diplom.project.gameconnect.data

import android.content.Context
import diplom.project.gameconnect.R

class DataManager(private val baseContext: Context) {
    private val appName = baseContext.resources.getString(R.string.app_name)
    private val preferences = baseContext.getSharedPreferences(appName, Context.MODE_PRIVATE)

    fun isOnBoardingPassed() : Boolean = preferences.getBoolean(baseContext.getString(R.string.onboard_passed), false)
    fun passOnBoarding() = preferences.edit().putBoolean(baseContext.getString(R.string.onboard_passed), true).apply()

    fun passLogin() = preferences.edit().putBoolean(baseContext.getString(R.string.login_passed), true).apply()
    fun isLoginPassed(): Boolean = preferences.getBoolean(baseContext.getString(R.string.login_passed), false)
    fun isAdditionalInfoEntered(): Boolean = preferences.getBoolean(baseContext.getString(R.string.isAdditionalInfoEntered), false)
    fun passAdditionalEnter() = preferences.edit().putBoolean(baseContext.getString(R.string.isAdditionalInfoEntered), true).apply()
    fun logout() = preferences.edit().putBoolean(baseContext.getString(R.string.login_passed), false).apply()

    fun setUserKey(key: String) = preferences.edit().putString(baseContext.getString(R.string.user_key), key).apply()
    fun getUserKey() : String = preferences.getString(baseContext.getString(R.string.user_key), "") ?: ""
}