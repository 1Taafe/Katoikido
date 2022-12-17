package by.taafe.katoikido

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Post() {

    companion object{
        const val undefined = "Undefined"
    }

    var id: String = undefined
    var type: String = undefined
    var title: String = undefined
    var petType: String = undefined
    var text: String = undefined
    var ownerPhone: String = undefined
    var ownerName: String = undefined
    var uploadDate: String = getCurrentDate()
    var imageUrl : String = undefined

    constructor(_id: String, _title: String, _postText: String) : this(){
        id = _id
        title = _title
        text = _postText
    }

    constructor(_id : String) : this(){
        id = _id
    }

    override fun toString(): String {
        return "$title \t $text"
    }

    private fun getCurrentDate() : String{
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        return LocalDateTime.now().format(formatter)
    }

}