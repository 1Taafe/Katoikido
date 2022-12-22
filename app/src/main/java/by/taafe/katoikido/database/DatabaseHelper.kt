package by.taafe.katoikido

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
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


        var dh: DatabaseHelper? = null
        var database: SQLiteDatabase? = null

        fun init(context: Context?) {
            dh = DatabaseHelper(context)
            database = dh!!.writableDatabase
            database?.close()
        }
    }
}