package com.example.mygrocerystore.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.mygrocerystore.R;
import com.example.mygrocerystore.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    CircleImageView profileImg;
    EditText name, email, number, address;
    Button update;

    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;

    ActivityResultLauncher<String> getContentLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Khởi tạo Firebase storage và authentication
        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Khởi tạo getContentLauncher
        getContentLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        handleSelectedImage(result);
                    }
                });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        profileImg = root.findViewById(R.id.profile_img);
        name = root.findViewById(R.id.profile_name);
        email = root.findViewById(R.id.profile_email);
        number = root.findViewById(R.id.profile_number);
        address = root.findViewById(R.id.profile_address);
        update = root.findViewById(R.id.update);

        // Thiết lập sự kiện click cho ImageView
        profileImg.setOnClickListener(v -> openImagePicker());

        // Thiết lập sự kiện click cho nút Update
        update.setOnClickListener(v -> updateUserProfile());

        // Lấy thông tin người dùng từ Firebase Realtime Database
        database.getReference().child("User").child(FirebaseAuth.getInstance().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        if (userModel != null ) {
                            // Hiển thị thông tin người dùng lên các EditText tương ứng
                            name.setText(userModel.getName());
                            email.setText(userModel.getEmail());
//                            number.setText(userModel.getPhoneNumber());
//                            address.setText(userModel.getAddress());

                            // Load hình ảnh người dùng lên ImageView
                            if(userModel.getProfileImg() != null){
                                Glide.with(requireContext()).load(userModel.getProfileImg()).into(profileImg);
                            }
                            else {
                                Glide.with(requireContext()).load(R.drawable.profile).into(profileImg);
                            }

                        } else {
                            name.setText("Your Name");
                            email.setText("Your Email");
                            number.setText("Your Phone Number");
                            address.setText("Your Address");

                            // Load hình ảnh mặc định lên ImageView
                            Glide.with(requireContext()).load(R.drawable.profile).into(profileImg);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), "Failed to load user data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        return root;
    }

    private void openImagePicker() {
        getContentLauncher.launch("image/*");
    }

    private void handleSelectedImage(Uri profileUri) {
        profileImg.setImageURI(profileUri);

        uploadImageToFirebase(profileUri);
    }

    // Upload hình ảnh lên Firebase Storage
    private void uploadImageToFirebase(Uri profileUri) {
        StorageReference reference = storage.getReference()
                .child("profile_picture")
                .child(FirebaseAuth.getInstance().getUid());

        reference.putFile(profileUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Upload thành công
                    reference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String downloadUrl = uri.toString();
                        // Lưu đường dẫn tải xuống vào Realtime Database
                        database.getReference().child("User").child(FirebaseAuth.getInstance().getUid())
                                .child("profileImg").setValue(downloadUrl)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(requireContext(),"Profile Picture Uploaded",Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(requireContext(),"Failed to upload profile picture",Toast.LENGTH_SHORT).show();
                                    }
                                });
                    });
                })
                .addOnFailureListener(e -> {
                    // Upload thất bại
                    Toast.makeText(requireContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void updateUserProfile() {
        // Thực hiện cập nhật thông tin người dùng
    }
}
