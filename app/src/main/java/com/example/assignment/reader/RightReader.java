package com.example.assignment.reader;

import android.content.Context;
import android.util.Log;

import com.example.assignment.data.RightContentData;
import com.example.assignment.data.RightData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class RightReader {

    public String TAG = "RightReader";
    public static List<RightData> readRightData(Context context) {
        List<RightData> rightDataList = new ArrayList<>();

        try {
            String json = loadJSONFromAsset(context, "rights_data.json");
            if (json != null) {
                JSONArray rightDataArray = new JSONArray(json);

                for (int i = 0; i < rightDataArray.length(); i++) {
                    JSONObject rightDataObject = rightDataArray.getJSONObject(i);

                    RightData rightData = new RightData();
                    rightData.setTitle(rightDataObject.optString("title"));

                    JSONArray contentArray = rightDataObject.optJSONArray("content");
                    if (contentArray != null) {
                        for (int j = 0; j < contentArray.length(); j++) {
                            JSONObject contentObject = contentArray.getJSONObject(j);

                            RightContentData contentData = new RightContentData();
                            contentData.setContentTitle(contentObject.optString("content_title"));
                            String file = contentObject.optString("content_description");

                            // Process content description
                            if(i == 0){
                                Log.d("RightReader", file);
                                processContentDescription(context, file, contentData);
                            }


                            rightData.addContent(contentData);
                        }
                    }

                    rightDataList.add(rightData);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rightDataList;
    }
    private static void processContentDescription(Context context, String file, RightContentData contentData) {
        try {
            String json = loadJSONFromAsset(context, file);
            if (json != null) {


                JSONArray rightDataArray = new JSONArray(json);

                for (int i = 0; i < rightDataArray.length(); i++) {
                    JSONObject contentObject = rightDataArray.getJSONObject(i);

                    StringBuilder detailsBuilder = new StringBuilder();
                    String p1 = "";
                    detailsBuilder.append("");
                    for (int j = 1; ; j++) {
                        String key = "p" + j;
                        if (contentObject.has(key)) {
                            if (key.equals("p1")) {

                                p1 = contentObject.getString(key);
                                Log.d("RightReader", "p1: " + p1);
                                contentData.addP1(p1);
                            } else {
                                String detail = contentObject.getString(key);
                                detailsBuilder.append(detail).append("\n\n");

                            }
                        } else {
                            break; // Break the loop if the key doesn't exist
                        }
                    }
                    contentData.addContentDescription(detailsBuilder.toString().trim());
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
