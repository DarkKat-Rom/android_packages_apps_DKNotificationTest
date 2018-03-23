/*
 * Copyright (C) 2018 DarkKat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.darkkatrom.dknotificationtest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import net.darkkatrom.dknotificationtest.utils.PreferenceUtils;
import net.darkkatrom.dknotificationtest.utils.Randomizer;

public class NotificationReplyReceiver extends BroadcastReceiver {

    public static final String ACTION_NOTIFICATION_REPLY =
            "net.darkkatrom.dknotificationtest.ACTION_NOTIFICATION_REPLY";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ACTION_NOTIFICATION_REPLY.equals(intent.getAction())) {
            Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
            if (remoteInput != null) {
                CharSequence replyText = remoteInput.getCharSequence(PreferenceUtils.TEXT_REPLY);
                int notificationId = intent.getIntExtra(PreferenceUtils.NOTIFICATION_ID, 0);
                if (replyText != null && notificationId > 0) {
                    Notification notification = buildNotification(context, replyText);
                    if (notification != null) {
                        sendNotification(context, notificationId, notification);
                    }
                }
            }
        }
    }

    private Notification buildNotification(Context context, CharSequence replyText) {
        Notification notif = null;
        Randomizer randomizer = new Randomizer(context);
        Notification.Builder builder = new Notification.Builder(context)
            .setSmallIcon(randomizer.getRandomSmallIconId())
            .setShowWhen(true)
            .setWhen(System.currentTimeMillis())
            .setSubText(context.getResources().getString(R.string.notification_sub_text))
            .setContentTitle(context.getResources().getString(R.string.notification_action_replied_title))
            .setContentText(context.getResources().getString(R.string.notification_action_replied_text,
                    replyText));
        return builder.build();
    }

    public void sendNotification(Context context, int id, Notification notification) {
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(id, notification);
    }
}
