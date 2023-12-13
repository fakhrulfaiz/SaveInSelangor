package com.example.assignment.reader;

import android.content.Context;

import com.example.assignment.ReportsFragment;
import com.example.assignment.data.ReportData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ReportReader {

    public static List<ReportData> readReportContacts(Context context) {
        List<ReportData> emergencyContacts = new ArrayList<>();

        try {
            String json = loadJSONFromAsset(context, "report_contacts.json");
            if (json != null) {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray emergencyContactsArray = jsonObject.getJSONArray("reportsContacts");

                for (int i = 0; i < emergencyContactsArray.length(); i++) {
                    JSONObject emergencyContactObject = emergencyContactsArray.getJSONObject(i);

                    ReportData emergencyContact = new ReportData();
                    emergencyContact.setCategory(emergencyContactObject.optString("category"));
                    emergencyContact.setOrganization(emergencyContactObject.optString("organization"));
                    emergencyContact.setPhone(emergencyContactObject.optString("phone"));
                    emergencyContact.setEmail(emergencyContactObject.optString("email"));

                    emergencyContacts.add(emergencyContact);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return emergencyContacts;
    }

    private static String loadJSONFromAsset(Context context, String fileName) {
        String json;
        try {
            InputStream is = context.getAssets().open(fileName);

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return json;
    }
}