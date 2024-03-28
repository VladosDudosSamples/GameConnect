package diplom.project.gameconnect.app

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp
import diplom.project.gameconnect.data.DataManager

class App : Application() {
    companion object{
        lateinit var dm: DataManager
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        dm = DataManager(baseContext)
        appContext = applicationContext
    }
}