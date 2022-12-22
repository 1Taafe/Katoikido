package by.taafe.katoikido.classes

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Message {

    companion object{
        const val undefined = "Undefined"
    }

    var id: String = undefined
    var sender: String = undefined
    var text: String = undefined
    var date: String = getCurrentDate()
    var phone: String = undefined

    private fun getCurrentDate() : String{
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        return LocalDateTime.now().format(formatter)
    }

}