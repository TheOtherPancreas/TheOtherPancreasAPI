package com.theotherpancreas.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.theotherpancreas.data.LogEntry;

/**
 * Created by Mike on 1/19/2018.
 */

public interface ThirdPartyRoutine {

    /**
     * Called when glucose is first read for the CGM
     * @param context
     * @param logEntry
     */
    void onGlucoseReceipt(Context context, LogEntry logEntry);

    /**
     * Called before calculating any treatments. This allows the ThirdPartyRoutine to change
     * settings prior to TOP calculating the treatment. These settings will only persist until the
     * current calculations are complete. This is useful for instances where the ThirdPartyRoutine
     * doesn't want to actually calculate treatments, but rather wants to change targets,
     * correction ratios, basals, or other things that could manually be changed by a user in the
     * TOP Settings menu
     * @param context
     * @param logEntry
     * @param originalSettings
     * @return
     */
    SharedPreferences alterSettingsBeforeCalculatingDose(Context context, LogEntry logEntry, SharedPreferences originalSettings);

    /**
     * Called before calculating any treatments. This allows the ThirdPartyRoutine to change the
     * logEntry prior to TOP calculating the treatment. These changes will be persisted in place
     * of the original LogEntry. This is useful for such things as implementing your own calibration
     * algorithms, or your own insulin on board calculations. The values of the returned LogEntry
     * will be used when calculating treatments.
     * @param context
     * @param logEntry
     * @return
     */
    LogEntry alterLogEntryBeforeCalculatingDose(Context context, LogEntry logEntry);


    /**
     * Called after TOP has calculated treatments. This provides an opportunity to change those
     * treatments. This is where you would overwrite any decisions made by TOP. If you were
     * implementing another treatment algorithm like OpenAPS this is where you would do that logic
     * and apply those calculated doses.
     * @param context
     * @param originalEntry
     * @return
     */
    LogEntry adjustTreatment(Context context, LogEntry originalEntry);

    /**
     * Called after treatment has been injected. This allows the ThirdPartyRoutine to change the
     * logEntry that is stored in the database. These changes will be persisted in place
     * of the original LogEntry. This is useful if you want to roll back any changes made to the
     * LogEntry in the {@link #alterLogEntryBeforeCalculatingDose(Context, LogEntry) alterLogEntryBeforeCalculatingDose}
     * method.
     * @param context
     * @param logEntry
     * @return
     */
    LogEntry alterLogEntryAfterDoseIsDelivered(Context context, LogEntry logEntry);

    /**
     * Called after treatment has been injected. This gives the ThirdPartyRoutine an opportunity
     * to store any data
     * @param context
     * @param logEntry
     * @return
     */
    void onTreatmentFinalized(Context context, LogEntry logEntry);


}
