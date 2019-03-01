package com.theotherpancreas.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theotherpancreas.data.LogEntry;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


/**
 * Created by Mike on 1/25/2018.
 */

public class TopDataProvider {
    public static List<LogEntry> queryLogEntries(Context context, long start) {
        return queryLogEntries(context, start, 0);
    }

    public static List<LogEntry> queryLogEntries(Context context, long start, long end) {
        return queryLogEntries(context, start, end, 15, TimeUnit.SECONDS);
    }

    public static List<LogEntry> queryLogEntries(Context context, long start, long end, long timeout, TimeUnit timeoutUnit) {
        Intent intent = new Intent("com.theotherpancreas.api.QUERY");
        intent.putExtra("start", start);
        if (end >= start) {
            intent.putExtra("end", end);
        }
        intent.setClassName("com.theotherpancreas", "com.theotherpancreas.api.LogEntryDataProvider");
        final CountDownLatch latch = new CountDownLatch(1);
        final String[] json = {null};

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                json[0] = getResultData();
                latch.countDown();
            }
        };


        
        context.sendOrderedBroadcast(intent, null, receiver, null, 0, null, null);

        try {
            latch.await(timeout, timeoutUnit);
        } catch (InterruptedException e) {
            Log.e(TopDataProvider.class.getName(), "Log entry query timed out after " + timeout + " " + timeoutUnit.name(), e);
        }


        try {
            ObjectMapper mapper = new ObjectMapper();
            List<LogEntry> result = mapper.readValue(json[0], new TypeReference<LinkedList<LogEntry>>(){});
            return result;
        } catch (IOException e) {
            Log.e(TopDataProvider.class.getName(), "Failed to read returned log entries", e);
        }
        return new LinkedList<>();
    }
}
