package diplom.project.gameconnect.statik

import com.google.firebase.firestore.FirebaseFirestore
import diplom.project.gameconnect.app.App
import diplom.project.gameconnect.model.Platform
import diplom.project.gameconnect.model.Request

object SelectedPlatforms {
    val selectedPlatform = mutableListOf<String>()
    val listPlatforms = listOf("PC(ПК)","XBOX","PlayStation","NintendoSwitch","Mobile")
}