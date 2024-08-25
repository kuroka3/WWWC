package io.github.kuroka3.wwwc

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.RemoteViews
import androidx.core.content.res.ResourcesCompat
import io.github.kuroka3.wwwc.api.WaveplateManager

/**
 * Implementation of App Widget functionality.
 */
class WWWCWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        val action = intent.action ?: ""

        if (action == "refresh") {
            WaveplateManager.init(dataDir = context.dataDir)
            WaveplateManager.load()
            updateWidgets(context)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    private fun updateWidgets(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(ComponentName(context, WWWCWidget::class.java))

        ids.forEach { id -> updateAppWidget(context, manager, id) }
    }

    private fun pendingIntent(
        context: Context,
        action: String
    ): PendingIntent {
        val intent = Intent(context, WWWCWidget::class.java)
        intent.action = action

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
    }

    internal fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        WaveplateManager.load()

        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.w_w_w_c_widget)

        // Drawable을 Bitmap으로 변환
        val drawable = ResourcesCompat.getDrawable(context.resources, R.drawable.background, null)
        drawable?.let {
            // Drawable을 Bitmap으로 변환
            val bitmap = drawableToBitmap(it)

            // Bitmap을 어둡게 변환
            val darkenedBitmap = darkenBitmap(bitmap)

            // 어둡게 변환된 Bitmap을 ImageView에 설정
            views.setImageViewBitmap(R.id.widget_background, darkenedBitmap)
        }
        views.setTextViewText(R.id.widget_plateview, "${WaveplateManager.waveplate}/240")
        views.setTextViewText(R.id.widget_leftwholeview, WaveplateManager.leftWholeChargeTime.toTimeStringhhmm())
        views.setImageViewResource(R.id.plate_img, R.drawable.waveplate)
        views.setOnClickPendingIntent(R.id.widget_refresh, pendingIntent(context, "refresh"))


        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    // Drawable을 Bitmap으로 변환하는 메서드
    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap: Bitmap
        if (drawable is BitmapDrawable) {
            bitmap = drawable.bitmap
        } else {
            bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        }
        return bitmap
    }

    // Bitmap을 어둡게 만드는 메서드
    private fun darkenBitmap(original: Bitmap): Bitmap {
        val darkBitmap = Bitmap.createBitmap(original.width, original.height, original.config)
        val canvas = Canvas(darkBitmap)
        val paint = Paint()
        paint.colorFilter = PorterDuffColorFilter(Color.argb(100, 0, 0, 0), PorterDuff.Mode.SRC_OVER)
        canvas.drawBitmap(original, 0f, 0f, paint)
        return darkBitmap
    }

    private fun Long.toTimeStringhhmm(): String {
        val totalSeconds = this / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60

        return String.format("%02d시간 %02d분", hours, minutes)
    }
}