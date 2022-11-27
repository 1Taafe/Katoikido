package by.taafe.katoikido

import android.content.Context
import com.cloudinary.Cloudinary
import com.cloudinary.android.MediaManager

class CloudinaryManager {
    companion object{
        var status = 0
        fun init(context: Context) {
            if(status == 0){
                var config: HashMap<String, String> = HashMap()
                config.put("cloud_name", "dg16mla2j")
                config.put("api_key", "337328279456651")
                config.put("api_secret", "bAMrMpUaC1GobiWbUCE69YYqmqI")
                MediaManager.init(context, config)
                status = 1;
            }
        }
    }
}