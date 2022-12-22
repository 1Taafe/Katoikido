package by.taafe.katoikido.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import by.taafe.katoikido.MessageActivity
import by.taafe.katoikido.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class Noty {

    companion object{
        const val undefined = "Undefined"
        var appContext: Context? = null
        var notyReference : DatabaseReference? = null
        var phoneNumberId : Long = 0
        var notDisplayedPhone = ""

        private val notyListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                val noty = dataSnapshot.getValue<Noty>()
                if(noty != null && notDisplayedPhone != noty.phoneFrom){
                    phoneNumberId = noty.phoneFrom.replace("+", "").toLong()
                    val alarmSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                    val intent = Intent(appContext, MessageActivity::class.java)
                    intent.putExtra("sendTo", noty.sendTo)
                    intent.putExtra("sendToPhone", noty.phoneTo)

                    val pendingIntent = PendingIntent.getActivity(appContext, 0, intent, 0)

                    val builder = NotificationCompat.Builder(appContext!!, "1")
                        .setSmallIcon(R.drawable.ic_baseline_catching_pokemon_24)
                        .setContentTitle(noty.sender)
                        .setContentText(noty.message)
                        .setSubText("Последнее сообщение")
                        .setAutoCancel(true)
                        .setSound(alarmSound)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)

                    val notificationManager = NotificationManagerCompat.from(appContext!!)
                    notificationManager.notify(phoneNumberId.toInt(), builder.build())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message

            }
        }

        fun dispose(){
            notyReference?.removeEventListener(notyListener)
        }

        fun init(phoneNumber: String, context: Context){
            val notyRef = Firebase.database("https://katoikido-default-rtdb.europe-west1.firebasedatabase.app/")
                .getReference("notifications").child(phoneNumber)
            notyReference = notyRef
            appContext = context
            notyReference!!.addValueEventListener(notyListener)
        }
    }

    var id: String = undefined
    var message: String = undefined
    var sender: String = undefined
    var sendTo: String = undefined
    var phoneFrom: String = undefined
    var phoneTo: String = undefined
}