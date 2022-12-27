package by.taafe.katoikido.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import by.taafe.katoikido.classes.Post


class DatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val usersTableQuery = ("CREATE TABLE " + USERS_TABLE + "("
                + PHONE_COLUMN + " TEXT PRIMARY KEY, "
                + NAME_COLUMN + " TEXT"
                + ")")
        val favsTableQuery = "CREATE TABLE $FAVS_TABLE($ID_COLUMN TEXT PRIMARY KEY, $TYPE_COLUMN TEXT, $TITLE_COLUMN TEXT," +
                " $PETTYPE_COLUMN TEXT, $TEXT_COLUMN TEXT, $DATE_COLUMN TEXT, $IMAGE_COLUMN TEXT, $PHONE_COLUMN TEXT, " +
                "FOREIGN KEY($PHONE_COLUMN) REFERENCES $USERS_TABLE($PHONE_COLUMN))"
        db.execSQL(usersTableQuery)
        db.execSQL(favsTableQuery)

        val phoneIndex = "CREATE INDEX IF NOT EXISTS favs_phone_index on $FAVS_TABLE ($PHONE_COLUMN)"
        db.execSQL(phoneIndex)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $USERS_TABLE")
        db.execSQL("DROP TABLE IF EXISTS $FAVS_TABLE")
        onCreate(db)
    }

    override fun onConfigure(db: SQLiteDatabase) {
        db.setForeignKeyConstraintsEnabled(true)
    }

    companion object {
        private const val DB_NAME = "katoikido.db"
        private const val DB_VERSION = 1

        const val USERS_TABLE = "users"
        const val PHONE_COLUMN = "phone"
        private const val NAME_COLUMN = "name"


        const val FAVS_TABLE = "favs"
        private const val ID_COLUMN = "id"
        private const val TYPE_COLUMN = "type"
        private const val TITLE_COLUMN = "title"
        private const val PETTYPE_COLUMN = "petType"
        private const val TEXT_COLUMN = "text"
//        private val PHONE_COLUMN = "phone"
        private const val DATE_COLUMN = "uploadDate"
        private const val IMAGE_COLUMN = "imageUrl"

        private val favsUri: Uri = Uri.parse("content://by.katoikido.provider/favs")
        private val usersUri: Uri = Uri.parse("content://by.katoikido.provider/users")

        @SuppressLint("StaticFieldLeak")
        var databaseHelper: DatabaseHelper? = null
        var database: SQLiteDatabase? = null
        @SuppressLint("StaticFieldLeak")
        var context: Context? = null

        fun init(context: Context?) {
            databaseHelper = DatabaseHelper(context)
            database = databaseHelper!!.writableDatabase
            this.context = context
            database?.close()
        }

        fun getFavoritePosts() : ArrayList<Post>{
            val resultList = ArrayList<Post>()
            val postCursor = context?.contentResolver?.query(favsUri, null, null, null)!!
            while(postCursor.moveToNext()){
                val post = Post()
                post.id = postCursor.getString(0)
                post.type = postCursor.getString(1)
                post.title = postCursor.getString(2)
                post.petType = postCursor.getString(3)
                post.text = postCursor.getString(4)
                post.uploadDate = postCursor.getString(5)
                post.imageUrl = postCursor.getString(6)
                post.ownerPhone = postCursor.getString(7)
                post.ownerName = postCursor.getString(9)
                resultList.add(post)
            }
            postCursor.close()
            return resultList
        }

        fun deleteFavoritePost(post: Post){
            context?.contentResolver?.delete(favsUri, "id = ?", arrayOf(post.id))
        }

        fun putFavoritePost(post: Post) : Int{
            var isPut = 1
            var contentValues = ContentValues()
            contentValues.put(NAME_COLUMN, post.ownerName)
            contentValues.put(PHONE_COLUMN, post.ownerPhone)

            if(context!!.contentResolver.insert(usersUri, contentValues) == null){
                context?.contentResolver?.update(usersUri, contentValues, "$PHONE_COLUMN = ?", arrayOf(post.ownerPhone))
            }

            contentValues = ContentValues()
            contentValues.put(ID_COLUMN, post.id)
            contentValues.put(TYPE_COLUMN, post.type)
            contentValues.put(TITLE_COLUMN, post.title)
            contentValues.put(PETTYPE_COLUMN, post.petType)
            contentValues.put(TEXT_COLUMN, post.text)
            contentValues.put(DATE_COLUMN, post.uploadDate)
            contentValues.put(IMAGE_COLUMN, post.imageUrl)
            contentValues.put(PHONE_COLUMN, post.ownerPhone)


            if(context!!.contentResolver.insert(favsUri, contentValues) == null){
                context?.contentResolver?.delete(favsUri, "id = ?", arrayOf(post.id))
                isPut = 0
            }
            return isPut
        }
    }
}