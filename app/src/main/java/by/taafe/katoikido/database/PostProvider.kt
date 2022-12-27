package by.taafe.katoikido.database

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.annotation.Nullable


class PostProvider : ContentProvider() {
    private var helper: DatabaseHelper? = null

    private val FAVS = 1
    //private val FAVS_ID = 2
    private val USERS = 3

    private val sURIMatcher = UriMatcher(UriMatcher.NO_MATCH)

    init{
        sURIMatcher.addURI(AUTHORITY, FAVS_TABLE, FAVS)
        sURIMatcher.addURI(AUTHORITY, USERS_TABLE, USERS)
    }

    companion object {
        val AUTHORITY = "by.katoikido.provider"
        private val FAVS_TABLE = "favs"
        private val USERS_TABLE = "users"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/$FAVS_TABLE")
    }

    override fun onCreate(): Boolean {
        helper = DatabaseHelper(context)
        return false
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        TODO("Not yet implemented")
    }

    override fun getType(uri: Uri): String? {
        TODO("Not yet implemented")
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val uriType = sURIMatcher.match(uri)
        val database = helper!!.writableDatabase
        when(uriType){
            FAVS -> {
                database.insert(FAVS_TABLE, null, values)
            }
            USERS -> {
                database.insert(USERS_TABLE, null, values)
            }
            else -> {
                throw java.lang.IllegalArgumentException("Unknown uri $uri")
            }
        }
        context?.contentResolver?.notifyChange(uri, null)
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val uriType = sURIMatcher.match(uri)
        val database = helper!!.writableDatabase
        var rowsDeleted: Int = when(uriType){
            FAVS -> {
                database.delete(FAVS_TABLE, selection, selectionArgs)
            }
            USERS -> {
                database.delete(USERS_TABLE, selection, selectionArgs)
            }
            else -> {
                throw IllegalArgumentException("unknown uri $uri")
            }
        }
        context?.contentResolver?.notifyChange(uri, null)
        return rowsDeleted
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        val uriType = sURIMatcher.match(uri)
        val database = helper!!.writableDatabase
        val rowsUpdated: Int = when(uriType){
            USERS -> {
                database.update(USERS_TABLE, values, selection, selectionArgs)
            }
            else -> {
                throw IllegalArgumentException("unknown uri $uri")
            }
        }
        context?.contentResolver?.notifyChange(uri, null)
        return rowsUpdated
    }
}