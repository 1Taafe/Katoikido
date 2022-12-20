package by.taafe.katoikido

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class Noty {

    companion object{
        const val undefined = "Undefined"
        val appContext: Context? = null

        fun init(phoneNumber: String, context: Context){
            val notyRef = Firebase.database("https://katoikido-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("notifications").child(phoneNumber)
            val notyListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get Post object and use the values to update the UI
                    val noty = dataSnapshot.getValue<Noty>()
                    if(noty != null){
                        Toast.makeText(context, noty.message, Toast.LENGTH_LONG).show()

                        val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                        val builder = NotificationCompat.Builder(context, "1")
                            .setSmallIcon(R.drawable.ic_baseline_catching_pokemon_24)
                            .setContentTitle(noty.sender)
                            .setContentText(noty.message)
                            .setAutoCancel(true)
                            .setSound(alarmSound)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)

                        val notificationManager = NotificationManagerCompat.from(context)
                        notificationManager.notify(1, builder.build())

                        notyRef.setValue(null)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message

                }
            }
            notyRef.addValueEventListener(notyListener)
        }
    }

    var id: String = undefined
    var message: String = undefined
    var sender: String = undefined
    var phoneFrom: String = undefined
    var phoneTo: String = undefined
}