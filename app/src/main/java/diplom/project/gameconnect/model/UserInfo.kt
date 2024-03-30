package diplom.project.gameconnect.model

data class UserInfo(
    var nick: String,
    var listPlatform: List<String>,
    var telegramId: String,
    var gender: Boolean,
    var rating: Int
)
