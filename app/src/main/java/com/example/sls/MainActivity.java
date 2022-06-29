package com.example.sls;

import static java.security.AccessController.getContext;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sls.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    ActivityMainBinding binding;
    Boolean validated = false;
    private final static String TAG = "countryspinnerexample";
    private AutoCompleteTextView location;

    private static final int PICK_FROM_GALLERY = 101;
    private static final int ADDRESS = 102;
    private static final int DRIVING = 103;
    private static final int AADHAAR = 104;
    private static final int SIGNATURE = 105;
    Uri aadhaarURI;
    Uri addressURI;
    Uri drivingURI;
    Uri selfieURI;
    Uri signatureURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        binding.toolbar.setTitle("Smart Link Solutions");
        setContentView(view);


        String[] centers = new String[]{"BARDOLI", "VYARA", "BHOPAL", "RAIPUR", "BHILAI", "BARGAHR",
                "SAMBALPUR", "BHUBANESWAR", "CORPORATE", "TILDA", "INDORE", "MALIBA", "UNITEL"};


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.select_dialog_item, centers);
        binding.center.setAdapter(adapter);


        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validated = validate();
                if (validated) {
                    StringBuilder mailBody = new StringBuilder();
                    mailBody.append("Locaion : " + binding.center.getSelectedItem().toString() + "\n");
                    mailBody.append("Name : " + binding.nameInp.getText().toString() + "\n");
                    mailBody.append("Company Name : " + binding.companyName.getText().toString() + "\n");
                    mailBody.append("GST no. : " + binding.gstNo.getText().toString() + "\n");
                    mailBody.append("Address : \n" + binding.address.getText().toString() + "\n");
                    mailBody.append("Mobile No. : " + binding.mobileNo.getText().toString() + "\n");
                    mailBody.append("Email id : " + binding.email.getText().toString() + "\n");
                    if (binding.purpose.getCheckedRadioButtonId() == R.id.Home)
                        mailBody.append("purpose :" + " Home" + "\n");
                    else
                        mailBody.append("purpose :" + " Office" + "\n");

                    if (binding.ott.getCheckedRadioButtonId() == R.id.ottYes)
                        mailBody.append("OTT required : " + "Yes" + "\n");
                    else
                        mailBody.append("OTT required : " + "No" + "\n");


//                    Uri uri = Uri.parse("mailto:" + "vamseevk9390@gmail.com")
//                            .buildUpon()
//                            .appendQueryParameter("subject", "New Connection Request")
//                            .appendQueryParameter("body", mailBody.toString())
//                            .build();

                    Intent selectorIntent = new Intent(Intent.ACTION_SENDTO);
                    selectorIntent.setData(Uri.parse("mailto:"));
                    final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"vamseevk9390@gmail.com"});
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "New Connection Request");

                    ArrayList<Uri> uris = new ArrayList<Uri>();

                    if (aadhaarURI != null) {
                        uris.add(aadhaarURI);
                    }

                    if (addressURI != null) {
                        uris.add(addressURI);
                    }

                    if (drivingURI != null) {
                        uris.add(drivingURI);
                    }
                    if (selfieURI != null) {
                        uris.add(selfieURI);
                    }

                    if (signatureURI != null) {
                        uris.add(signatureURI);

                        emailIntent.putExtra(Intent.EXTRA_STREAM, signatureURI);
                    }


                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, mailBody.toString());
                    emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    emailIntent.setSelector(selectorIntent);
