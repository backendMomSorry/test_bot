import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

fun main(args: Array<String>) {
    val telegramBotsApi = TelegramBotsApi(DefaultBotSession()::class.java)
    try {
        telegramBotsApi.registerBot(Bot())
    } catch (e: TelegramApiRequestException) {
        e.printStackTrace()
    }

}