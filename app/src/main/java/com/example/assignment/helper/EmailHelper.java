package com.example.assignment.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class EmailHelper {

    private static final int REQUEST_CODE = 101;


    public static void sendEmail(Context context, String emailAddress, String subject, String body) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + emailAddress));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            // Handle the case where no email app is available
            Toast.makeText(context, "No email app installed", Toast.LENGTH_SHORT).show();
        }
    }


    // New method to open the email client with a specified recipient
    public static void openEmailClient(Context context, String emailAddress) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailAddress});

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(Intent.createChooser(intent, "Choose an email client"));
        }
    }
    private static boolean isEmailPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, android.Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private static void requestEmailPermission(Context context) {
        ActivityCompat.requestPermissions(
                (Activity) context,
                new String[]{android.Manifest.permission.SEND_SMS},
                REQUEST_CODE
        );
    }
}
