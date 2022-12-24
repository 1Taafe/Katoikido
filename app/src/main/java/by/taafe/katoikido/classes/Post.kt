package by.taafe.katoikido.classes

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.NodeList
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult


class Post() {

    companion object{
        const val undefined = "Undefined"
        var tempContainer = Post()
        var Favorites = ArrayList<Post>()

        fun serializeToJson(dir: File?, list: ArrayList<Post>) {
            try {
                val gson = Gson()
                val fw = FileWriter(File(dir, "favorites.json"))
                val json: String = gson.toJson(list)
                gson.toJson(list, fw)
                fw.close()
            } catch (_: Exception) {
            }
        }

        fun deserializeFromJson(dir: File?) : ArrayList<Post> {
            var newCollection: ArrayList<Post> = ArrayList()
            try {
                val gson = Gson()
                val fr = FileReader(File(dir, "favorites.json"))
                newCollection =
                    gson.fromJson(fr, object : TypeToken<ArrayList<Post>>() {}.type)
            }
            catch (_: Exception) {

            }
            return newCollection
        }

        fun serializeToXml(context: Context?, list: ArrayList<Post>){
            try {
                val factory: DocumentBuilderFactory = DocumentBuilderFactory.newInstance()
                val builder: DocumentBuilder = factory.newDocumentBuilder()
                val doc: Document = builder.newDocument()
                val posts: Element = doc.createElement("Posts")
                doc.appendChild(posts)
                for (post in list) {
                    val newPost: Element = doc.createElement("Post")
                    newPost.setAttribute("ID", post.id)
                    newPost.setAttribute("Type", post.type)
                    newPost.setAttribute("Title", post.title)
                    newPost.setAttribute("PetType", post.petType)
                    newPost.setAttribute("Text", post.text)
                    newPost.setAttribute("OwnerPhone", post.ownerPhone)
                    newPost.setAttribute("OwnerName", post.ownerName)
                    newPost.setAttribute("UploadDate", post.uploadDate)
                    newPost.setAttribute("ImageUrl", post.imageUrl)
                    posts.appendChild(newPost)
                }
                val tf: TransformerFactory = TransformerFactory.newInstance()
                val t: Transformer = tf.newTransformer()
                val src = DOMSource(doc)
                val fos: FileOutputStream =
                    context!!.openFileOutput("favorites.xml", Context.MODE_PRIVATE)
                val sr = StreamResult(fos)
                t.transform(src, sr)
                fos.close()
            }
            catch (_: Exception) {

            }
        }

        fun deserializeFromXml(dir: File?) : ArrayList<Post> {
            val newPosts: ArrayList<Post> = ArrayList()
            try {
                val factory = DocumentBuilderFactory.newInstance()
                val builder = factory.newDocumentBuilder()
                val f = File(dir, "favorites.xml")
                val doc = builder.parse(f)
                val nl: NodeList = doc.getElementsByTagName("Post")
                for (i in 0 until nl.length) {
                    val post = Post()
                    val map: NamedNodeMap = nl.item(i).attributes
                    post.id = map.getNamedItem("ID").nodeValue
                    post.type = map.getNamedItem("Type").nodeValue
                    post.title = map.getNamedItem("Title").nodeValue
                    post.petType = map.getNamedItem("PetType").nodeValue
                    post.text = map.getNamedItem("Text").nodeValue
                    post.ownerPhone = map.getNamedItem("OwnerPhone").nodeValue
                    post.ownerName = map.getNamedItem("OwnerName").nodeValue
                    post.uploadDate = map.getNamedItem("UploadDate").nodeValue
                    post.imageUrl = map.getNamedItem("ImageUrl").nodeValue
                    newPosts.add(post)
                }
            }
            catch (_: Exception) {

            }
            return newPosts
        }


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