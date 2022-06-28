package com.example.sls;

import static java.security.AccessController.getContext;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Boolean validated = false;
    private final static String TAG = "countryspinnerexample";
    private JSONArray jsonCountryArray;
    private AutoCompleteTextView countrySpinner;
    private AutoCompleteTextView stateSpinner;
    private AutoCompleteTextView citySpinner;

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



//                validated = validate();
//                if (validated) {
                    StringBuilder mailBody = new StringBuilder();
                    mailBody.append("Name : "+binding.nameInp.getText().toString()+"\n");
                    mailBody.append("Company Name : "+binding.companyName.getText().toString()+"\n");
                    mailBody.append("GST no. : "+binding.gstNo.getText().toString()+"\n");
                    mailBody.append("Address : \n"+binding.address.getText().toString()+"\n");
                    mailBody.append("Mobile No. : "+binding.mobileNo.getText().toString()+"\n");
                    mailBody.append("Email id : "+binding.email.getText().toString()+"\n");
                    if(binding.purpose.getCheckedRadioButtonId() == R.id.Home)
                        mailBody.append("purpose :"+" Home"+"\n");
                    else
                        mailBody.append("purpose :"+" Office"+"\n");

                    if(binding.ott.getCheckedRadioButtonId() == R.id.ottYes)
                        mailBody.append("OTT required : "+"Yes"+"\n");
                    else
                        mailBody.append("OTT required : "+"No"+"\n");

                    Intent email = new Intent(Intent.ACTION_SENDTO);
                    email.setData(Uri.parse("mailto:"));
                    email.putExtra(Intent.EXTRA_EMAIL, new String[]{"vamseevk9390@gmail.com"});
                    email.putExtra(Intent.EXTRA_SUBJECT, "New Smart Link");
                    email.putExtra(Intent.EXTRA_TEXT, mailBody.toString());
                    startActivity(Intent.createChooser(email, "Choose an Email client :"));
                    Toast.makeText(getApplicationContext(), "submitted successfully !", Toast.LENGTH_SHORT).show();
                //}
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
            binding.aadharCard.setError("upload aadhaar card");
            return false;
        }

        if (binding.addressProof.length() == 0) {
            binding.addressProof.setError("upload address proof");
            return false;
        }

        if (binding.drivingLicense.length() == 0) {
            binding.drivingLicense.setError("upload your driving license");
            return false;
        }

        if (binding.selfie.length() == 0) {
            binding.selfie.setError("upload your selfie");
            return false;
        }

        if (binding.signature.length() == 0) {
            binding.signature.setError("upload your Signature");
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

//    private fun selectImage() {
//        val choice = arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
//        val myAlertDialog: AlertDialog.Builder = AlertDialog.Builder(this)
//        myAlertDialog.setTitle("Select Image")
//        myAlertDialog.setItems(choice, DialogInterface.OnClickListener { dialog, item ->
//                when {
//            // Select "Choose from Gallery" to pick image from gallery
//            choice[item] == "Choose from Gallery" -> {
//                val pickFromGallery = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//                pickFromGallery.type = "/image"
//                startActivityForResult(pickFromGallery, 1)
//            }
//            // Select "Take Photo" to take a photo
//            choice[item] == "Take Photo" -> {
//                val cameraPicture = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                startActivityForResult(cameraPicture, 0)
//            }
//            // Select "Cancel" to cancel the task
//            choice[item] == "Cancel" -> {
//                myAlertDialog.dismiss()
//            }
//        }
//        })
//        myAlertDialog.show()
//    }

    void selectImage() {
        String[] choice = new String[]{"Take Photo", "Choose from Gallery", "cancel"};
        final AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        final AlertDialog alertDialog = myAlertDialog.create();
        myAlertDialog.setTitle("Select Image");
        alertDialog.setCanceledOnTouchOutside(true);
        myAlertDialog.setItems(choice, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                if (choice[item] == "Choose from Gallery") {
                    Intent pickFromGallery = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickFromGallery.setType("/image");
                    startActivityForResult(pickFromGallery, 1);
                }
                // Select "Take Photo" to take a photo
                else if (choice[item] == "Take Photo") {
                    Intent cameraPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraPicture, 0);
                }

                else if (choice[item] == "cancel") {
                    alertDialog.dismiss();
                }

            }
        });
        myAlertDialog.show();


    }

    private void selectPdf() {
        Intent pdfIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pdfIntent.setType("application/pdf");
        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(pdfIntent, 12);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode != RESULT_CANCELED) {
//            switch (requestCode) {
//                case 0 :
//                    if (resultCode == RESULT_OK && data != null) {
//                    val imageSelected = data.extras!!["data"] as Bitmap?;
//                    imageView.setImageBitmap(imageSelected);
//                }
//                case 1 :
//                    if (resultCode == RESULT_OK && data != null) {
//                    val imageSelected = data.data
//                    val pathColumn = arrayOf(MediaStore.Images.Media.DATA)
//                    if (imageSelected != null) {
//                        val myCursor = contentResolver.query(
//                                imageSelected,
//                                pathColumn, null, null, null
//                        )
//                        if (myCursor != null) {
//                            myCursor.moveToFirst()
//                            val columnIndex = myCursor.getColumnIndex(pathColumn[0])
//                            val picturePath = myCursor.getString(columnIndex)
//                            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath))
//                            myCursor.close()
//                        }
//                    }
//                }
//            }
//        }
//
//        // For loading PDF
//        switch (requestCode) {
//            case 12 :
//                if (resultCode == RESULT_OK) {
//                pdfUri = data?.data!!
//                        val uri: Uri = data?.data!!
//                        val uriString: String = uri.toString()
//                var pdfName: String? = null
//                if (uriString.startsWith("content://")) {
//                    var myCursor: Cursor? = null
//                    try {
//                        myCursor = applicationContext!!.contentResolver.query(uri, null, null, null, null)
//                        if (myCursor != null && myCursor.moveToFirst()) {
//                            pdfName = myCursor.getString(myCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
//                            pdfTextView.text = pdfName;
//                        }
//                    } finally {
//                        myCursor?.close()
//                    }
//                }
//            }
//        }
    }




}