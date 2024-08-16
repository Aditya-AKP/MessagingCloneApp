package com.akpdeveloper.baatcheet;

import static com.akpdeveloper.baatcheet.StartActivity.logcat;
import static com.akpdeveloper.baatcheet.StartActivity.makeToast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import com.akpdeveloper.baatcheet.databinding.ActivityLoginBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    ActivityResultLauncher<Intent> someActivityResultLauncher;
    ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.LoginMobileNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int i1, int i2) {
                ArrayList<Character> c =new ArrayList<>(10);
                for(int j=0;j<10;j++){
                    c.add((char)(j+48));
                }
                for (int j=0;j<charSequence.length();j++){
                    if(charSequence.charAt(j)==' '){
                        logcat("space not allowed");
                        makeToast(getApplicationContext(),"Space not allowed");
                        return;
                    }else if(!c.contains(charSequence.charAt(j))){
                        makeToast(getApplicationContext(),"only number allowed");
                        return;
                    }
                }
                binding.LoginNextButton.setEnabled(charSequence.length() == 10);
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        someActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        int n = data.getIntExtra("num",10);
                        logcat("The num is "+n);
                    }
                });

        binding.LoginNextButton.setOnClickListener(view -> {
            showConfirmationDialogBox();
        });
    }

    public void showConfirmationDialogBox(){
        String number = "+"+binding.countryCodeHolder.getSelectedCountryCode() + binding.LoginMobileNo.getText().toString();
        logcat("The number is "+number);

        // TODO : sirf 2 baar SMS aaye ek din me, uska logic likho samje future wale Aditya

        MaterialAlertDialogBuilder dialogBox = new MaterialAlertDialogBuilder(this);
        dialogBox.setMessage("Confirm "+number+" number to verify.")
                .setPositiveButton("CONFIRM", (dialogInterface, i) -> {
                    Intent intent = new Intent(this,OTPActivity.class);
                    intent.putExtra(Intent.EXTRA_PHONE_NUMBER,number);
                    someActivityResultLauncher.launch(intent);
                })
                .setNegativeButton("EDIT",null)
                .create()
                .show();
    }
}