package com.akpdeveloper.baatcheet;

import static com.akpdeveloper.baatcheet.StartActivity.logcat;
import static com.akpdeveloper.baatcheet.StartActivity.makeToast;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.akpdeveloper.baatcheet.databinding.ActivityOtpactivityBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    private ActivityOtpactivityBinding binding;
    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private String verificationToken;
    private String phoneNumber;
    private Long timeForResend=60L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        phoneNumber = getIntent().getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        logcat("The otp number is "+phoneNumber);

        binding.OTPTextViewVerifyNumber.setText(getString(R.string.OTP_Activity_verify_number,phoneNumber));
        binding.OTPTextViewShortDescription.setText(getString(R.string.OTP_Activity_enter_otp_to_verify,phoneNumber));

        setConfirmButton();
        setResendButton();

        sendOTP(false);

        Intent test = new Intent();
        test.putExtra("num",1);
        setResult(RESULT_OK,test);
        logcat("Done ");
    }

    private void startTimerForResendButton(){
        binding.OTPResendButton.setEnabled(false);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeForResend--;
                runOnUiThread(()-> binding.OTPResendButton.setText(getString(R.string.OTP_Activity_enter_otp_to_verify,timeForResend.toString())));
                if (timeForResend<=0){
                    timeForResend=60L;
                    timer.cancel();
                    runOnUiThread(() -> {
                        binding.OTPResendButton.setEnabled(true);
                        binding.OTPResendButton.setText(getString(R.string.OTP_Activity_resend_otp));
                    });
                }
            }
        },0,1000);
    }

    private void setResendButton(){
        binding.OTPResendButton.setOnClickListener(view -> sendOTP(true));
    }

    private void setConfirmButton(){
        binding.OTPNextButton.setOnClickListener(view -> {
            String otp = binding.OTPNumber.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationToken,otp);
            signIn(credential);
        });
    }

    private void sendOTP(boolean isResend){
        startTimerForResendButton();
        showInProgress(true);
        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signIn(phoneAuthCredential);
                        showInProgress(false);
                    }
                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        logcat("Firebase Verification Failed: "+e);
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            makeToast(getApplicationContext(),"Invalid Request");
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            makeToast(getApplicationContext(),"Too many Requests");
                        } else if (e instanceof FirebaseAuthMissingActivityForRecaptchaException) {
                            makeToast(getApplicationContext(),"Verification Failed");
                        }else {
                            makeToast(getApplicationContext(), "OTP verification failed");
                        }
                        showInProgress(false);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        resendingToken=forceResendingToken;
                        verificationToken=s;
                        showInProgress(false);
                        logcat("Code Send");
                        makeToast(getApplicationContext(),"OTP send");
                    }
                });
        if (isResend) {
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendingToken).build());
        }else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }
    }

    private void showInProgress(boolean isInProgress){
        if(isInProgress){
            binding.OTPProgressBar.setVisibility(View.VISIBLE);
            binding.OTPNextButton.setVisibility(View.GONE);
        }else{
            binding.OTPProgressBar.setVisibility(View.GONE);
            binding.OTPNextButton.setVisibility(View.VISIBLE);
        }
    }

    private void signIn(PhoneAuthCredential phoneAuthCredential){
        showInProgress(true);
        auth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(task -> {
            showInProgress(false);
            if(task.isSuccessful()){
                Intent intent = new Intent(OTPActivity.this,SetUpActivity.class);
                intent.putExtra(Intent.EXTRA_PHONE_NUMBER,phoneNumber);
                startActivity(intent);
            }else{
                makeToast(getApplicationContext(),"Wrong OTP");
            }
        });
    }
}