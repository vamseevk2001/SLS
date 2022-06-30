package com.example.sls;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.sls.databinding.ActivityMainBinding;
import com.github.gcacace.signaturepad.views.SignaturePad;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    ActivityMainBinding binding;
    Boolean validated = false;
    private final static String TAG = "countryspinnerexample";
    private AutoCompleteTextView location;
    SignaturePad signaturePad;
    Button clearSign, saveSign;

    private static final int PICK_FROM_GALLERY = 101;
    private static final int ADDRESS = 102;
    private static final int DRIVING = 103;
    private static final int AADHAAR = 104;
    private static final int SIGNATURE = 105;

    private static final int GALLERY_AADHAAR = 107;
    private static final int GALLERY_ADDRESS = 108;
    private static final int GALLERY_GST = 109;
    private static final int GALLERY_SELFIE= 110;
    //private static final int GALLERY_SIGNATURE = 111;

    private static final int TAKE_PHOTO_AADHAAR = 112;
    private static final int TAKE_PHOTO_ADDRESS = 113;
    private static final int TAKE_PHOTO_GST = 114;
    private static final int TAKE_PHOTO_SELFIE = 115;
    //private static final int TAKE_PHOTO_SIGNATURE = 116;
    private static final int REQUEST_EXTERNAL_STORAGE_RESULT = 1;
    Uri aadhaarURI;
    Uri addressURI;
    Uri drivingURI;
    Uri selfieURI;
    Uri signatureURI;
    String signature;
    ArrayList<Uri> uris = new ArrayList<Uri>();
    File pictureDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SLS_documents");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        binding.toolbar.setTitle("Smart Link Solutions pvt ltd");
        setContentView(view);
        if (!pictureDir.exists()) {
            pictureDir.mkdirs();
        }


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
                    mailBody.append("Location : " + binding.center.getSelectedItem().toString() + "\n\n");
                    mailBody.append("Name : " + binding.nameInp.getText().toString() + "\n\n");
                    mailBody.append("Company Name : " + binding.companyName.getText().toString() + "\n\n");
                    mailBody.append("GST no. : " + binding.gstNo.getText().toString() + "\n\n");
                    mailBody.append("Address : \n" + binding.address.getText().toString() + "\n\n");
                    mailBody.append("Mobile No. : " + binding.mobileNo.getText().toString() + "\n\n");
                    mailBody.append("Email id : " + binding.email.getText().toString() + "\n\n");
                    if (binding.purpose.getCheckedRadioButtonId() == R.id.Home)
                        mailBody.append("purpose :" + " Home" + "\n\n");
                    else
                        mailBody.append("purpose :" + " Office" + "\n\n");

                    if (binding.ott.getCheckedRadioButtonId() == R.id.ottYes)
                        mailBody.append("OTT required : " + "Yes" + "\n\n");
                    else
                        mailBody.append("OTT required : " + "No" + "\n\n");


//                    Uri uri = Uri.parse("mailto:" + "vamseevk9390@gmail.com")
//                            .buildUpon()
//                            .appendQueryParameter("subject", "New Connection Request")
//                            .appendQueryParameter("body", mailBody.toString())
//                            .build();

                    Intent selectorIntent = new Intent(Intent.ACTION_SENDTO);
                    selectorIntent.setData(Uri.parse("mailto:"));
                    final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"vamseevk9390@gmail.com"});
                    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "New Connection Request");


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

                        //emailIntent.putExtra(Intent.EXTRA_STREAM, signatureURI);
                    }
                    Intent chooser = Intent.createChooser(emailIntent, "Sending email...");
                    List<ResolveInfo> resInfoList = getApplicationContext().getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);

//                for(Uri uri:uris) {
//
//                    for (ResolveInfo resolveInfo : resInfoList) {
//                        String packageName = resolveInfo.activityInfo.packageName;
//                        getApplicationContext().grantUriPermission(packageName, aadhaarURI, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                    }
//                }
                    chooser.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, mailBody.toString());
                    emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    emailIntent.setSelector(selectorIntent);


