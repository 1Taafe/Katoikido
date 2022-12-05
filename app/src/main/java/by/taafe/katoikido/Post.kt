package by.taafe.katoikido

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Post() {

    var id: String = "Undefined";
    var type: String = "Undefined"
    var title: String = "Undefined"
    var animalType: String = "Undefined"
    var animalBreed: String = "Undefined"
    var postText: String = "Undefined"
    var ownerPhone: String = "Undefined"
    var ownerName: String = "Undefined"
    var uploadDate: String = getCurrentDate()
    var imageUrl : String = "Undefined"

    constructor(_id: String, _title: String, _postText: String) : this(){
        id = _id
        title = _title
        postText = _postText
    }

    constructor(_id : String) : this(){
        id = _id
    }

    fun getCurrentDate() : String{
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        return LocalDateTime.now().format(formatter)
    }

}