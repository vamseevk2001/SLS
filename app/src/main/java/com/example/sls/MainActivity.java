package com.example.sls;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.sls.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Boolean validated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validated = validate();
                if(validated){
                    Toast.makeText(getApplicationContext(), "submitted successfully !", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    boolean validate(){

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
}