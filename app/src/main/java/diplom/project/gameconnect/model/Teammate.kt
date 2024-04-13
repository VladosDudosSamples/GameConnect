package diplom.project.gameconnect.model

data class Teammate(
    var userNick: String,
    var gameName: String,
    var telegramId: String,
    var profileImage: String,
    var userId: String,
    var type: TypeTeammate
)
