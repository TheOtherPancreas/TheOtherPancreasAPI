package com.theotherpancreas.api;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.theotherpancreas.data.LogEntry;

import java.io.IOException;
import java.lang.reflect.Constructor;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by Mike on 1/24/2018.
 */

public class TopBroadcastReceiver extends BroadcastReceiver {

    private ThirdPartyRoutine routine;

    private synchronized void blessRoutine(Context context) throws AlgorithmLoadException {

        try {
            ActivityInfo ai = context.getPackageManager().getReceiverInfo(new ComponentName(context, TopBroadcastReceiver.class), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            String routineClassName = bundle.getString("com.theotherpancreas.api.ThirdPartyRoutine");
            Class<?> routineClass = Class.forName(routineClassName);
            boolean hasDefaultConstructor = false;
            for (Constructor<?> constructor : routineClass.getConstructors()) {
                if (constructor.getParameterTypes().length == 0) {
                    hasDefaultConstructor = true;
                    break;
                }
            }
            if (!hasDefaultConstructor) {
                throw new AlgorithmLoadException(routineClassName + " does not have a default constructor");
            }

            routine = (ThirdPartyRoutine) routineClass.newInstance();
        }
        catch (Exception e) {
            throw new AlgorithmLoadException(e);
        }
    }

    @Override
    public final void onReceive(Context context, Intent intent) {
        Log.e("THIRD PARTY", "Received broadcast: " + intent.getAction());
        try {
            blessRoutine(context);
            String json = getResultExtras(true).getString("com.theotherpancreas.data.LogEntry");
            LogEntry logEntry = LogEntry.fromJson(json);
            switch(intent.getAction()) {
                case  "com.theotherpancreas.api.ThirdPartyRoutine.onGlucoseReceipt":
                    routine.onGlucoseReceipt(context, logEntry);
                    break;
                case  "com.theotherpancreas.api.ThirdPartyRoutine.alterSettingsBeforeCalculatingDose":
                    SharedPreferences result = routine.alterSettingsBeforeCalculatingDose(context, logEntry, null);
                    //TODO send sharedpreference result
                    break;
                case  "com.theotherpancreas.api.ThirdPartyRoutine.alterLogEntryBeforeCalculatingDose":
                    sendAdjustedTreatment(routine.alterLogEntryBeforeCalculatingDose(context, logEntry));
                    break;
                case  "com.theotherpancreas.api.ThirdPartyRoutine.adjustTreatment":
                    sendAdjustedTreatment(routine.adjustTreatment(context, logEntry));
                    break;
                case  "com.theotherpancreas.api.ThirdPartyRoutine.alterLogEntryAfterDoseIsDelivered":
                    sendAdjustedTreatment(routine.alterLogEntryAfterDoseIsDelivered(context, logEntry));
                    break;
                case "com.theotherpancreas.api.ThirdPartyRoutine.onTreatmentFinalized":
                    routine.onTreatmentFinalized(context, logEntry);
                    break;

            }

        }
        catch (IOException e) {
            Log.e("TopApiService", "Unable to parse LogEntry json.", e);
            sendErrorResponse(e.getMessage());
        } catch (AlgorithmLoadException e) {
            Log.e("TopApiService", "Error Loading Algorithm.", e);
            sendErrorResponse(e.getMessage());
        }
    }

    protected final void sendAdjustedTreatment(LogEntry entry) {
        setResultData(entry.toJson());
        setResultCode(RESULT_OK);
    }

    protected final void sendErrorResponse(String message) {
        setResultData(message);
        setResultCode(RESULT_CANCELED);
    }
}