//                    if (emailIntent.resolveActivity(getPackageManager()) != null) {
//                        startActivity(emailIntent);
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Sorry, We couldn't find any email client apps!", Toast.LENGTH_SHORT).show();
//                    }
                    startActivity(chooser);
                    clear();

                }

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults) {

        if (requestCode == REQUEST_EXTERNAL_STORAGE_RESULT) {
            if (grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {
                //callCameraApp();
            } else {
                Toast.makeText(
                        this, "External write permission"
                                + " has not been granted, "
                                + " cannot saved images",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            super.onRequestPermissionsResult(
                    requestCode,
                    permissions,
                    grantResults);
        }
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
//        binding.Home.setSelected(true);
//        binding.ottYes.setSelected(true);
        uris.clear();
        aadhaarURI = null;
        addressURI = null;
        drivingURI = null;
        selfieURI = null;
        signatureURI = null;
        binding.purpose.clearCheck();
        binding.ott.clearCheck();


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

    void selectFile(int gallery, int camera, String fileName) {
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
                    //pickFromGallery.setType("/image");
                    pickFromGallery.setType("image/*|application/pdf");
                    String[] mimeTypes = new String[]{"image/*", "application/pdf"};
                    pickFromGallery.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                    startActivityForResult(pickFromGallery, gallery);

                }
                // Select "Take Photo" to take a photo
                else if (choice[item] == "Take Photo") {

//                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                            == PackageManager.PERMISSION_GRANTED) {
//
//                        Intent cameraPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        startActivityForResult(cameraPicture, TAKE_PHOTO);
//                    } else {
//                        if (ActivityCompat.shouldShowRequestPermissionRationale(getParent(), Manifest
//                                                        .permission
//                                                        .WRITE_EXTERNAL_STORAGE)) {
//                            Toast.makeText(getApplicationContext(), "External storage permission"
//                                            + " required to save images", Toast.LENGTH_SHORT).show();
//                        }
//
//                        ActivityCompat.requestPermissions(getParent(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                                REQUEST_EXTERNAL_STORAGE_RESULT);
//                    }

//                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//                    StrictMode.setVmPolicy(builder.build());
                    Intent cameraPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File image = new File(pictureDir, fileName);
                    cameraPicture.putExtra(MediaStore.EXTRA_OUTPUT,FileProvider.getUriForFile(getApplicationContext(), BuildConfig.APPLICATION_ID + ".provider",
                            image));
                    startActivityForResult(cameraPicture, camera);
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    File image = new File(pictureDir, "Aadhaar.jpg");
//                    aadhaarURI = Uri.fromFile(image);
//                    intent.putExtra(MediaStore.EXTRA_OUTPUT, aadhaarURI);
//                    startActivityForResult(intent, TAKE_PHOTO);
                } else if (choice[item] == "cancel") {
                    alertDialog.dismiss();
                }

            }
        });
        myAlertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_AADHAAR && resultCode == RESULT_OK) {
            aadhaarURI = data.getData();
            Cursor returnCursor =
                    getContentResolver().query(aadhaarURI, null, null, null, null);

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            binding.aadharCard.setText(returnCursor.getString(nameIndex));
        }




        if (requestCode == GALLERY_ADDRESS && resultCode == RESULT_OK) {
            addressURI = data.getData();
            Cursor returnCursor =
                    getContentResolver().query(addressURI, null, null, null, null);

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            binding.addressProof.setText(returnCursor.getString(nameIndex));
        }

        if (requestCode == GALLERY_GST && resultCode == RESULT_OK) {
            drivingURI = data.getData();
            Cursor returnCursor =
                    getContentResolver().query(drivingURI, null, null, null, null);

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            binding.drivingLicense.setText(returnCursor.getString(nameIndex));
        }

        if (requestCode == GALLERY_SELFIE && resultCode == RESULT_OK) {
            selfieURI = data.getData();
            Cursor returnCursor =
                    getContentResolver().query(selfieURI, null, null, null, null);

            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            returnCursor.moveToFirst();
            binding.selfie.setText(returnCursor.getString(nameIndex));
        }


        if (requestCode == TAKE_PHOTO_AADHAAR && resultCode == RESULT_OK) {
//            aadhaarURI = data.getData();
//            Cursor returnCursor =
//                    getContentResolver().query(aadhaarURI, null, null, null, null);
//
//            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//            returnCursor.moveToFirst();
//            binding.aadharCard.setText(returnCursor.getString(nameIndex));

//            Bitmap photo = (Bitmap) data.getExtras()
//                    .get("data");

            File image = new File(pictureDir, "Aadhaar.jpg");
            //aadhaarURI = Uri.fromFile(image);
            //aadhaarURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", createImageFile());
            aadhaarURI = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", image);
            binding.aadharCard.setText("Aadhaar.jpg");
            // Set the image in imageview for display
        }

        if (requestCode == TAKE_PHOTO_ADDRESS && resultCode == RESULT_OK) {
//            aadhaarURI = data.getData();
//            Cursor returnCursor =
//                    getContentResolver().query(aadhaarURI, null, null, null, null);
//
//            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//            returnCursor.moveToFirst();
//            binding.aadharCard.setText(returnCursor.getString(nameIndex));

//            Bitmap photo = (Bitmap) data.getExtras()
//                    .get("data");

            File image = new File(pictureDir, "Address.jpg");
            //aadhaarURI = Uri.fromFile(image);
            //aadhaarURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", createImageFile());
            addressURI = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", image);
            binding.addressProof.setText("Address.jpg");
            // Set the image in imageview for display
        }

        if (requestCode == TAKE_PHOTO_GST && resultCode == RESULT_OK) {
//            aadhaarURI = data.getData();
//            Cursor returnCursor =
//                    getContentResolver().query(aadhaarURI, null, null, null, null);
//
//            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//            returnCursor.moveToFirst();
//            binding.aadharCard.setText(returnCursor.getString(nameIndex));

//            Bitmap photo = (Bitmap) data.getExtras()
//                    .get("data");

            File image = new File(pictureDir, "gst.jpg");
            //aadhaarURI = Uri.fromFile(image);
            //aadhaarURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", createImageFile());
            drivingURI = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", image);
            binding.drivingLicense.setText("gst.jpg");
            // Set the image in imageview for display
        }

        if (requestCode == TAKE_PHOTO_SELFIE && resultCode == RESULT_OK) {
//            aadhaarURI = data.getData();
//            Cursor returnCursor =
//                    getContentResolver().query(aadhaarURI, null, null, null, null);
//
//            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//            returnCursor.moveToFirst();
//            binding.aadharCard.setText(returnCursor.getString(nameIndex));

//            Bitmap photo = (Bitmap) data.getExtras()
//                    .get("data");

            File image = new File(pictureDir, "selfie.jpg");
            //aadhaarURI = Uri.fromFile(image);
            //aadhaarURI = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", createImageFile());
            selfieURI = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", image);
            binding.selfie.setText("selfie.jpg");
            // Set the image in imageview for display
        }


