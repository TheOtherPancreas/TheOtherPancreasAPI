# TheOtherPancreasAPI
Want to automate you or your patient's treatments in a novel way we haven't thought of? TOP APIs make it trivial to expand or change functionality without having to worry about the nuts and bolts of an artificial pancreas system.

The TOP API provides a simple way to interact with the treatment process. There are 6 touch-points exposed by this API.
1. onGlucoseReceipt
   - This is called when glucose is first read from the CGM
   - `<uses-permission android:name="com.theotherpancreas.api.permission.READ"/>`
2. alterSettingsBeforeCalculatingDose
   - Called before calculating any treatments. This allows the ThirdPartyRoutine to change settings prior to TOP calculating the treatment. These settings will only persist until the current calculations are complete. This is useful for instances where the ThirdPartyRoutine doesn't want to actually calculate treatments, but rather wants to change targets, correction ratios, basals, or other things that could manually be changed by a user in the TOP Settings menu.
   - `<uses-permission android:name="com.theotherpancreas.api.permission.WRITE"/>`
3. alterLogEntryBeforeCalculatingDose
   - Called before calculating any treatments. This allows the ThirdPartyRoutine to change the LogEntry prior to TOP calculating the treatment. These changes will be persisted in place of the original LogEntry. This is useful for such things as implementing your own calibration algorithms, or your own insulin on board calculations. The values of the returned LogEntry will be used when calculating treatments.
   - `<uses-permission android:name="com.theotherpancreas.api.permission.WRITE"/>`
4. adjustTreatment
   - Called after TOP has calculated treatments. This provides an opportunity to change those treatments. This is where you would overwrite any decisions made by TOP. If you were implementing another treatment algorithm like OpenAPS this is where you would do that logic and apply those calculated doses.
   - `<uses-permission android:name="com.theotherpancreas.api.permission.WRITE"/>`
5. alterLogEntryAfterDoseIsDelivered
   - Called after treatment has been injected. This allows the ThirdPartyRoutine to change the logEntry that is stored in the database. These changes will be persisted in place of the original LogEntry. This is useful if you want to roll back any changes made to the LogEntry in the `alterLogEntryBeforeCalculatingDose` method.
   - `<uses-permission android:name="com.theotherpancreas.api.permission.WRITE"/>`
6. onTreatmentFinalized
   - Called after treatment has been injected. This gives the ThirdPartyRoutine an opportunity to store any data
   - `<uses-permission android:name="com.theotherpancreas.api.permission.READ"/>`
   



## Creating an app to interact with TOP takes 5 simple steps:
### 1. Add TheOtherPancreasAPI dependency to your project
1. Add the [JitPack](https://jitpack.io/) `maven { url 'https://jitpack.io' }` repository to your root build.gradle at the end of repositories:
```gradle
  allprojects {
    repositories {
      ...
      maven { url 'https://jitpack.io' }
    }
  }
```
2. Add the TOP API dependency
```gradle
  dependencies {
    implementation 'com.github.TheOtherPancreas:TheOtherPancreasAPI:-SNAPSHOT'
    implementation 'com.github.TheOtherPancreas:TheOtherPancreasCommon:-SNAPSHOT'
  }
```

### 2. Create a ThirdPartyRoutine class
This class either `implements com.theotherpancreas.api.ThirdPartyRoutine` or `extends com.theotherpancreas.api.ThirdPartyRoutineAdapter`
```java
public class YourSpecialRoutine extends ThridPartyRoutineAdapter {

    /**
    * This will be called every 5 minutes when a new CGM reading is received
    * after TOP has already calculated the dose it plans to deliver. You are
    * free to update the dose, or leave it unchanged. In this example the basal
    * dose (commented out) will be whatever TOP decided and the insulin dose will
    * always be 0.5 units. Assuming the basal, bolus, and carb doses are not above
    * the user defined Max Insulin Dose, the pump will inject whatever doses
    * are specified in the LogEntry returned from this method.
    */
    @Override
    LogEntry adjustTreatment(Context context, LogEntry logEntry) {
      Log.d("GLUCOSE", logEntry.glucose + "mg/Dl");
      Log.d("INSULIN ON BOARD", logEntry.insulinOnBoard + "u");
      logEntry.insulinDose = 0.5f;
      // logEntry.basalDose = 0.1f;
      return logEntry;
    }
}

```


### 3. Register TopBroadcastReceiver as a receiver in your manifest
```xml
<receiver android:name="com.theotherpancreas.api.TopBroadcastReceiver">
    <intent-filter>
        <action android:name="com.theotherpancreas.api.ThirdPartyRoutine.onGlucoseReceipt"/>
        <action android:name="com.theotherpancreas.api.ThirdPartyRoutine.alterSettingsBeforeCalculatingDose"/>
        <action android:name="com.theotherpancreas.api.ThirdPartyRoutine.alterLogEntryBeforeCalculatingDose"/>
        <action android:name="com.theotherpancreas.api.ThirdPartyRoutine.adjustTreatment"/>
        <action android:name="com.theotherpancreas.api.ThirdPartyRoutine.alterLogEntryAfterDoseIsDelivered"/>
        <action android:name="com.theotherpancreas.api.ThirdPartyRoutine.onTreatmentFinalized"/>
    </intent-filter>
    <meta-data android:name="com.theotherpancreas.api.ThirdPartyRoutine"
        android:value="com.example.YourSpecialRoutine"/>
</receiver>
```
In the receiver's intent-filter only include the actions you would like to receive. Then replace `com.example.YourSpecialRoutine` with your class that implements `com.theotherpancreas.api.ThirdPartyRoutine` or extends `com.theotherpancreas.api.ThirdPartyRoutineAdapter`

### 4. Prompt the user to grant your app permissions
From within some `Activity` that the user will launch call one of the following:

If you intend to respond to `onGlucoseReceipt` and `onTreatmentFinalized` call:
```java
TOPTools.requestReadPermission(this);
```

If you intend to respond to `alterSettingsBeforeCalculatingDose`, `alterLogEntryBeforeCalculatingDose`, `adjustTreatment`, or `alterLogEntryAfterDoseIsDelivered` call:
```java
TOPTools.requestWritePermission(this);
```

If you intend to respond to both types call:
```java
TOPTools.requestReadAndWritePermission(this);
```

### 5. Enable Third Party Routines within TOP
And finally from within The Other Pancreas app you need to tell it to allow Third Party Routines

**Settings** > **Third Party Routines** > **Allow Third Party Routines**

