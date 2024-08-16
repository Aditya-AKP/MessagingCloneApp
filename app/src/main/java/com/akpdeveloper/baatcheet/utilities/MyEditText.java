package com.akpdeveloper.baatcheet.utilities;

import static com.akpdeveloper.baatcheet.StartActivity.logcat;

import android.content.ClipData;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.ContentInfo;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.os.BuildCompat;
import androidx.core.view.ContentInfoCompat;
import androidx.core.view.OnReceiveContentListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;

public class MyEditText extends AppCompatEditText {

    private String[] type;
    private KeyBoardInputCallbackListener keyBoardInputCallbackListener;

    public MyEditText(@NonNull Context context) {
        super(context);
        init();
    }

    public MyEditText(Context context, AttributeSet attr){
        super(context,attr);
        init();
    }

    public MyEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        logcat("init of MyEditClass");
        type = new String[]{"image/*"};
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            this.setOnReceiveContentListener(type, (android.view.OnReceiveContentListener) this);
//        }
    }

    @Nullable
    @Override
    public InputConnection onCreateInputConnection(@NonNull EditorInfo outAttrs) {
        logcat("enter oncreateInputConnection");
        InputConnection ic = super.onCreateInputConnection(outAttrs);
        if (ic == null) {
            logcat("ic=null");
            return ic;
        }
        EditorInfoCompat.setContentMimeTypes(outAttrs, type);
        String[] mimeTypes = ViewCompat.getOnReceiveContentMimeTypes(this);
        if (mimeTypes != null) {
            EditorInfoCompat.setContentMimeTypes(outAttrs, mimeTypes);
            ic = InputConnectionCompat.createWrapper(this, ic, outAttrs);
            logcat("mimeType!=null");
        }
        logcat("exiting oncreateInputConnection");
        return InputConnectionCompat.createWrapper(ic, outAttrs, callback);
    }



//    @Nullable
//    @Override
//    public ContentInfoCompat onReceiveContent(@NonNull View view, @NonNull ContentInfoCompat payload) {
//        // Split the incoming content into two groups: content URIs and everything else.
//        // This way we can implement custom handling for URIs and delegate the rest.
//        Pair<ContentInfoCompat, ContentInfoCompat> split = payload.partition(item -> item.getUri() != null);
//        ContentInfoCompat uriContent = split.first;
//        ContentInfoCompat remaining = split.second;
//        if (uriContent != null) {
//            ClipData clip = uriContent.getClip();
//            for (int i = 0; i < clip.getItemCount(); i++) {
//                Uri uri = clip.getItemAt(i).getUri();
//                // ... app-specific logic to handle the URI ...
//            }
//        }
//        // Return anything that we didn't handle ourselves. This preserves the default platform
//        // behavior for text and anything else for which we are not implementing custom handling.
//        return remaining;
//    }


    final InputConnectionCompat.OnCommitContentListener callback =
            new InputConnectionCompat.OnCommitContentListener() {
                @Override
                public boolean onCommitContent(@NonNull InputContentInfoCompat inputContentInfo,
                                               int flags, Bundle opts) {

                    // read and display inputContentInfo asynchronously
                    if (BuildCompat.isAtLeastNMR1() && (flags &
                            InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0) {
                        try {
                            inputContentInfo.requestPermission();
                        } catch (Exception e) {
                            logcat("MyEditText Exception: "+e.getMessage());
                            return false; // return false if failed
                        }
                    }
                    boolean supported = false;
                    for (final String mimeType : type) {
                        if (inputContentInfo.getDescription().hasMimeType(mimeType)) {
                            supported = true;
                            break;
                        }
                    }
                    if (!supported) {
                        logcat("!supported");
                        return false;
                    }

                    if (keyBoardInputCallbackListener != null) {
                        keyBoardInputCallbackListener.onCommitContent(inputContentInfo, flags, opts);
                    }
                    return true;  // return true if succeeded
                }
            };



    public interface KeyBoardInputCallbackListener {
        void onCommitContent(InputContentInfoCompat inputContentInfo,
                             int flags, Bundle opts);
    }

    public void setKeyBoardInputCallbackListener(KeyBoardInputCallbackListener keyBoardInputCallbackListener) {
        this.keyBoardInputCallbackListener = keyBoardInputCallbackListener;
    }

    public String[] getImgTypeString() {
        return type;
    }

    public void setImgTypeString(String[] imgTypeString) {
        this.type = imgTypeString;
    }
}
