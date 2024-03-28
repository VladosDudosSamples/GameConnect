package diplom.project.gameconnect.model

data class Request(
    var userNick: String,
    var needUsers: Int,
    var gender: Boolean,
    var gameName: String,
    var comment: String,
    var userRating: Int,
    var telegramId: String
)