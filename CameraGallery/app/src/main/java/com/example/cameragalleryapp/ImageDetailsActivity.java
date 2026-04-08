package com.example.cameragalleryapp;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImageDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        // Map components to the new XML IDs
        ImageView mainPreview = findViewById(R.id.img_display_full);
        TextView infoLabelName = findViewById(R.id.info_file_name);
        TextView infoLabelPath = findViewById(R.id.info_file_path);
        TextView infoLabelSize = findViewById(R.id.info_file_size);
        TextView infoLabelDate = findViewById(R.id.info_file_date);
        Button actionRemoveBtn = findViewById(R.id.btn_remove_image);

        // Retrieve intent data using the updated keys from MainActivity
        String pathString = getIntent().getStringExtra("file_uri");
        String fileName = getIntent().getStringExtra("file_name");
        long fileSizeInBytes = getIntent().getLongExtra("file_size", 0);
        long lastModifiedDate = getIntent().getLongExtra("file_timestamp", 0);

        Uri resourceUri = Uri.parse(pathString);
        mainPreview.setImageURI(resourceUri);

        // c.i) Populate details
        infoLabelName.setText(String.format("Filename: %s", fileName));
        infoLabelPath.setText(String.format("Location: %s", pathString));

        // Convert bytes to KB for readability
        double sizeInKB = fileSizeInBytes / 1024.0;
        infoLabelSize.setText(String.format(Locale.getDefault(), "File Size: %.2f KB", sizeInKB));

        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM dd, yyyy - HH:mm", Locale.getDefault());
        infoLabelDate.setText(String.format("Captured on: %s", dateFormatter.format(new Date(lastModifiedDate))));

        // c.ii) Deletion logic with confirmation
        actionRemoveBtn.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Deletion")
                    .setMessage("This action cannot be undone. Delete this photo?")
                    .setPositiveButton("Confirm", (dialogInterface, i) -> {
                        DocumentFile targetFile = DocumentFile.fromSingleUri(this, resourceUri);

                        if (targetFile != null && targetFile.exists()) {
                            boolean isDeleted = targetFile.delete();
                            if (isDeleted) {
                                Toast.makeText(this, "File removed successfully", Toast.LENGTH_SHORT).show();
                                // Returns to the main gallery view
                                finish();
                            } else {
                                Toast.makeText(this, "Error: Could not delete file", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Dismiss", null)
                    .setIcon(android.R.drawable.ic_menu_delete)
                    .show();
        });
    }
}