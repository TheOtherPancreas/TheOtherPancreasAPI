package com.theotherpancreas.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.theotherpancreas.data.LogEntry;

public abstract class ThirdPartyRoutineAdapter implements ThirdPartyRoutine {
    @Override
    public void onGlucoseReceipt(Context context, LogEntry logEntry) {

    }

    @Override
    public SharedPreferences alterSettingsBeforeCalculatingDose(Context context, LogEntry logEntry, SharedPreferences originalSettings) {
        return null;
    }

    @Override
    public LogEntry alterLogEntryBeforeCalculatingDose(Context context, LogEntry logEntry) {
        return null;
    }

    @Override
    public LogEntry adjustTreatment(Context context, LogEntry originalEntry) {
        return null;
    }

    @Override
    public LogEntry alterLogEntryAfterDoseIsDelivered(Context context, LogEntry logEntry) {
        return null;
    }

    @Override
    public void onTreatmentFinalized(Context context, LogEntry logEntry) {

    }
}