//                    if (emailIntent.resolveActivity(getPackageManager()) != null) {
//                        startActivity(emailIntent);
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Sorry, We couldn't find any email client apps!", Toast.LENGTH_SHORT).show();
//                    }
                    startActivity(Intent.createChooser(emailIntent, "Sending email..."));
                    clear();
                }
            }
        });

    }


    void clear() {
        binding.center.setSelection(0);
        binding.nameInp.getText().clear();
        binding.companyName.getText().clear();
        binding.gstNo.getText().clear();
        binding.address.getText().clear();
        binding.mobileNo.getText().clear();
        binding.email.getText().clear();
        binding.aadharCard.getText().clear();
        binding.addressProof.getText().clear();
        binding.drivingLicense.getText().clear();
        binding.selfie.getText().clear();
        binding.signature.getText().clear();
        binding.Home.setSelected(true);
        binding.ottYes.setSelected(true);


    }


    boolean validate() {

        if (binding.center.getSelectedItem().toString().length() == 0) {
            Toast.makeText(this, "This field is required", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (binding.nameInp.length() == 0) {
            binding.nameInp.setError("This field is required");
            return false;
        }
        if (binding.companyName.length() == 0) {
            binding.companyName.setError("This field is required");
            return false;
        }

        if (binding.address.length() == 0) {
            binding.address.setError("This field is required");
            return false;
        }

        if (binding.mobileNo.length() == 0) {
            binding.mobileNo.setError("This field is required");
            return false;
        } else if (binding.mobileNo.length() != 10) {
            binding.mobileNo.setError("mobile no. should be 10 digits");
            return false;
        }

        if (binding.email.length() == 0) {
            binding.email.setError("This field is required");
            return false;
        }

        if (!patternMatches(binding.email.getText().toString(), "^(.+)@(\\S+)$")) {
            binding.email.setError("Enter a valid Email id");
            return false;
        }

        if (binding.aadharCard.length() == 0) {
            Toast.makeText(this, "upload aadhaar card", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (binding.addressProof.length() == 0) {
            Toast.makeText(this, "upload address proof", Toast.LENGTH_SHORT).show();
            return false;
        }

//        if (binding.drivingLicense.length() == 0) {
//            Toast.makeText(this, "upload your driving license", Toast.LENGTH_SHORT).show();
//            return false;
//        }

        if (binding.selfie.length() == 0) {
            Toast.makeText(this, "upload your selfie", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (binding.signature.length() == 0) {
            Toast.makeText(this, "upload your Signature", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public static boolean patternMatches(String emailAddress, String regexPattern) {
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }


    void selectImage(int x) {
        Intent pickFromGallery = new Intent();
        pickFromGallery.setType("image/*");
        pickFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(pickFromGallery, "Select Picture"), x);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AADHAAR && resultCode == RESULT_OK) {
            aadhaarURI = data.getData();
            Cursor returnCursor =
                    getContentResolver().query(aadhaarURI, null, null, null, null);

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            binding.aadharCard.setText(returnCursor.getString(nameIndex));
        }

        if (requestCode == ADDRESS && resultCode == RESULT_OK) {
            addressURI = data.getData();
            Cursor returnCursor =
                    getContentResolver().query(addressURI, null, null, null, null);

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            binding.addressProof.setText(returnCursor.getString(nameIndex));
        }

        if (requestCode == DRIVING && resultCode == RESULT_OK) {
            drivingURI = data.getData();
            Cursor returnCursor =
                    getContentResolver().query(drivingURI, null, null, null, null);

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            binding.drivingLicense.setText(returnCursor.getString(nameIndex));
        }

        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
            selfieURI = data.getData();
            Cursor returnCursor =
                    getContentResolver().query(selfieURI, null, null, null, null);

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            binding.selfie.setText(returnCursor.getString(nameIndex));
        }

        if (requestCode == SIGNATURE && resultCode == RESULT_OK) {
            signatureURI = data.getData();
            Cursor returnCursor =
                    getContentResolver().query(signatureURI, null, null, null, null);

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            binding.signature.setText(returnCursor.getString(nameIndex));
        }


    }


    public void uploadAadhar(View view) {
        Intent pdfIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pdfIntent.setType("image/*|application/pdf");
        String[] mimeTypes = new String[]{"image/*", "application/pdf"};
        pdfIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(pdfIntent, AADHAAR);
    }

    public void uploadAddress(View view) {
        Intent pdfIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pdfIntent.setType("image/*|application/pdf");
        String[] mimeTypes = new String[]{"image/*", "application/pdf"};
        pdfIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(pdfIntent, ADDRESS);
    }

    public void uploadDriving(View view) {
        Intent pdfIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pdfIntent.setType("image/*|application/pdf");
        String[] mimeTypes = new String[]{"image/*", "application/pdf"};
        pdfIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(pdfIntent, DRIVING);
    }

    public void uploadSelfie(View view) {
        selectImage(PICK_FROM_GALLERY);
    }

    public void uploadsignature(View view) {
        selectImage(SIGNATURE);
    }
}