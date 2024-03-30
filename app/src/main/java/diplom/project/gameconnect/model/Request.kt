package diplom.project.gameconnect.model

data class Request(
    var userNick: String,
    var needUsers: String,
    var gender: Boolean,
    var gameName: String,
    var comment: String,
    var userRating: String,
    var telegramId: String,
    var date: String
)