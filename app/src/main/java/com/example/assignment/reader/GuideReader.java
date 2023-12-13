package com.example.assignment.reader;

import android.content.Context;

import com.example.assignment.data.GuideData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GuideReader {

    public static List<GuideData> readGuideData(Context context) {
        List<GuideData> guideDataList = new ArrayList<>();

        try {
            String json = loadJSONFromAsset(context, "guidance_data.json");
            if (json != null) {
                JSONArray guideDataArray = new JSONArray(json);

                for (int i = 0; i < guideDataArray.length(); i++) {
                    JSONObject guideDataObject = guideDataArray.getJSONObject(i);

                    GuideData guideData = new GuideData();
                    guideData.setTitle(guideDataObject.optString("title"));
                    String file = guideDataObject.getString("description");
                    getGuideDetails(context, file, guideData);

                    // Extract details starting from p1
                    for (int j = 1; ; j++) {
                        String key = "p" + j;
                        if (guideDataObject.has(key)) {
                            String detail = guideDataObject.getString(key);
                            guideData.addDetail(detail);
                        } else {
                            break; // Break the loop if the key doesn't exist
                        }
                    }

                    guideDataList.add(guideData);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return guideDataList;
    }

    private static void getGuideDetails(Context context, String file, GuideData guideData) {

        try {
            String json = loadJSONFromAsset(context, file);
            if (json != null) {
                JSONArray guideDataArray = new JSONArray(json);

                for (int i = 0; i < guideDataArray.length(); i++) {
                    JSONObject guideDataObject = guideDataArray.getJSONObject(i);

                    StringBuilder detailsBuilder = new StringBuilder();
                    String p1 = "";
                    detailsBuilder.append("");
                    for (int j = 1; ; j++) {
                        String key = "p" + j;
                        if (guideDataObject.has(key)) {
                            if(key.equals("p1")){
                                 p1 = guideDataObject.getString(key);
                                 guideData.addP1(p1);
                            }else{
                                String detail = guideDataObject.getString(key);
                                detailsBuilder.append(detail).append("\n\n");
                            }

                        } else {
                            break; // Break the loop if the key doesn't exist
                        }
                    }

                    guideData.addDetail(detailsBuilder.toString().trim());
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
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

