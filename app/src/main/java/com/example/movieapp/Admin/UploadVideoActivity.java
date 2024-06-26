package com.example.movieapp.Admin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.movieapp.Models.VideoUploadDetails;
import com.example.movieapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;
import java.util.List;

public class UploadVideoActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Uri videoUri; //Uniform Resource Identifier, lưu trữ URI của video mà người dùng đã chọn để tải lên
    TextView text_video_selected;
    String videoCategory;
    String videotitle;
    String currentuid;
    StorageReference mstorageRef; //Biến này lưu trữ tham chiếu đến vị trí trong Firebase Storage nơi video sẽ được tải lên
    StorageTask mUploadsTask;//StorageTask là một lớp trong Firebase SDK nó được sử dụng để thực hiện các tác vụ như tải lên tệp, tải xuống tệp, xóa tệp, di chuyển tệp và cập nhật thông tin về tệp.
    DatabaseReference referenceVidoes; //Biến này lưu trữ tham chiếu đến vị trí trong Firebase Realtime Database nơi thông tin về video sẽ được lưu trữ
    FirebaseDatabase database;
    EditText video_description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_video);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        text_video_selected = findViewById(R.id.textviedeoselected);
        video_description = findViewById(R.id.movies_description);
        referenceVidoes = FirebaseDatabase.getInstance().getReference().child("videos");
        mstorageRef = FirebaseStorage.getInstance().getReference().child("videos");

        Spinner spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<>();
        categories.add("Action");
        categories.add("Adventure");
        categories.add("Sport");
        categories.add("Romantic");
        categories.add("Comedy");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item); //Thiết lập layout được sử dụng khi hiển thị các mục trong danh sách thả xuống của Spinner
        spinner.setAdapter(dataAdapter); //Gán ArrayAdapter cho Spinner, làm cho Spinner hiển thị danh sách các danh mục đã tạo
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        videoCategory = parent.getItemAtPosition(position).toString();
        Toast.makeText(this,"selected "+videoCategory,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void openvideoFiles(View view){
        Intent in = new Intent(Intent.ACTION_GET_CONTENT);
        in.setType("video/*");
        startActivityForResult(in,101);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 101)&& (resultCode == RESULT_OK)&& (data.getData()!=null)){

            videoUri = data.getData();
            String path = null;
            Cursor cursor; //Đối tượng Cursor dùng để truy vấn dữ liệu từ Content Provider của hệ thống Android
            int coloum_index_data;
            String[] projection = {MediaStore.MediaColumns.DATA, MediaStore.Video.Media.BUCKET_DISPLAY_NAME,MediaStore.Video.Media._ID, MediaStore.Video.Thumbnails.DATA};
            final String orderby = MediaStore.Video.Media.DEFAULT_SORT_ORDER;
            cursor = UploadVideoActivity.this.getContentResolver().query(videoUri,projection,null,null,orderby);//Truy vấn này sẽ trả về thông tin về tệp video đó, bao gồm đường dẫn tệp, tên thư mục chứa tệp, ID của tệp và đường dẫn đến hình ảnh thu nhỏ của tệp.
            coloum_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);//Lấy chỉ số của cột DATA trong kết quả truy vấn

            while (cursor.moveToNext()){
                path = cursor.getString( coloum_index_data);//Lấy đường dẫn của tệp video từ cột DATA
                videotitle = FilenameUtils.getBaseName(path);

            }
            text_video_selected.setText(videotitle);
        }
    }
    public void uploadFileToFirebase(View v){
        if(text_video_selected.getText().equals("No Video Selected")){
            Toast.makeText(this,"please selected an video!",Toast.LENGTH_SHORT).show();
        }else{
                if(mUploadsTask != null && mUploadsTask.isInProgress()){
                    Toast.makeText(this,"video uploads is all already in progress...",Toast.LENGTH_SHORT).show();
                }else{
                    uploadFile();
                }
        }
    }

    private void uploadFile() {
        if(videoUri != null){
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("video uploading...");
            progressDialog.show();
            final StorageReference storageReference = mstorageRef.child(videotitle); //tạo một tham chiếu mới tới tệp với tên là videotitle

            mUploadsTask = storageReference.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() { //lấy URL tải xuống của tệp đã tải lên thông qua getDownloadUrl()
                        @Override
                        public void onSuccess(Uri uri) {
                            String video_url = uri.toString(); //Chuyển đổi đối tượng Uri thành chuỗi URL tải xuống
                            VideoUploadDetails videoUploadDetails = new VideoUploadDetails("", "", "", video_url, videotitle,video_description.getText().toString(), videoCategory);
                            String uploadsid = referenceVidoes.push().getKey();
                            referenceVidoes.child(uploadsid).setValue(videoUploadDetails);
                            currentuid = uploadsid;
                            progressDialog.dismiss();//Ẩn ProgressDialog sau khi quá trình tải lên và lưu dữ liệu đã hoàn tất
                            if (currentuid.equals(uploadsid)){
                                startThumbnailsActivity();
                            }
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress = (100.0 * snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                    progressDialog.setMessage("uploaded "+((int) progress)+"%...");
                }
            });
        }else{
            Toast.makeText(this,"No Video Selected To Upload",Toast.LENGTH_SHORT).show();
        }
    }
    public void startThumbnailsActivity(){
        Intent in = new Intent(UploadVideoActivity.this,UploadThumbnailActivity.class);
        in.putExtra("currentuid",currentuid);
        in.putExtra("thumbnailsName",videotitle);
        startActivity(in);
        Toast.makeText(this, "video uploaded successfully upload video thumnail", Toast.LENGTH_LONG).show();
    }
}