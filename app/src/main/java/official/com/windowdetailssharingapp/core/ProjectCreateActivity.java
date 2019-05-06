package official.com.windowdetailssharingapp.core;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import official.com.windowdetailssharingapp.R;
import official.com.windowdetailssharingapp.core.dao.ProjectDao;
import official.com.windowdetailssharingapp.core.models.Project;

public class ProjectCreateActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_STORAGE = 100;
    private boolean permissionGranted;

    private TextInputLayout builderWrapper;
    private TextInputLayout siteContactWrapper;
    private TextInputLayout commentsWrapper;

    private EditText builderField;
    private EditText siteContactField;
    private EditText commentsField;

    private int jobNumber;
    private String projectAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_create);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get intent extras
        Intent i = getIntent();
        jobNumber = i.getIntExtra("jobNumber", -1);
        projectAddress = i.getStringExtra("projectAddress");
        if (jobNumber == -1 || projectAddress == null || projectAddress.isEmpty()) {
            finish();
            return;
        }

        // Get UI references
        builderWrapper = findViewById(R.id.builderWrapper);
        siteContactWrapper = findViewById(R.id.siteContactWrapper);
        commentsWrapper = findViewById(R.id.commentsWrapper);

        builderField = findViewById(R.id.builder);
        siteContactField = findViewById(R.id.siteContact);
        commentsField = findViewById(R.id.comments);

        ((TextInputEditText) findViewById(R.id.jobNumber)).setText(String.valueOf(jobNumber));
        ((TextInputEditText) findViewById(R.id.projectAddress)).setText(projectAddress);

        findViewById(R.id.createButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!permissionGranted) {
                    checkAndRequestPermission();
                } else {
                    // Read inputs
                    String builder = builderField.getText().toString().trim();
                    String siteContact = siteContactField.getText().toString().trim();
                    String comments = commentsField.getText().toString().trim();

                    // Disable error messages
                    builderWrapper.setErrorEnabled(false);
                    siteContactWrapper.setErrorEnabled(false);

                    // Validate inputs
                    if (builder.isEmpty()) {
                        builderWrapper.setErrorEnabled(true);
                        builderWrapper.setError("This field is required");
                    } else if (siteContact.isEmpty()) {
                        siteContactWrapper.setErrorEnabled(true);
                        siteContactWrapper.setError("This field is required");
                    } else if (comments.isEmpty()) {
                        commentsWrapper.setErrorEnabled(true);
                        commentsWrapper.setError("This field is required");
                    }

                    // On successful validation
                    else {
                        try {
                            Project project = new Project(jobNumber, projectAddress);
                            project.setBuilder(builder);
                            project.setSiteContact(siteContact);
                            project.setComments(comments);

                            // Create project if it does not exist
                            ProjectDao dao = ProjectDao.getInstance();
                            dao.addIfNotExists(project);

                            Intent i = new Intent(getApplicationContext(), ProjectDetailsActivity.class);
                            i.putExtra("project", project);
                            startActivity(i);
                            finish();
                        } catch (Exception e) {
                            // Display error message
                            commentsWrapper.setErrorEnabled(true);
                            commentsWrapper.setError(e.getMessage());
                        }
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAndRequestPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Clear input fields
        builderField.setText("");
        siteContactField.setText("");
        commentsField.setText("");

        // Check permissions
        permissionGranted = ContextCompat.checkSelfPermission(ProjectCreateActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void checkAndRequestPermission() {
        // Are storage permissions granted?
        if (ContextCompat.checkSelfPermission(ProjectCreateActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ProjectCreateActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE) {
            permissionGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
    }

}