//        if (requestCode == GALLERY_SELFIE && resultCode == RESULT_OK) {
//            selfieURI = data.getData();
//            Cursor returnCursor =
//                    getContentResolver().query(selfieURI, null, null, null, null);
//
//            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//            returnCursor.moveToFirst();
//            binding.selfie.setText(returnCursor.getString(nameIndex));
//        }

//        if (requestCode == SIGNATURE && resultCode == RESULT_OK) {
//            signatureURI = data.getData();
//            Cursor returnCursor =
//                    getContentResolver().query(signatureURI, null, null, null, null);
//
//            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
//            returnCursor.moveToFirst();
//            binding.signature.setText(returnCursor.getString(nameIndex));
//        }


    }


    public void uploadAadhar(View view) {
//        Intent pdfIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        pdfIntent.setType("image/*|application/pdf");
//        String[] mimeTypes = new String[]{"image/*", "application/pdf"};
//        pdfIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE);
//        startActivityForResult(pdfIntent, AADHAAR);
        selectFile(GALLERY_AADHAAR, TAKE_PHOTO_AADHAAR, "Aadhaar.jpg");
    }

    public void uploadAddress(View view) {
//        Intent pdfIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        pdfIntent.setType("image/*|application/pdf");
//        String[] mimeTypes = new String[]{"image/*", "application/pdf"};
//        pdfIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE);
//        startActivityForResult(pdfIntent, ADDRESS);
        selectFile(GALLERY_ADDRESS, TAKE_PHOTO_ADDRESS, "Address.jpg");
    }

    public void uploadGST(View view) {
//        Intent pdfIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        pdfIntent.setType("image/*|application/pdf");
//        String[] mimeTypes = new String[]{"image/*", "application/pdf"};
//        pdfIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//        pdfIntent.addCategory(Intent.CATEGORY_OPENABLE);
//        startActivityForResult(pdfIntent, DRIVING);
        selectFile(GALLERY_GST, TAKE_PHOTO_GST, "gst.jpg");
    }

    public void uploadSelfie(View view) {
//        selectImage(PICK_FROM_GALLERY);
        selectFile(GALLERY_SELFIE, TAKE_PHOTO_SELFIE, "selfie.jpg");
    }

    public void uploadsignature(View v) {
        //selectImage(SIGNATURE);

//        View newHelp = LayoutInflater.from(this).inflate(R.layout.signature_dialog_box, null);
//        final AlertDialog addNewHelp = new AlertDialog.Builder(getApplicationContext()).create();
//        addNewHelp.setView(newHelp);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        ViewGroup viewGroup = findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.signature_dialog_box, viewGroup, false);
        builder.setView(dialogView);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        signaturePad = dialogView.findViewById(R.id.signaturePad);
        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {

            }

            @Override
            public void onClear() {
                Toast.makeText(getApplicationContext(), "Signature cleared", Toast.LENGTH_SHORT).show();
            }
        });

        clearSign = dialogView.findViewById(R.id.clearSign);
        saveSign = dialogView.findViewById(R.id.saveSign);

        clearSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signaturePad.clear();
            }
        });

        saveSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signature = saveImage(signaturePad.getSignatureBitmap());
                File image = new File(pictureDir, "signature.jpg");
                signatureURI = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", image);
                binding.signature.setText("signature.jpg");
                alertDialog.dismiss();
            }

        });

        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();

    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
//        File wallpaperDirectory = new File(
//                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY /*iDyme folder*/);
        // have the object build the directory structure, if needed.
//        if (!wallpaperDirectory.exists()) {
//            wallpaperDirectory.mkdirs();
//            Log.d("hhhhh",wallpaperDirectory.toString());
//        }

        try {
            File f = new File(pictureDir, "signature.jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(MainActivity.this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Toast.makeText(getApplicationContext(), "Signature saved successfully", Toast.LENGTH_SHORT).show();

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";

    }
}