package Util.Android

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import kotlin.reflect.KClass

fun RemoteViews.registerOnClick(context : Context, id : Int, action : String) {
    val intent = Intent()
    intent.action = action
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)
    this.setOnClickPendingIntent(id, pendingIntent)
}

inline fun <reified Provider> RemoteViews.update(context : Context) {
    val widget = ComponentName(context, Provider::class.java)
    val manager = AppWidgetManager.getInstance(context)
    manager.updateAppWidget(widget, this)
}