package by.taafe.katoikido

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class ListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
    }

    fun OpenAddActivity(view: View) {
        val addPostIntent = Intent(this, AddPostActivity::class.java)
        addPostIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(addPostIntent)
    }

}