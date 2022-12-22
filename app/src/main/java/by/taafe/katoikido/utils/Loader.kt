package by.taafe.katoikido.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import androidx.swiperefreshlayout.widget.CircularProgressDrawable

class Loader {
    companion object{
        fun create(context : Context, _centerRadius : Float, _strokeWidth : Float) : CircularProgressDrawable{
            val circularProgressDrawable = CircularProgressDrawable(context)
            circularProgressDrawable.strokeWidth = _strokeWidth
            circularProgressDrawable.centerRadius = _centerRadius
            circularProgressDrawable.strokeCap = Paint.Cap.ROUND
            circularProgressDrawable.setColorSchemeColors(Color.parseColor("#FF6200EE"), Color.parseColor("#C700FE"))
            circularProgressDrawable.start()
            return circularProgressDrawable
        }
    }
}