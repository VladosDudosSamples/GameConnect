package diplom.project.gameconnect.model

data class Request(
    var id: Int,
    var userNick: String,
    var needUsers: String,
    var gender: Boolean,
    var gameName: String,
    var comment: String,
    var userRating: String,
    var telegramId: String,
    var date: String,
    var platformList: List<String>,
    var profileImage: String,
    var userId: String
)