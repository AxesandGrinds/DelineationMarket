package com.eli.orange.activity.uploadFiles;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.eli.orange.R;
import com.eli.orange.activity.LoginActivity;
import com.eli.orange.models.Upload;
import com.eli.orange.utils.Constants;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import butterknife.BindView;
import butterknife.ButterKnife;


public class ImageUploadActivity extends Activity implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.btnUpload)
    Button uploadImage;
    @BindView(R.id.btn_pick)
    Button pickImage;
    private final int SELECT_PHOTO = 1;
    @BindView(R.id.imageView)
    ImageView imageView;

    @BindView(R.id.imageTitle)
    TextInputEditText itemTitle;
    @BindView(R.id.imagePrice)
    TextInputEditText itemPrice;
    @BindView(R.id.imageDesc)
    TextInputEditText itemDesc;
    private Bitmap selectedImage;
    private String item, category;
    String itemName;
    SharedPreferences prefs;
    //private ProgressBar progressBar;

    private Uri filePath;

    //firebase objects
    private StorageReference storageReference;
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setProgressBarIndeterminateVisibility(true);
        setContentView(R.layout.activity_image_upload);
        ButterKnife.bind(this);

        storageReference = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);
        mFirebaseAuth = FirebaseAuth.getInstance();


        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();


        doUpload();

        imageView.setVisibility(View.GONE);
        uploadImage.setVisibility(View.GONE);

        itemTitle.setVisibility(View.GONE);

        uploadImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(itemTitle.getText())) {
                    itemTitle.setError("Title Required!");

                } else if (TextUtils.isEmpty(itemPrice.getText())) {
                    itemPrice.setError("Price Required!");
                } else if (TextUtils.isEmpty(itemDesc.getText())) {
                    itemDesc.setError("Description Required!");
                } else if (mFirebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(ImageUploadActivity.this, LoginActivity.class));
                    finish();
                } else {
                    uploadImageToStorageUserAccountNode();
                }
            }
        });

        pickImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                doUpload();
            }
        });
    }


    public void doUpload() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK && imageReturnedIntent != null) {
                    try {
                        filePath = imageReturnedIntent.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(filePath);
                        selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                        imageView.setVisibility(View.VISIBLE);
                        uploadImage.setVisibility(View.VISIBLE);
                        imageView.setImageBitmap(selectedImage);
                        String text = "Change Image";
                        itemTitle.setVisibility(View.VISIBLE);
                        pickImage.setText(text);
                        setSpinnerAdapter();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {
                    this.finish();
                }


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onBackPressed() {
        finish();
    }


    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte, Base64.DEFAULT);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
        item = parent.getItemAtPosition(i).toString();
        switch (item) {
            case "Clothes":
                category = "w";
                break;
            case "Shoes":
                category = "v";
                break;
            case "Jewellery":
                category = "j";
                break;
            case "MobilePhones":
                category = "p";
                break;
        }

        // Showing selected spinner item
        if (item.equals("Category")) {
            uploadImage.setEnabled(false);
            Toast.makeText(parent.getContext(), "Select Category", Toast.LENGTH_LONG).show();
            Log.d("IMAGE STRING", imageToString(selectedImage));
        } else {
            uploadImage.setEnabled(true);
            Toast.makeText(parent.getContext(), "You have Selected: " + item, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void setSpinnerAdapter() {
        Spinner spinner = (Spinner) findViewById(R.id.spinnerCategory);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Category");
        categories.add("Clothes");
        categories.add("Shoes");
        categories.add("Jewellery");
        categories.add("MobilePhones");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    void uploadImageToStorageUserAccountNode() {
        //checking if file is available
        if (filePath != null) {
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            String USER_UPLOAD_PATH = mFirebaseAuth.getCurrentUser().getUid() + "/";
            //getting the storage reference
            StorageReference sRef = storageReference.child(Constants.STORAGE_PATH_UPLOADS + USER_UPLOAD_PATH + System.currentTimeMillis() + "." + getFileExtension(filePath));

            UploadTask uploadTask = sRef.putFile(filePath);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    //dismissing the progress dialog
                    progressDialog.dismiss();

                    //displaying success toast
                    Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                    String IMAGE_PATH = "gs://" + sRef.getBucket() + sRef.getPath();
                    Log.d("Uploaded Uri", sRef.getName());


                    //creating the upload object to store uploaded image details
                    Upload upload = new Upload(itemTitle.getText().toString().trim(), itemDesc.getText().toString(), itemPrice.getText().toString(), sRef.getName());

                    //adding an upload to firebase database
                    String uploadId = mDatabase.push().getKey();
                    mDatabase.child(USER_UPLOAD_PATH).child(uploadId).setValue(upload);

                    finish();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        } else {
            //display an error if no file is selected
        }

    }
}


