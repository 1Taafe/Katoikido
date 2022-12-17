package by.taafe.katoikido

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Message {

    companion object{
        const val undefined = "Undefined"
    }

    var id: String = Message.undefined
    var sender: String = Message.undefined
    var text: String = Message.undefined
    var date: String = getCurrentDate()
    var phone: String = Message.undefined

    private fun getCurrentDate() : String{
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        return LocalDateTime.now().format(formatter)
    }

}