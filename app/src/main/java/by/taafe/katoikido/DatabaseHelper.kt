//package by.taafe.katoikido
//
//import android.content.ContentValues
//import android.content.Context
//import android.database.Cursor
//import android.database.sqlite.SQLiteDatabase
//import android.database.sqlite.SQLiteOpenHelper
//
//
//class DatabaseHelper(context: Context?) :
//    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
//    override fun onCreate(db: SQLiteDatabase) {
//        val groupQuery = ("CREATE TABLE " + GROUP_TABLE + "("
//                + IDGROUP_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT, "
//                + IDFACULTY_COLUMN + " INTEGER,"
//                + COURSE_COLUMN + " TEXT,"
//                + NAME_COLUMN + " TEXT,"
//                + HEAD_COLUMN + " INTEGER,"
//                + "FOREIGN KEY (" + IDFACULTY_COLUMN + ") REFERENCES " + FACULTY_TABLE + "(" + IDFACULTY_COLUMN + "))")
//        db.execSQL(groupQuery)
//
////        //examdate and sort
////        val indx_Progresses_ExamDate =
////            "CREATE INDEX IF NOT EXISTS indx_Progress_ExamDate ON PROGRESS (EXAMDATE)"
////        val indx_Progress_mark =
////            "CREATE INDEX IF NOT EXISTS indx_Progress_mark ON PROGRESS (MARK DESC)"
////
////        //foreign keys
////        val indx_Student_idgroup =
////            "CREATE INDEX IF NOT EXISTS indx_Student_idgroup on STUDENT (IDGROUP)"
////        val indx_Group_idfaculty =
////            "CREATE INDEX IF NOT EXISTS indx_Group_idfaculty on GROUP_ (IDFACULTY)"
////        val indx_Progress_idstudent =
////            "CREATE INDEX IF NOT EXISTS indx_Progress_idstudent on PROGRESS (IDSTUDENT)"
////        val indx_Progress_idsubject =
////            "CREATE INDEX IF NOT EXISTS indx_Progress_idsubject on PROGRESS (IDSUBJECT)"
////        db.execSQL(indx_Student_idgroup)
////        db.execSQL(indx_Progresses_ExamDate)
////        db.execSQL(indx_Progress_mark)
////        db.execSQL(indx_Group_idfaculty)
////        db.execSQL(indx_Progress_idstudent)
////        db.execSQL(indx_Progress_idsubject)
//    }
//
//    fun Initialize(context: Context?) {
//        dh = DatabaseHelper(context)
//        database = dh!!.writableDatabaseN
//        database?.close()
//    }
//
//    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//        db.execSQL("DROP TABLE IF EXISTS " + FACULTY_TABLE)
//        db.execSQL("DROP TABLE IF EXISTS " + GROUP_TABLE)
//        onCreate(db)
//    }
//
//    override fun onConfigure(db: SQLiteDatabase) {
//        db.setForeignKeyConstraintsEnabled(true)
//    }
//
//    companion object {
//        private val DB_NAME = "University.db"
//        private val DB_VERSION = 1
//
//        //Tables
//        private val FACULTY_TABLE = "FACULTY"
//        private val GROUP_TABLE = "GROUP_"
//
//        private val IDFACULTY_COLUMN = "IDFACULTY"
//        private val IDGROUP_COLUMN = "IDGROUP"
//        private val COURSE_COLUMN = "COURSE"
//        private val NAME_COLUMN = "NAME"
//        private val HEAD_COLUMN = "HEAD"
//
//
//        var dh: DatabaseHelper? = null
//        var database: SQLiteDatabase? = null
//    }
//}