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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    ActivityMainBinding binding;
    Boolean validated = false;
    private final static String TAG = "countryspinnerexample";
    private JSONArray jsonCountryArray;
    private AutoCompleteTextView countrySpinner;
    private AutoCompleteTextView stateSpinner;
    private AutoCompleteTextView citySpinner;

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

        countrySpinner = binding.country;
        stateSpinner = binding.state;
        citySpinner = binding.city;
        populateSpinner();


        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validated = validate();
                if (validated) {
                StringBuilder mailBody = new StringBuilder();
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

//                Intent email = new Intent(Intent.ACTION_SENDTO);
//                email.setData(Uri.parse("mailto:"));
//                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"vamseevk9390@gmail.com"});
//                email.putExtra(Intent.EXTRA_SUBJECT, "New Smart Link");
//                email.putExtra(Intent.EXTRA_TEXT, mailBody.toString());
//                if (URI != null) {
//                    email.putExtra(Intent.EXTRA_STREAM, URI);
//                }
//                startActivity(Intent.createChooser(email, "Choose an Email client :"));
//                Toast.makeText(getApplicationContext(), "submitted successfully !", Toast.LENGTH_SHORT).show();
                final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                //emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("plain/text");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"vamseevk9390@gmail.com"});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "New Smart List");

                ArrayList<Uri> uris = new ArrayList<Uri>();
                //convert from paths to Android friendly Parcelable Uri's


                if (aadhaarURI != null) {
                    uris.add(aadhaarURI);
                }

                if (addressURI != null) {
                    uris.add(addressURI);
                    //emailIntent.putExtra(Intent.EXTRA_STREAM, addressURI);
                }

                if (drivingURI != null) {
                    uris.add(drivingURI);
//                    emailIntent.putExtra(Intent.EXTRA_STREAM, drivingURI);
                }
                if (selfieURI != null) {
                    uris.add(selfieURI);
//                    emailIntent.putExtra(Intent.EXTRA_STREAM, selfieURI);
                }

                if (signatureURI != null) {
                    uris.add(signatureURI);

                    emailIntent.putExtra(Intent.EXTRA_STREAM, signatureURI);
                }


                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, mailBody.toString());
                emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                startActivity(Intent.createChooser(emailIntent, "Sending email..."));
                }
            }
        });

    }


    boolean validate() {

        if (binding.nameInp.length() == 0) {
            binding.nameInp.setError("This field is required");
            return false;
        }
        if (binding.companyName.length() == 0) {
            binding.companyName.setError("This field is required");
            return false;
        }
        if (binding.gstNo.length() == 0) {
            binding.gstNo.setError("This field is required");
            return false;
        }
        if (binding.address.length() == 0) {
            binding.address.setError("This field is required");
            return false;
        }

        if (binding.mobileNo.length() == 0) {
            binding.mobileNo.setError("This field is required");
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

        if (binding.drivingLicense.length() == 0) {
            Toast.makeText(this, "upload your driving license", Toast.LENGTH_SHORT).show();
            return false;
        }

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

    private void populateSpinner() {
        try {
            jsonCountryArray = new JSONObject(loadJSONFromAsset()).optJSONArray("country");

            ArrayList<String> countryList = new ArrayList<>();

            for (int i = 0; i < jsonCountryArray.length(); i++) {
                countryList.add(jsonCountryArray.optJSONObject(i).optString("name"));
            }

            ArrayAdapter<String> countryListAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, countryList);

            countrySpinner.setAdapter(countryListAdapter);

            countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ArrayList<String> stateArray = new ArrayList<>();

                    final JSONArray jsonStateArray = jsonCountryArray.optJSONObject(position).optJSONArray("state");

                    for (int i = 0; i < jsonStateArray.length(); i++) {
                        stateArray.add(jsonStateArray.optJSONObject(i).optString("name"));
                    }

                    ArrayAdapter<String> stateListAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, stateArray);

                    stateSpinner.setAdapter(stateListAdapter);

                    stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            final ArrayList<String> cityArray = new ArrayList<>();

                            final JSONArray jsonCityArray = jsonStateArray.optJSONObject(position).optJSONArray("city");

                            for (int i = 0; i < jsonCityArray.length(); i++) {
                                cityArray.add(jsonCityArray.optJSONObject(i).optString("name"));
                            }

                            ArrayAdapter<String> cityListAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, cityArray);

                            citySpinner.setAdapter(cityListAdapter);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "error=" + e.getMessage());
        }
    }

    public String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getAssets().open("country.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    void selectImage(int x) {
//        String[] choice = new String[]{"Take Photo", "Choose from Gallery", "cancel"};
//        final AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
//        final AlertDialog alertDialog = myAlertDialog.create();
//        myAlertDialog.setTitle("Select Image");
//        alertDialog.setCanceledOnTouchOutside(true);
//        myAlertDialog.setItems(choice, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int item) {
//                if (choice[item] == "Choose from Gallery") {
        Intent pickFromGallery = new Intent();
        pickFromGallery.setType("image/*");
        pickFromGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(pickFromGallery, "Select Picture"), x);
//                }
//                // Select "Take Photo" to take a photo
//                else if (choice[item] == "Take Photo") {
//                    Intent cameraPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(cameraPicture, PICK_FROM_GALLERY);
//                }
//
//                else if (choice[item] == "cancel") {
//                    alertDialog.dismiss();
//                }
//
//            }
//        });
//        myAlertDialog.show();


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
        pdfIntent.setType("application/pdf");
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(pdfIntent, AADHAAR);
    }

    public void uploadAddress(View view) {
        Intent pdfIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pdfIntent.setType("application/pdf");
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(pdfIntent, ADDRESS);
    }

    public void uploadDriving(View view) {
        Intent pdfIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pdfIntent.setType("application/pdf");
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