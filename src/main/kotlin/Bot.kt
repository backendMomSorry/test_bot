import com.fasterxml.jackson.databind.ObjectMapper
import entities.UserPhotos
import enums.Buttons
import enums.UserState
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.util.ArrayList
import java.util.concurrent.ConcurrentHashMap

class Bot : TelegramLongPollingCommandBot() {

    private val userStates = ConcurrentHashMap<Long, UserState>()    // todo заменить на кэлбеки, если можно. Либо нужно удалять неактивные состояния
    private val userPhotosMap = ConcurrentHashMap<Long, MutableList<UserPhotos>>() //  todo должно храниться в БД
    private val objectMapper = ObjectMapper()

    override fun getBotUsername(): String {
        return "Fredfemkabot"
    }

    override fun getBotToken(): String {
        return "1862656301:AAGJ02sbnJKbv-xQbkmXlJS9hmCyF29XDYY"
    }

    override fun processNonCommandUpdate(update: Update?) {
        val sendMessage = SendMessage()
        setPrimaryButtons(sendMessage)
        sendMessage(sendMessage, update?.message?.chatId, update?.message?.text)
    }

    private fun sendMessage(sendMessage: SendMessage, chatId: Long?, text: String?) {
        sendMessage.enableMarkdown(true)
        sendMessage.chatId = chatId.toString()
        sendMessage.text = text ?: ""
        try {
            super.execute(sendMessage)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    private fun setPrimaryButtons(sendMessage: SendMessage) {
        val replyKeyboardMarkup = ReplyKeyboardMarkup()
        sendMessage.replyMarkup = replyKeyboardMarkup
        replyKeyboardMarkup.selective = true
        replyKeyboardMarkup.resizeKeyboard = true
        replyKeyboardMarkup.oneTimeKeyboard = false

        val keyboard: MutableList<KeyboardRow> = ArrayList()

        val keyboardFirstRow = KeyboardRow()
        keyboardFirstRow.add(KeyboardButton(Buttons.ADD_NEW_PHOTOS.buttonName));

        val keyboardSecondRow = KeyboardRow()
        keyboardSecondRow.add(KeyboardButton(Buttons.PHOTO_LIST.buttonName))

        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);

        replyKeyboardMarkup.keyboard = keyboard;
    }

    override fun onUpdatesReceived(updates: MutableList<Update>) {
        val update = updates[0]
        when {
            update.hasMessage() -> {
                val sendMessage = SendMessage()
                val chatId = update.message.chatId

                val userCondition = userStates[chatId]
                if (userCondition == UserState.ADDING_PHOTOS) {
                    userStates[chatId] = UserState.NOTHING

                    addUserPhoto(update, chatId)
                } else {
                    doMessageCommand(update, chatId, sendMessage)
                }
            }

            update.hasCallbackQuery() -> {
                doCallbackQueryCommand(update)
            }

            else -> {
                super.onUpdatesReceived(updates)
            }
        }
    }

    private fun doCallbackQueryCommand(update: Update) {
        val chatId = update.callbackQuery.from.id
        val photos = userPhotosMap[chatId]
        photos
            ?.find { it.id == update.callbackQuery.data.toLong() }
            ?.let {
                val sendMessage = SendMessage()
                sendMessage(sendMessage, chatId, "Author: ${it.author}\nID: ${it.id}\nURL: ${it.url}")
            }
    }

    private fun doMessageCommand(update: Update, chatId: Long, sendMessage: SendMessage) {
        when (update.message.text) {
            Buttons.ADD_NEW_PHOTOS.buttonName -> {
                userStates[chatId] = UserState.ADDING_PHOTOS
                sendMessage(sendMessage, update.message?.chatId, "send a link")
            }
            Buttons.PHOTO_LIST.buttonName -> {
                setInlineButtonsWithPhotos(sendMessage, chatId)
                sendMessage(sendMessage, update.message?.chatId, "photo list")
            }
            else -> processNonCommandUpdate(update)
        }
    }

    private fun addUserPhoto(update: Update, chatId: Long) {
        val userPhotos = objectMapper.readValue(update.message.text, UserPhotos::class.java)
        userPhotos.userId = chatId

        val list = userPhotosMap[update.message.chatId]
        if (list == null) {
            userPhotosMap[update.message.chatId] = mutableListOf(userPhotos)
        } else {
            userPhotosMap[update.message.chatId]!!.add(userPhotos)
        }
    }

    private fun setPhotoListButtons(chatId: Long, sendMessage: SendMessage) {
        val replyKeyboardMarkup = ReplyKeyboardMarkup()
        sendMessage.replyMarkup = replyKeyboardMarkup
        replyKeyboardMarkup.selective = true
        replyKeyboardMarkup.resizeKeyboard = true
        replyKeyboardMarkup.oneTimeKeyboard = false

        val keyboard: List<KeyboardRow> = userPhotosMap[chatId]?.map {
            KeyboardRow().also { keyboardRow ->
                keyboardRow.add(KeyboardButton("${it.author} (${it.id})"))
            }
        } ?: emptyList()

        replyKeyboardMarkup.keyboard = keyboard;
    }

    private fun setInlineButtonsWithPhotos(sendMessage: SendMessage, chatId: Long) {
        val buttons: MutableList<List<InlineKeyboardButton>> = ArrayList()
        val buttons1: MutableList<InlineKeyboardButton> = ArrayList()

        userPhotosMap[chatId]?.forEach {
            KeyboardRow().also { keyboardRow ->
                val inlineKeyboardButton = InlineKeyboardButton()
                inlineKeyboardButton.text = "${it.author} (${it.id})"
                inlineKeyboardButton.callbackData = "${it.id}"
                buttons1.add(inlineKeyboardButton)
            }
        }

        buttons.add(buttons1)
        val markupKeyboard = InlineKeyboardMarkup()
        markupKeyboard.keyboard = buttons

        sendMessage.replyMarkup = markupKeyboard
    }

}