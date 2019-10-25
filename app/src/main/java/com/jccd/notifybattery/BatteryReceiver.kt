package com.jccd.notifybattery

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.app.*
import androidx.core.app.NotificationCompat
import android.graphics.Color


class BatteryReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent) {

        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            context?.registerReceiver(null, ifilter)
        }
        val batteryPct: Float? = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level / scale.toFloat()
        }

        if(batteryPct != null){
            if ( batteryPct < 0.5f){
                context?.let { setChannel(it) }
            }
        }
    }

    fun setChannel(ctx: Context){
        val NOTIFICATION_ID = 234

        val notificationManager = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {


            val CHANNEL_ID = "my_channel_01"
            val name = "my_channel"
            val Description = "This is my channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = Description
            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            mChannel.setShowBadge(false)
            notificationManager.createNotificationChannel(mChannel)
        }

        val builder = NotificationCompat.Builder(ctx, "my_channel_01")
            .setSmallIcon((R.drawable.ic_battery_alert_black_24dp))
            .setContentTitle(ctx.getText(R.string.label_low_battery))
            .setContentText(ctx.getText(R.string.text_low_battery))

        val resultIntent = Intent(ctx, MainActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(ctx)
        stackBuilder.addParentStack(MainActivity::class.java)
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        builder.setContentIntent(resultPendingIntent)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}