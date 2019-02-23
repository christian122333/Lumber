package com.example.android.lumber;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class PostActivity extends AppCompatActivity  {
    Post post = new Post();

    private ArrayAdapter<CharSequence> adapter;
    private Spinner numOfBathrooms;
    private Spinner numOfBedrooms;

    private EditText numOfStories;
    private EditText squareFootage;
    private EditText price;
    private EditText address;

    private Button buttonChoose;
    private Button submit;

    private ImageView imageView;

    private Uri filepath;

    private final int PICK_IMAGE_REQUEST = 70;

    private DatabaseReference databaseRef;
    private StorageReference storageRef;
    AppCompatActivity s = this;
    private View.OnClickListener ClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId())
            {
                case R.id.buttonSubmit :
                    submitPost();
                    break;
                case R.id.buttonChoose :
                    chooseImage();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        databaseRef = FirebaseDatabase.getInstance().getReference("posts");
        storageRef = FirebaseStorage.getInstance().getReference();

        numOfBathrooms = (Spinner)findViewById(R.id.spinnerBathrooms);
        adapter = ArrayAdapter.createFromResource(this, R.array.bathrooms_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numOfBathrooms.setAdapter(adapter);

        numOfBedrooms= (Spinner) findViewById(R.id.spinnerBedrooms);
        adapter = ArrayAdapter.createFromResource(this, R.array.bedrooms_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numOfBedrooms.setAdapter(adapter);

        numOfStories = (EditText) findViewById(R.id.editStories);
        squareFootage = (EditText) findViewById(R.id.editSquareFootage);
        price = (EditText) findViewById(R.id.editPrice);
        address = (EditText) findViewById(R.id.editAddress);

        imageView = (ImageView) findViewById(R.id.imageView);

        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonChoose.setOnClickListener(ClickListener);
        submit = (Button) findViewById(R.id.buttonSubmit);
        submit.setOnClickListener(ClickListener);

    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null)
        {
            filepath = data.getData();
            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void submitPost() {

        post.numOfBathrooms = Integer.parseInt((String) numOfBedrooms.getSelectedItem());
        post.numOfBedrooms = Integer.parseInt((String) numOfBedrooms.getSelectedItem());

        if(isEmpty(numOfStories)) {
            Toast t = Toast.makeText(this, "Number of stories is empty", Toast.LENGTH_SHORT);
            t.show();
            return;
        }
        else
            post.numOfStories = Integer.parseInt(numOfStories.getText().toString());

        if(isEmpty(squareFootage)) {
            Toast t = Toast.makeText(this, "Square Footage is empty", Toast.LENGTH_SHORT);
            t.show();
            return;
        }
        else
            post.squareFootage = Integer.parseInt(squareFootage.getText().toString());

        if(isEmpty(price)) {
            Toast t = Toast.makeText(this, "Price is empty", Toast.LENGTH_SHORT);
            t.show();
            return;
        }
        else
            post.price = Integer.parseInt(price.getText().toString());

        if(isEmpty(address)) {
            Toast t = Toast.makeText(this, "Address is empty", Toast.LENGTH_SHORT);
            t.show();
            return;
        }
        else
            post.address = address.getText().toString();

        if(filepath == null)
        {
            Toast t = Toast.makeText(this, "No image uploaded", Toast.LENGTH_SHORT);
            t.show();
            return;
        }
        else
        {
            final String imageFilePath = "images/" + UUID.randomUUID().toString();
            StorageReference imageStorageRef = storageRef.child(imageFilePath);
            imageStorageRef.putFile(filepath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(PostActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            post.ImagePath = imageFilePath;
                            String postId = databaseRef.push().getKey();
                            databaseRef.child(postId).setValue(post);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PostActivity.this, "Failed" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());

                        }
                    });
        }
    }

    boolean isEmpty(View view)
    {
        if(view instanceof EditText) {
            String string = ((EditText)view).getText().toString().trim();
            return TextUtils.isEmpty(string);
        }
        else if (view instanceof Spinner) {
            return ((Spinner)view).getSelectedItem() == null ? true : false;
        }

        return true;
    }


}
