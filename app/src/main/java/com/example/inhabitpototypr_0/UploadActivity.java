package com.example.inhabitpototypr_0;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.BitSet;

public class UploadActivity extends AppCompatActivity {

    private Button uploadBtn;
    private EditText captionEdt;
    private ImageView imageToUploadIv;


    private DatabaseReference mDatabaseReference;

    private static final int OPEN_GALLERY = 123;

    private Uri selectedImgUri;
    private String selectedImgPath;
    private Post post;
    private Bitmap selectedImageBitmap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_post);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        init();
    }

    private void init() {
        uploadBtn = findViewById(R.id.upload_btn);
        captionEdt = findViewById(R.id.caption_edt);
        imageToUploadIv = findViewById(R.id.image_to_upload_iv);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String caption = captionEdt.getText().toString();
                if (!caption.isEmpty()) {
                    savePost(caption);
                } else {
                    Toast.makeText(UploadActivity.this, "please enter some text", Toast.LENGTH_SHORT).show();
                }
            }
        });


        imageToUploadIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(UploadActivity.this, "clicked", Toast.LENGTH_SHORT).show();
                openGallery();
            }
        });
    }


    private void savePost(String caption) {
        Post post2 = new Post();
        post2.setCaption(caption);

        String key = mDatabaseReference.child("post").push().getKey();
        mDatabaseReference.child(key).child("posts").setValue(post2);
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "select picture"), OPEN_GALLERY);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OPEN_GALLERY) {

            if (resultCode == RESULT_OK) {
                selectedImgUri = data.getData();

                try {
                    setImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setImage() throws IOException {
        selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImgUri);
        imageToUploadIv.setImageBitmap(selectedImageBitmap);
    }
}
