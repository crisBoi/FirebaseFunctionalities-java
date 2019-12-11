package com.example.inhabitpototypr_0;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.BitSet;
import java.util.Random;

public class UploadActivity extends AppCompatActivity {

    private Button uploadBtn;
    private EditText captionEdt;
    private ImageView imageToUploadIv;


    private DatabaseReference mDatabaseReference;
    private FirebaseStorage mStorage;
    private StorageReference storageReference;

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
        mStorage = FirebaseStorage.getInstance();
        storageReference = mStorage.getReference();
        post = new Post();

        init();
    }

    private void init() {
        uploadBtn = findViewById(R.id.upload_btn);
        captionEdt = findViewById(R.id.caption_edt);
        imageToUploadIv = findViewById(R.id.image_to_upload_iv);

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadImage("filename" + new Random().nextInt(), imageToUploadIv);
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
        post.setCaption(caption);

        String key = mDatabaseReference.child("post").push().getKey();
        mDatabaseReference.child(key).child("posts").setValue(post);
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


    private void uploadImage(String fileName, ImageView imageView) {
        final StorageReference ref = storageReference.child("images").child(fileName);

        byte[] images = imageToByte(imageView);

        UploadTask uploadTask = ref.putBytes(images);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadActivity.this, "upload failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(UploadActivity.this, "upload successful", Toast.LENGTH_SHORT).show();


            }
        });


        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isComplete()) {
                    Uri downloadUri = task.getResult();
                    String imgUrl = downloadUri.toString();

                    post.setImgUrl(downloadUri.toString());

                    String caption = captionEdt.getText().toString();
                    if (!caption.isEmpty()) {
                        savePost(caption);
                    } else {
                        Toast.makeText(UploadActivity.this, "please enter some text", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });


    }

    private byte[] imageToByte(ImageView imageView) {
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] data = baos.toByteArray();

        return data;
    }
}
