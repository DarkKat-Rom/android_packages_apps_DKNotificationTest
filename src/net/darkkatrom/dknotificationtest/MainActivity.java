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

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.session.MediaSession;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.android.internal.util.darkkat.ThemeColorHelper;
import com.android.internal.util.darkkat.ColorHelper;
import com.android.internal.util.darkkat.ThemeHelper;

import net.darkkatrom.dknotificationtest.utils.PreferenceUtils;
import net.darkkatrom.dknotificationtest.utils.Randomizer;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements  View.OnClickListener,
        CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {

    public static final String DEFAULT_NOTIFICATION_CHANNEL_ID =
            "default_notification_chanel";
    public static final String BIG_TEXT_NOTIFICATION_CHANNEL_ID =
            "big_text_notification_chanel";
    public static final String BIG_PICTURE_NOTIFICATION_CHANNEL_ID =
            "big_picture_notification_chanel";
    public static final String INBOX_NOTIFICATION_CHANNEL_ID =
            "inbox_notification_chanel";
    public static final String MESSAGING_NOTIFICATION_CHANNEL_ID =
            "messaging_notification_chanel";
    public static final String MEDIA_NOTIFICATION_CHANNEL_ID =
            "media_notification_chanel";

    public static final int DEFAULT_NOTIFICATION_COLOR = 0xff757575;

    private PreferenceUtils mUtils;
    private NotificationManager mManager;
    private Randomizer mRandomizer;

    private RadioGroup mTypeButtonsGroup;
    private RadioGroup mActionButtonsGroup;

    private Switch mShowActionButtons;
    private ImageView mFab;

    private GradientDrawable mFabContainerBackground;

    private int mDefaultPrimaryColor = 0;
    private int mThemeResId = 0;
    private int mThemeOverlayAccentResId = 0;
    private boolean mLightStatusBar = false;
    private boolean mLightNavigationBar = false;
    private int mStatusBarColor = 0;
    private int mPrimaryColor = 0;
    private boolean mCustomizeColors = false;
    private boolean mIsBlackoutTheme = false;
    private boolean mIsWhiteoutTheme = false;
    private int mNavigationColor = 0;
    private boolean mColorizeNavigationBar = false;

    private boolean mIsMediaNotification = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        updateTheme();
        super.onCreate(savedInstanceState);

        mUtils = new PreferenceUtils(getApplicationContext());
        mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mRandomizer = new Randomizer(this);

        setContentView(R.layout.activity_main);

        mTypeButtonsGroup = (RadioGroup) findViewById(R.id.type_buttons_group);
        mActionButtonsGroup = (RadioGroup) findViewById(R.id.number_of_actions_buttons_group);

        mShowActionButtons = (Switch) findViewById(R.id.show_action_buttons_switch);
        mFab = (ImageView) findViewById(R.id.floating_action_button);

        mFabContainerBackground = (GradientDrawable) findViewById(R.id.floating_action_button_container)
                .getBackground().mutate();

        mTypeButtonsGroup.setOnCheckedChangeListener(this);

        mShowActionButtons.setOnCheckedChangeListener(this);
        mFab.setOnClickListener(this);

        setNotificationChannels();

        if (savedInstanceState == null) {
            onCheckedChanged(mTypeButtonsGroup, R.id.type_default);
            mShowActionButtons.setChecked(true);
        }
    }

    private void updateTheme() {
        mDefaultPrimaryColor = getColor(com.android.internal.R.color.primary_color_darkkat);
        mThemeResId = ThemeHelper.getDKThemeResId(this);
        mThemeOverlayAccentResId = ThemeColorHelper.getThemeOverlayAccentResId(this);
        mLightStatusBar = ThemeColorHelper.lightStatusBar(this, mDefaultPrimaryColor);
        mLightNavigationBar = ThemeColorHelper.lightNavigationBar(this, mDefaultPrimaryColor);
        mStatusBarColor = ThemeColorHelper.getStatusBarBackgroundColor(this, mDefaultPrimaryColor);
        mPrimaryColor = ThemeColorHelper.getPrimaryColor(this, mDefaultPrimaryColor);
        mCustomizeColors = ThemeColorHelper.customizeColors(this);
        mIsBlackoutTheme = ThemeHelper.isBlackoutTheme(this);
        mIsWhiteoutTheme = ThemeHelper.isWhiteoutTheme(this);
        mNavigationColor = ThemeColorHelper.getNavigationBarBackgroundColor(this, mDefaultPrimaryColor);
        mColorizeNavigationBar = ThemeColorHelper.colorizeNavigationBar(this);

        if (mThemeResId > 0) {
            setTheme(mThemeResId);
        }

        if (mThemeOverlayAccentResId > 0) {
            getTheme().applyStyle(mThemeOverlayAccentResId, true);
        }

        int oldFlags = getWindow().getDecorView().getSystemUiVisibility();
        int newFlags = oldFlags;
        if (!mLightStatusBar) {
            boolean isLightStatusBar = (newFlags & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                    == View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            // Check if light status bar flag was set.
            if (isLightStatusBar) {
                // Remove flag
                newFlags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
        }
        if (!mLightNavigationBar) {
            // Check if light navigation bar flag was set
            boolean isLightNavigationBar = (newFlags & View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
                    == View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            if (isLightNavigationBar) {
                // Remove flag
                newFlags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
        }
        if (oldFlags != newFlags) {
            getWindow().getDecorView().setSystemUiVisibility(newFlags);
        }

        if (mCustomizeColors && !mIsBlackoutTheme && !mIsWhiteoutTheme) {
            getWindow().setStatusBarColor(mStatusBarColor);
            getActionBar().setBackgroundDrawable(new ColorDrawable(mPrimaryColor));
        }
        if (mNavigationColor != 0) {
            getWindow().setNavigationBarColor(mNavigationColor);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        for (int i = 0; i < group.getChildCount(); i++) {
            if (group.getChildAt(i) instanceof LinearLayout) {
                LinearLayout l = (LinearLayout) group.getChildAt(i);
                for (int j = 0; j < l.getChildCount(); j++) {
                    if (l.getChildAt(j) instanceof ToggleButton) {
                        ToggleButton tb = (ToggleButton) l.getChildAt(j);
                        tb.setChecked(tb.getId() == checkedId);
                    }
                }
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.show_action_buttons_switch) {
            for (int i = 0; i < mActionButtonsGroup.getChildCount(); i++) {
                ((RadioButton) mActionButtonsGroup.getChildAt(i)).setEnabled(isChecked);
            }
        }
    }

    public void onToggle(View view) {
        updateTypeState(view);
    }

    private void updateTypeState(View view) {
        mTypeButtonsGroup.check(0);
        mTypeButtonsGroup.check(view.getId());
        int fabIconResId = R.drawable.ic_send;
        mIsMediaNotification = false;
        switch (view.getId()) {
            default:
            case R.id.type_default:
                break;
            case R.id.type_big_text:
                fabIconResId = R.drawable.ic_status_bar_text;
                break;
            case R.id.type_big_picture:
                fabIconResId = R.drawable.ic_status_bar_image;
                break;
            case R.id.type_inbox:
            case R.id.type_messaging:
                fabIconResId = R.drawable.ic_status_bar_message;
                break;
            case R.id.type_media:
                fabIconResId = R.drawable.ic_status_bar_media;
                mIsMediaNotification = true;
                break;
        }

        mFab.setImageResource(fabIconResId);
        updateFab();
    }

    @Override
    public void onClick(View v) {
        Notification notification = buildNotification();
        if (notification != null) {
            sendNotification(notification);
        }
    }

    private Notification buildNotification() {
        Notification.Builder builder = null;
        switch (mTypeButtonsGroup.getCheckedRadioButtonId()) {
            default:
            case R.id.type_default:
                builder = new Notification.Builder(this, DEFAULT_NOTIFICATION_CHANNEL_ID);
                prepareDefaultNotification(builder);
                break;
            case R.id.type_big_text:
                builder = new Notification.Builder(this, BIG_TEXT_NOTIFICATION_CHANNEL_ID);
                prepareBigTextNotification(builder);
                break;
            case R.id.type_big_picture:
                builder = new Notification.Builder(this, BIG_PICTURE_NOTIFICATION_CHANNEL_ID);
                prepareBigPictureNotification(builder);
                break;
            case R.id.type_inbox:
                builder = new Notification.Builder(this, INBOX_NOTIFICATION_CHANNEL_ID);
                prepareInboxNotification(builder);
                break;
            case R.id.type_messaging:
                builder = new Notification.Builder(this, MESSAGING_NOTIFICATION_CHANNEL_ID);
                prepareMessagingNotification(builder);
                break;
            case R.id.type_media:
                builder = new Notification.Builder(this, MEDIA_NOTIFICATION_CHANNEL_ID);
                prepareMediaNotification(builder);
                break;
        }

        builder
            .setShowWhen(true)
            .setWhen(System.currentTimeMillis())
            .setSubText(getResources().getString(R.string.notification_sub_text))
            .setContentTitle(getResources().getString(R.string.notification_title))
            .setContentText(getResources().getString(R.string.notification_content_text));

        if (mTypeButtonsGroup.getCheckedRadioButtonId() != R.id.type_media) {
            setButtons(builder);
        }
        if (mUtils.getShowEmphasizedActions() && !mUtils.getShowTombstoneActions()) {
            PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(), 0);
            builder.setFullScreenIntent(intent, false);
        }
        if (!mUtils.getUseDefaultNotificationColor() || !mIsMediaNotification) {
            builder.setColor(mUtils.getNotificationColor());
        }

        return builder.build();

    }

    public void showSettings(MenuItem item) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    private void setButtons(Notification.Builder builder) {
        int buttons = 0;
        if (mShowActionButtons.isChecked()) {
            switch (mActionButtonsGroup.getCheckedRadioButtonId()) {
                case R.id.action_buttons_1:
                    buttons = 1;
                    break;
                case R.id.action_buttons_2:
                    buttons = 2;
                    break;
                case R.id.action_buttons_3:
                    buttons = 3;
                    break;
            }
        }

        // Add as many buttons as you have to
        PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(), 0);
        boolean showReplyAction = mUtils.getShowReplyAction();
        for (int i = 0; i < buttons; i++) {
            boolean showTombstoneActions = mUtils.getShowTombstoneActions()
                    && !mUtils.getShowEmphasizedActions() && i != 1;
//            if (showReplyAction && i == 0) {
//                builder.addAction(getReplyAction());
//            } else {
                builder.addAction(mRandomizer.getRandomIconId(), getResources().getString(
                        R.string.notification_action_text, (i + 1)), showTombstoneActions ? null : intent);
//            }
        }
    }

    private void prepareDefaultNotification(Notification.Builder builder) {
        builder.setSmallIcon(mRandomizer.getRandomSmallIconId());
    }

    private Notification.Action getReplyAction() {
        Intent resultIntent = new Intent(this, NotificationReplyReceiver.class);
        resultIntent.setAction(NotificationReplyReceiver.ACTION_NOTIFICATION_REPLY);
        resultIntent.putExtra(PreferenceUtils.NOTIFICATION_ID, mUtils.getNotificationId());
        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(getApplicationContext(),
                mUtils.getNotificationId(), resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String replyHint = getResources().getString(R.string.notification_action_reply_hint);
        RemoteInput remoteInput = new RemoteInput.Builder(PreferenceUtils.TEXT_REPLY)
            .setLabel(replyHint)
            .build();

        Notification.Action action = new Notification.Action.Builder(mRandomizer.getRandomIconId(),
                getResources().getString(R.string.notification_action_reply_title), resultPendingIntent)
                .addRemoteInput(remoteInput)
                .build();
        return action;
    }

    private void prepareBigTextNotification(Notification.Builder builder) {
        Notification.BigTextStyle style = new Notification.BigTextStyle()
            .bigText(getResources().getString(R.string.big_text));

        builder.setSmallIcon(R.drawable.ic_status_bar_text)
            .setStyle(style);
	}

    private void prepareBigPictureNotification(Notification.Builder builder) {
        Bitmap large = mRandomizer.getRandomImage();
        Notification.BigPictureStyle style = new Notification.BigPictureStyle()
            .bigPicture(large);

        builder.setSmallIcon(R.drawable.ic_status_bar_image)
            .setStyle(style)
            .setLargeIcon(large);
    }

    private void prepareInboxNotification(Notification.Builder builder) {
        Bitmap large = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Notification.InboxStyle style = new Notification.InboxStyle();
        addInboxLines(style);

        builder.setSmallIcon(R.drawable.ic_status_bar_message)
            .setStyle(style)
            .setLargeIcon(large);
    }

    private void prepareMessagingNotification(Notification.Builder builder) {
        Bitmap large = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Notification.MessagingStyle style = new Notification.MessagingStyle(
                getResources().getString(R.string.app_name))
            .setConversationTitle(getResources().getString(
                    R.string.conversation_title));
        addMessages(style);

        builder.setSmallIcon(R.drawable.ic_status_bar_message)
            .setStyle(style)
            .setLargeIcon(large);
    }

    private void prepareMediaNotification(Notification.Builder builder) {
        MediaSession session = new MediaSession(this, "Test");
        PendingIntent intent = PendingIntent.getActivity(this, 0, new Intent(), 0);
        Notification.MediaStyle style = new Notification.MediaStyle()
                .setShowActionsInCompactView(1, 2, 3)
                .setMediaSession(session.getSessionToken());

        builder.setSmallIcon(R.drawable.ic_status_bar_media)
            .setStyle(style)
            .setLargeIcon(mRandomizer.getRandomImage())
            .addAction(R.drawable.ic_action_fast_rewind,
                    getResources().getString(R.string.fast_rewind_title), intent)
            .addAction(R.drawable.ic_action_skip_previous,
                    getResources().getString(R.string.skip_previous_title), intent)
            .addAction(R.drawable.ic_action_play,
                    getResources().getString(R.string.play_title), intent)
            .addAction(R.drawable.ic_action_skip_next,
                    getResources().getString(R.string.skip_next_title), intent)
            .addAction(R.drawable.ic_action_fast_forward,
                    getResources().getString(R.string.fast_forward_title), intent);
    }

    private void addInboxLines(Notification.InboxStyle style) {
        for (int i = 0; i < 6; i++) {
            style.addLine(getResources().getString(R.string.line_number_text, (i + 1)));
        }
    }

    private void addMessages(Notification.MessagingStyle style) {
        for (int i = 0; i < 6; i++) {
            style.addMessage(getResources().getString(R.string.line_number_text, (i + 1)),
                    System.currentTimeMillis(),getResources().getString(R.string.app_name));
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final View checkedTypeButton = findViewById(savedInstanceState.getInt(
                PreferenceUtils.TYPE_BUTTONS_CHECKED_ID, R.id.type_default));
        final boolean showActionButtons = savedInstanceState.getBoolean(
                PreferenceUtils.SHOW_ACTION_BUTTONS, false);

        if (checkedTypeButton == null) {
            updateTypeState(findViewById(R.id.type_default));
        } else {
            updateTypeState(checkedTypeButton);
        }
        if (!showActionButtons) {
            onCheckedChanged(mShowActionButtons, false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        int themeOverlayAccentResId = ThemeColorHelper.getThemeOverlayAccentResId(this);
        boolean lightStatusBar = ThemeColorHelper.lightStatusBar(this, mDefaultPrimaryColor);
        boolean lightNavigationBar = ThemeColorHelper.lightNavigationBar(this, mDefaultPrimaryColor);
        int primaryColor = ThemeColorHelper.getPrimaryColor(this, mDefaultPrimaryColor);
        boolean customizeColors = ThemeColorHelper.customizeColors(this);
        boolean colorizeNavigationBar = ThemeColorHelper.colorizeNavigationBar(this);

        if (mThemeOverlayAccentResId != themeOverlayAccentResId
                || mLightStatusBar != lightStatusBar
                || mLightNavigationBar != lightNavigationBar
                || mPrimaryColor != primaryColor
                || mCustomizeColors != customizeColors
                || mColorizeNavigationBar != colorizeNavigationBar) {
            recreate();
        } else {
            updateFab();
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(PreferenceUtils.TYPE_BUTTONS_CHECKED_ID,
                mTypeButtonsGroup.getCheckedRadioButtonId());
        outState.putBoolean(PreferenceUtils.SHOW_ACTION_BUTTONS, mShowActionButtons.isChecked());
        super.onSaveInstanceState(outState);
    }

    public void sendNotification(Notification notif) {
        int notificationId = mUtils.getNotificationId();
        mManager.notify(notificationId, notif);
        int nextNotificationId = notificationId == 100 ? 1 : notificationId + 1;
        mUtils.setNotificationId(nextNotificationId);
    }

    public void setNotificationChannels() {
        List<NotificationChannel> channels = new ArrayList<NotificationChannel>();

        NotificationChannel channelDefault = getNotificationChannel(DEFAULT_NOTIFICATION_CHANNEL_ID);
        NotificationChannel channelBigText = getNotificationChannel(BIG_TEXT_NOTIFICATION_CHANNEL_ID);
        NotificationChannel channelBigPicture = getNotificationChannel(BIG_PICTURE_NOTIFICATION_CHANNEL_ID);
        NotificationChannel channelInbox = getNotificationChannel(INBOX_NOTIFICATION_CHANNEL_ID);
        NotificationChannel channelMessaging = getNotificationChannel(MESSAGING_NOTIFICATION_CHANNEL_ID);
        NotificationChannel channelMedia = getNotificationChannel(MEDIA_NOTIFICATION_CHANNEL_ID);

        channels.add(channelDefault);
        channels.add(channelBigText);
        channels.add(channelBigPicture);
        channels.add(channelInbox);
        channels.add(channelMessaging);
        channels.add(channelMedia);

        for (int i = 0; i < channels.size(); i++) {
            NotificationChannel channel = (NotificationChannel) channels.get(i);
            channel.setSound(null, null);
            channel.enableLights(false);
            channel.enableVibration(false);
        }

        mManager.createNotificationChannels(channels);
    }

    private NotificationChannel getNotificationChannel(String id) {
        int nameResId = R.string.default_title;
        int descriptionResId = R.string.default_notification_chanel_description;

        switch (id) {
            case BIG_TEXT_NOTIFICATION_CHANNEL_ID:
                nameResId = R.string.big_text_title;
                descriptionResId = R.string.big_text_notification_chanel_description;
                break;
            case BIG_PICTURE_NOTIFICATION_CHANNEL_ID:
                nameResId = R.string.big_picture_title;
                descriptionResId = R.string.big_picture_notification_chanel_description;
                break;
            case INBOX_NOTIFICATION_CHANNEL_ID:
                nameResId = R.string.inbox_title;
                descriptionResId = R.string.inbox_notification_chanel_description;
                break;
            case MESSAGING_NOTIFICATION_CHANNEL_ID:
                nameResId = R.string.messaging_title;
                descriptionResId = R.string.messaging_notification_chanel_description;
                break;
            case MEDIA_NOTIFICATION_CHANNEL_ID:
                nameResId = R.string.media_title;
                descriptionResId = R.string.media_notification_chanel_description;
                break;
            default:
                break;
        }

        CharSequence name = getString(nameResId);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        String description = getString(descriptionResId);

        NotificationChannel channel = new NotificationChannel(id, name, importance);
        channel.setDescription(description);

        return channel;
    }

    private void updateFab() {
        mFabContainerBackground.setColor(ColorStateList.valueOf(
                mUtils.getNotificationColor(mIsMediaNotification)));
        mFab.setImageTintList(ColorStateList.valueOf(getFabIconColor()));
        mFab.setBackgroundTintList(ColorStateList.valueOf(getFabBackgroundColor()));
    }

    private int getFabIconColor() {
        return getResources().getColor(
                ColorHelper.isColorGrayscale(mUtils.getNotificationColor(mIsMediaNotification))
                        && !ColorHelper.isColorDark(mUtils.getNotificationColor(mIsMediaNotification))
                        ? R.color.floating_action_button_icon_color_dark
                        : R.color.floating_action_button_icon_color_light);
    }

    private int getFabBackgroundColor() {
        return getResources().getColor(
                ColorHelper.isColorDark(mUtils.getNotificationColor(mIsMediaNotification))
                        ? R.color.floating_action_button_pressed_color_light
                        : R.color.floating_action_button_pressed_color_dark);
    }
}
