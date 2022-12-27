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
        val uriType = sURIMatcher.match(uri)
        val database = helper!!.readableDatabase
        val postCursor: Cursor?
        when(uriType){
            FAVS -> {
                postCursor = database!!.rawQuery("select * from ${DatabaseHelper.FAVS_TABLE} inner join ${DatabaseHelper.USERS_TABLE} on ${DatabaseHelper.FAVS_TABLE}.${DatabaseHelper.PHONE_COLUMN} = ${DatabaseHelper.USERS_TABLE}.${DatabaseHelper.PHONE_COLUMN}", null)
            }
            else -> {
                throw IllegalArgumentException("Unknown uri")
            }
        }
        return postCursor
    }

    override fun getType(uri: Uri): String? {
        return when(sURIMatcher.match(uri)){
            FAVS -> {
                FAVS_TABLE
            }
            USERS -> {
                USERS_TABLE
            }
            else -> {
                throw java.lang.IllegalArgumentException("Unknown uri $uri")
            }
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val uriType = sURIMatcher.match(uri)
        val database = helper!!.writableDatabase
        var isInserted = false
        when(uriType){
            FAVS -> {
                if(database.insert(FAVS_TABLE, null, values) != (-1).toLong()){
                    isInserted = true
                }
            }
            USERS -> {
                if(database.insert(USERS_TABLE, null, values) != (-1).toLong()){
                    isInserted = true
                }
            }
            else -> {
                throw java.lang.IllegalArgumentException("Unknown uri $uri")
            }
        }
        context?.contentResolver?.notifyChange(uri, null)
        return if(!isInserted){
            null
        } else{
            uri
        }

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