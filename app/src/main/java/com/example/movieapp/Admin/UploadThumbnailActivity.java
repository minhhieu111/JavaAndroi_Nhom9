package com.example.movieapp.Admin;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.movieapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class UploadThumbnailActivity extends AppCompatActivity {
    Uri videothumburi;
    String thumbnail_url;
    ImageView thumbnail_image;
    StorageReference mStoragerefthumbnails;
    DatabaseReference referenceVideos;
    TextView textSelected;
    RadioButton radioButtonlatest, radioButtonpopular, radioButtonNotype, radioButtonSlide;
    StorageTask mStorageTask;
    DatabaseReference updatedataref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_thumbnail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textSelected = findViewById(R.id.textNoThumbnailSelected);
        thumbnail_image = findViewById(R.id.imageView);
        radioButtonlatest = findViewById(R.id.radioLatestMovies);
        radioButtonpopular = findViewById(R.id.radioBestPopularMovies);
        radioButtonNotype = findViewById(R.id.radioNotype);
        radioButtonSlide = findViewById(R.id.radioSlideMovies);
        mStoragerefthumbnails = FirebaseStorage.getInstance().getReference().child("VideoThumnails");
        referenceVideos = FirebaseDatabase.getInstance().getReference().child("videos");
        String currentUid = getIntent().getExtras().getString("currentuid");
        updatedataref = FirebaseDatabase.getInstance().getReference("videos").child(currentUid);


        radioButtonNotype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatedataref.child("video_type").setValue("");
                updatedataref.child("video_slide").setValue("");
                Toast.makeText(UploadThumbnailActivity.this, "Selected: No Type", Toast.LENGTH_SHORT).show();
            }
        });

        radioButtonlatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latesMovies = radioButtonlatest.getText().toString();
                updatedataref.child("video_type").setValue(latesMovies);
                updatedataref.child("video_slide").setValue("");
                Toast.makeText(UploadThumbnailActivity.this, "Selected "+ latesMovies, Toast.LENGTH_SHORT).show();
            }
        });

        radioButtonpopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String popularMovies = radioButtonpopular.getText().toString();
                updatedataref.child("video_type").setValue(popularMovies);
                updatedataref.child("video_slide").setValue("");
                Toast.makeText(UploadThumbnailActivity.this, "Selected "+ popularMovies, Toast.LENGTH_SHORT).show();
            }
        });

        radioButtonSlide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String slideMovies = radioButtonSlide.getText().toString();
                updatedataref.child("video_slide").setValue(slideMovies);
                Toast.makeText(UploadThumbnailActivity.this, "Selected "+ slideMovies, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void showimagechooser(View view){
        Intent in = new Intent(Intent.ACTION_GET_CONTENT);
        in.setType("image/*");
        startActivityForResult(in,102);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data); //Gọi phương thức onActivityResult() của lớp cha
        if ((requestCode == 102) && (resultCode == RESULT_OK) && (data.getData()!=null)){
            videothumburi = data.getData();

            try{
                String thumbname = getFileName(videothumburi);
                textSelected.setText(thumbname);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),videothumburi); //lấy một Bitmap từ URI videothumburi, sử dụng ContentResolver để truy cập dữ liệu trên thiết bị
                thumbnail_image.setImageBitmap(bitmap);

            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
    private String getFileName (Uri uri){
        String result = null;
        if(uri.getScheme().equals("content")){
            Cursor cursor = getContentResolver().query(uri,null,null,null,null); //một phương thức của ContentResolver được sử dụng để truy vấn thông tin về một tệp tin dựa trên Uri
            try{
                if(cursor != null && cursor.moveToFirst()){
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME); //getColumnIndex() để lấy chỉ mục của cột "DISPLAY_NAME" trong kết quả truy vấn
                    if (nameIndex != -1) { //cột "DISPLAY_NAME" tồn tại trong kết quả truy vấn
                        result = cursor.getString(nameIndex);
                    }
                }
            }finally {
                cursor.close();
            }
        }
        if (result == null){
            result = uri.getPath(); //uri.getPath() sẽ trả về đường dẫn đầy đủ của tệp tin hoặc thư mục được đại diện bởi uri
            int cut = result.lastIndexOf('/'); //tìm vị trí xuất hiện cuối cùng của dấu / trong result
            if(cut != -1){
                result = result.substring(cut + 1); //ubstring() để lấy phần tên tệp tin (không bao gồm đường dẫn) từ result
                //cut + 1 là vị trí bắt đầu của tên tệp tin, do dấu / đánh dấu sự kết thúc của đường dẫn
            }
        }
        return result;
    }

    private void uploadFiles(){
        if (videothumburi != null){
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("wait uploading thumbnail ...");
            progressDialog.show();
            String video_title = getIntent().getExtras().getString("thumbnailsName");

            final StorageReference sRef = mStoragerefthumbnails.child(video_title+"."+getfileExtension(videothumburi));

            sRef.putFile(videothumburi).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    sRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            thumbnail_url = uri.toString();
                            updatedataref.child("video_thumb").setValue(thumbnail_url);
                            progressDialog.dismiss();
                            Toast.makeText(UploadThumbnailActivity.this, "file uploaded", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadThumbnailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    progressDialog.setMessage("upload "+((int)progress)+"%...");
                }
            });

        }
    }
    public void uploadfiletofirebase(View view){
        if (textSelected.equals("No Thumbnail Selected")){
            Toast.makeText(this, "first select an image", Toast.LENGTH_SHORT).show();
        }else {
            if (mStorageTask != null && mStorageTask.isInProgress()){
                Toast.makeText(this, "upload files already in progress", Toast.LENGTH_SHORT).show();
            }else {
                uploadFiles();
            }
        }
    }
    public String getfileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri)); //lấy phần mở rộng tệp dựa trên kiểu MIME. Nếu kiểu MIME là image/jpeg, nó sẽ trả về jpg.
    }
}