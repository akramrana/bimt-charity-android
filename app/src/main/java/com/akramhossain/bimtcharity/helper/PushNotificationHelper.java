package com.akramhossain.bimtcharity.helper;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.akramhossain.bimtcharity.MainActivity;
import com.akramhossain.bimtcharity.R;

import java.util.Map;

public class PushNotificationHelper {

    private static final String CHANNEL_ID = "general_notifications";

    public static void showNotification(
            Context context,
            String title,
            String body,
            Map<String, String> data
    ) {
        createNotificationChannel(context);

        Intent intent = new Intent(context, MainActivity.class);

        intent.setFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
        );

        if (data != null) {

            String screen = data.get("screen");

            if (screen != null) {
                intent.putExtra("screen", screen);
            }

            // Optional IDs/data
            String id = data.get("id");

            if (id != null) {
                intent.putExtra("id", id);
            }
        }

        int requestCode =
                (int) (System.currentTimeMillis() % Integer.MAX_VALUE);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT |
                        PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(
                        context,
                        CHANNEL_ID
                )
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setStyle(
                                new NotificationCompat.BigTextStyle()
                                        .bigText(body)
                        )
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(
                        Context.NOTIFICATION_SERVICE
                );

        if (manager != null) {

            int notificationId =
                    (int) (System.currentTimeMillis()
                            % Integer.MAX_VALUE);

            manager.notify(
                    notificationId,
                    builder.build()
            );
        }
    }

    private static void createNotificationChannel(
            Context context
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "General notifications",
                            NotificationManager.IMPORTANCE_HIGH
                    );

            channel.setDescription(
                    "General application notifications"
            );

            NotificationManager manager =
                    context.getSystemService(
                            NotificationManager.class
                    );

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}