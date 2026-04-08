package com.example.cameragalleryapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import java.io.OutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // Unique Request Codes
    private static final int CODE_PERM_CAMERA = 201;
    private static final int CODE_IMG_CAPTURE = 202;
    private static final int CODE_DIR_PICKER = 203;

    private Uri targetDirectoryUri = null;
    private GridView galleryDisplayGrid;
    private ImageAdapter galleryAdapter;
    private ArrayList<DocumentFile> mediaContentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components using the updated IDs
        Button selectDirBtn = findViewById(R.id.action_select_directory);
        Button launchCameraBtn = findViewById(R.id.action_capture_image);
        galleryDisplayGrid = findViewById(R.id.asset_grid_viewer);

        mediaContentList = new ArrayList<>();
        galleryAdapter = new ImageAdapter(this, mediaContentList);
        galleryDisplayGrid.setAdapter(galleryAdapter);

        // Logic for Folder Selection (Requirement B)
        selectDirBtn.setOnClickListener(view -> {
            Intent folderIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(folderIntent, CODE_DIR_PICKER);
        });

        // Logic for Camera Launch (Requirement A)
        launchCameraBtn.setOnClickListener(view -> {
            if (targetDirectoryUri == null) {
                Toast.makeText(this, "Set a save folder first!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check Camera Permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CODE_PERM_CAMERA);
            } else {
                triggerCameraCapture();
            }
        });

        // Open Detail View on Item Click (Requirement C)
        galleryDisplayGrid.setOnItemClickListener((adapterView, view, idx, id) -> {
            DocumentFile selectedFile = mediaContentList.get(idx);
            Intent detailIntent = new Intent(MainActivity.this, ImageDetailsActivity.class);
            detailIntent.putExtra("file_uri", selectedFile.getUri().toString());
            detailIntent.putExtra("file_name", selectedFile.getName());
            detailIntent.putExtra("file_size", selectedFile.length());
            detailIntent.putExtra("file_timestamp", selectedFile.lastModified());
            startActivity(detailIntent);
        });
    }

    private void triggerCameraCapture() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CODE_IMG_CAPTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_PERM_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                triggerCameraCapture();
            } else {
                Toast.makeText(this, "Access to camera is denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == CODE_DIR_PICKER) {
                targetDirectoryUri = data.getData();
                // Persist folder access rights
                getContentResolver().takePersistableUriPermission(targetDirectoryUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                refreshGalleryList();
            }
            else if (requestCode == CODE_IMG_CAPTURE) {
                Bitmap capturedBmp = (Bitmap) data.getExtras().get("data");
                processAndSaveBitmap(capturedBmp);
            }
        }
    }

    private void refreshGalleryList() {
        mediaContentList.clear();
        if (targetDirectoryUri != null) {
            DocumentFile rootDir = DocumentFile.fromTreeUri(this, targetDirectoryUri);
            if (rootDir != null && rootDir.isDirectory()) {
                for (DocumentFile item : rootDir.listFiles()) {
                    // Only include common image formats
                    if (item.getType() != null && item.getType().contains("image")) {
                        mediaContentList.add(item);
                    }
                }
            }
        }
        galleryAdapter.notifyDataSetChanged();
    }

    private void processAndSaveBitmap(Bitmap bmp) {
        if (targetDirectoryUri != null) {
            DocumentFile rootDir = DocumentFile.fromTreeUri(this, targetDirectoryUri);
            String timestampedName = "AppSnap_" + System.currentTimeMillis() + ".jpg";

            DocumentFile newFile = rootDir.createFile("image/jpeg", timestampedName);
            if (newFile != null) {
                try (OutputStream stream = getContentResolver().openOutputStream(newFile.getUri())) {
                    bmp.compress(Bitmap.CompressFormat.JPEG, 95, stream);
                    Toast.makeText(this, "Photo Saved!", Toast.LENGTH_SHORT).show();
                    refreshGalleryList();
                } catch (Exception e) {
                    Toast.makeText(this, "Error saving photo.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Automatically refresh if returning from deletion in Detail view
        if (targetDirectoryUri != null) {
            refreshGalleryList();
        }
    }
}