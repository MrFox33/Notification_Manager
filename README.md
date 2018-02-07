# Notification Manager

Android application created to manage notifications.


# Description

User can add selected applications installed on the device to the groups and choose for them actions after recieving notification (from any application included in group).

Implemented Actions:

- Delete
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Notification is removed from Notification Bar,
- Display only last notification
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Only last recieved notification is displayed in Notification Bar,

- Delete all notifications
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;All notifications are removed from Notification Bar after recieving any &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;notification from given group,

- Send Message after recieving missed call
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;User can specify numbers to which will be sent text message after recieving &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;missed call. Message is provided by user,
- Apply options in given time interval
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;User can specify time range in which application will perform selected actions for &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;given group of applications,

User can delete created groups or modify them (add/remove applications, change options, change name of group).

# Prerequisites

In order to run application on Android device API level 18 is required (Android 4.3).
Also for the application to work properly, appropriate permissions must be granted (access to contacts, sms, storage).

# Tech

Application uses one external library([GSON library][df1]) to manage saving and reading JSON files.

In order to intercept a notification received by the android system application need to have a specific service running on the system's background. This service is called: NotificationListenerService.

What the service basically does is: It registers itseft to the android system and after that starts to listen to the calls from the system when new notifications are posted or removed, or their ranking changed.

When the NotificationListenerService identifies that a notification has been posted, removed or had its ranking modified then application perform specified actions.

# Common problems and bugs

1) After several instalations of application on the same device Notification Service can be killed and not restored by system due to misinterpreted of listener by Android system. In that case user should change name of implemented notification listener class (+ make changes in manifest).
2) Some applications have 'non-removable' notifications. In that case delete option can not perform action.

[df1]: <https://github.com/google/gson>
