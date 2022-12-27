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

//        //examdate and sort
//        val indx_Progresses_ExamDate =
//            "CREATE INDEX IF NOT EXISTS indx_Progress_ExamDate ON PROGRESS (EXAMDATE)"
//        val indx_Progress_mark =
//            "CREATE INDEX IF NOT EXISTS indx_Progress_mark ON PROGRESS (MARK DESC)"
//
//        //foreign keys
//        val indx_Student_idgroup =
//            "CREATE INDEX IF NOT EXISTS indx_Student_idgroup on STUDENT (IDGROUP)"
//        val indx_Group_idfaculty =
//            "CREATE INDEX IF NOT EXISTS indx_Group_idfaculty on GROUP_ (IDFACULTY)"
//        val indx_Progress_idstudent =
//            "CREATE INDEX IF NOT EXISTS indx_Progress_idstudent on PROGRESS (IDSTUDENT)"
//        val indx_Progress_idsubject =
//            "CREATE INDEX IF NOT EXISTS indx_Progress_idsubject on PROGRESS (IDSUBJECT)"
//        db.execSQL(indx_Student_idgroup)
//        db.execSQL(indx_Progresses_ExamDate)
//        db.execSQL(indx_Progress_mark)
//        db.execSQL(indx_Group_idfaculty)
//        db.execSQL(indx_Progress_idstudent)
//        db.execSQL(indx_Progress_idsubject)
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
        private val DB_NAME = "katoikido.db"
        private val DB_VERSION = 1

        private val USERS_TABLE = "users"
        private val PHONE_COLUMN = "phone"
        private val NAME_COLUMN = "name"


        private val FAVS_TABLE = "favs"
        private val ID_COLUMN = "id"
        private val TYPE_COLUMN = "type"
        private val TITLE_COLUMN = "title"
        private val PETTYPE_COLUMN = "petType"
        private val TEXT_COLUMN = "text"
//        private val PHONE_COLUMN = "phone"
        private val DATE_COLUMN = "uploadDate"
        private val IMAGE_COLUMN = "imageUrl"


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
            database = databaseHelper?.readableDatabase
            val postCursor = database!!.rawQuery("select * from $FAVS_TABLE inner join $USERS_TABLE on $FAVS_TABLE.$PHONE_COLUMN = $USERS_TABLE.$PHONE_COLUMN", null)
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
            database!!.close()
            return resultList
        }

        fun deleteFavoritePost(post: Post){
            database = databaseHelper?.writableDatabase
            database?.delete(FAVS_TABLE, "id = ?", arrayOf(post.id))
            database?.close()
        }

        fun putFavoritePost(post: Post) : Int{
            var isPut = 1
            database = databaseHelper?.writableDatabase
            var contentValues = ContentValues()
            contentValues.put(NAME_COLUMN, post.ownerName)
            contentValues.put(PHONE_COLUMN, post.ownerPhone)
            val usersUri: Uri = Uri.parse("content://by.katoikido.provider/users")
            if(context!!.contentResolver.insert(usersUri, contentValues) == null){
                database?.update(USERS_TABLE, contentValues, "$PHONE_COLUMN = ?", arrayOf(post.ownerPhone))
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

            val favsUri: Uri = Uri.parse("content://by.katoikido.provider/favs")

            if(context!!.contentResolver.insert(favsUri, contentValues) == null){
                database?.delete(FAVS_TABLE, "id = ?", arrayOf(post.id))
                isPut = 0
            }
            database?.close()
            return isPut
        }
    }
}