package com.varshavikshith.utilsfunctions;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.varshavikshith.utilsfunctions.helper.GMail;
import com.varshavikshith.utilsfunctions.helper.Utils;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;


public class SupportEmailForm extends Activity {

    Button sendFabButton;
    EditText edtSubject, edtMessage, edtAttachmentData;

    private final int SELECT_PHOTO = 1;
    public String fileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_email_form);

        sendFabButton = findViewById(R.id.activity_support_email_form_fab);
        edtSubject = findViewById(R.id.activity_support_email_form_subject);
        edtMessage = findViewById(R.id.activity_support_email_form_body);
        edtAttachmentData = findViewById(R.id.activity_support_email_form_attachmentData);

        findViewById(R.id.activity_support_email_form_attachment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.checkPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                } else {
                    ActivityCompat.requestPermissions(SupportEmailForm.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, SELECT_PHOTO);
                }
            }
        });

        sendFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Utils.getString(edtSubject).isEmpty()) {
                    showMessage("Enter Subject");

                } else if (Utils.getString(edtMessage).isEmpty()) {
                    showMessage("Enter Message");

                } else if (Utils.getString(edtAttachmentData).isEmpty()) {
                    showMessage("Attach files");
                } else {
                    String fromEmail = "info@travelize.in";
                    String fromPassword = "lobotus_2017";

                    String toEmails = "";
                    List toEmailList = Arrays.asList(toEmails
                            .split("\\s*,\\s*"));
                    String emailSubject = getIntent().getStringExtra("SUBJECT") + " " + Utils.getString(edtSubject);
                    String emailBody = getIntent().getStringExtra("MESSAGE") + " " + Utils.getString(edtMessage);
                    new SendMailTask(SupportEmailForm.this).execute(fromEmail,
                            fromPassword, toEmailList, emailSubject, emailBody, fileName);
                }
            }
        });

    }

    private void clearFields() {
        edtSubject.setText("");
        edtMessage.setText("");
        edtAttachmentData.setText("");
        fileName = "";
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case SELECT_PHOTO:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    final Uri imageUri = data.getData();
                    fileName = getPathFromURI(imageUri);
                    edtAttachmentData.setText(fileName);
                }
        }
    }

    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, "", null, "");
        assert cursor != null;
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }


    public class SendMailTask extends AsyncTask {

        private ProgressDialog statusDialog;
        private Activity sendMailActivity;

        public SendMailTask(Activity activity) {
            sendMailActivity = activity;

        }

        protected void onPreExecute() {
            statusDialog = new ProgressDialog(sendMailActivity);
            statusDialog.setMessage("Getting ready...");
            statusDialog.setIndeterminate(false);
            statusDialog.setCancelable(false);
            statusDialog.show();
        }

        @Override
        protected Object doInBackground(Object... args) {
            try {
                System.out.println("--------sssssssss---SendMailTask----About to instantiate GMail-----");
                publishProgress("Processing input....");
                GMail androidEmail = new GMail(args[0].toString(),
                        args[1].toString(), (List) args[2], args[3].toString(),
                        args[4].toString(), args[5].toString());
                publishProgress("Preparing mail message....");
                androidEmail.createEmailMessage();
                publishProgress("Sending email....");
                androidEmail.sendEmail();
                publishProgress("Email Sent.");
                System.out.println("--------sssssssss---SendMailTask----Mail Sent.-");
                Log.i("SendMailTask", "Mail Sent.");
                return "Success";
            } catch (Exception e) {
                publishProgress(e.getMessage());
                System.out.println("--------sssssssss---SendMailTask----Exception-" + e.getMessage());
                Log.e("SendMailTask", e.getMessage(), e);
                return null;
            }

        }

        @Override
        public void onPostExecute(Object result) {
            statusDialog.dismiss();
            if (result == null) {
                showMessage("Failed try after some time");
            } else if (result.equals("Success")) {
                showMessage("Successfully send");
                clearFields();
            }
        }

    }

}

