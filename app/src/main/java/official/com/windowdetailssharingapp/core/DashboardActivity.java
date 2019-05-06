package official.com.windowdetailssharingapp.core;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.Locale;

import official.com.windowdetailssharingapp.R;
import official.com.windowdetailssharingapp.core.dao.ProjectDao;
import official.com.windowdetailssharingapp.core.models.Project;

public class DashboardActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_STORAGE = 100;
    private boolean permissionGranted;

    private TextInputLayout jobNumberWrapper;
    private TextInputLayout projectAddressWrapper;

    private EditText jobNumberField;
    private EditText projectAddressField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Configure action bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Get UI references
        jobNumberWrapper = findViewById(R.id.jobNumberWrapper);
        projectAddressWrapper = findViewById(R.id.projectAddressWrapper);

        projectAddressField = findViewById(R.id.projectAddress);
        jobNumberField = findViewById(R.id.jobNumber);

        findViewById(R.id.createButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!permissionGranted) {
                    checkAndRequestPermission();
                } else {
                    // Read inputs
                    String jobNumber = jobNumberField.getText().toString().trim();
                    final String projectAddress = projectAddressField.getText().toString().trim();

                    // Disable error messages
                    jobNumberWrapper.setErrorEnabled(false);
                    projectAddressWrapper.setErrorEnabled(false);

                    // Validate inputs
                    if (jobNumber.isEmpty()) {
                        jobNumberWrapper.setErrorEnabled(true);
                        jobNumberWrapper.setError("This field is required");
                    } else if (projectAddress.isEmpty()) {
                        projectAddressWrapper.setErrorEnabled(true);
                        projectAddressWrapper.setError("This field is required");
                    }

                    // On successful validation
                    else {
                        try {
                            // Create project if it does not exist
                            ProjectDao dao = ProjectDao.getInstance();

                            // Does the project already exist?
                            final int jobNo = Integer.parseInt(jobNumber);
                            String id = String.format(Locale.ENGLISH, "%d - %s", jobNo, projectAddress);
                            Project project = dao.get(id);
                            if (project != null) {
                                Intent i = new Intent(getApplicationContext(), ProjectDetailsActivity.class);
                                i.putExtra("project", project);
                                startActivity(i);
                            }

                            // If this is a new door/window
                            else {
                                Snackbar s = Snackbar.make(findViewById(R.id.coordinatorLayout),
                                        String.format(Locale.ENGLISH, "Project '%d-%s' does not exist. " +
                                                "Create new project?", jobNo, projectAddress.toUpperCase()),
                                        Snackbar.LENGTH_INDEFINITE)
                                        .setAction("Create", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent i = new Intent(getApplicationContext(), ProjectCreateActivity.class);
                                                i.putExtra("jobNumber", jobNo);
                                                i.putExtra("projectAddress", projectAddress);
                                                startActivity(i);
                                            }
                                        });
                                s.getView().setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                                s.addCallback(new Snackbar.Callback() {
                                    @Override
                                    public void onDismissed(Snackbar transientBottomBar, int event) {
                                        super.onDismissed(transientBottomBar, event);
                                        findViewById(R.id.createButton).setVisibility(View.VISIBLE);
                                    }
                                });
                                s.show();

                                findViewById(R.id.createButton).setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            // Display error message
                            projectAddressWrapper.setErrorEnabled(true);
                            projectAddressWrapper.setError(e.getMessage());
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
        jobNumberField.setText("");
        projectAddressField.setText("");

        // Check permissions
        permissionGranted = ContextCompat.checkSelfPermission(DashboardActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void checkAndRequestPermission() {
        // Are storage permissions granted?
        if (ContextCompat.checkSelfPermission(DashboardActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DashboardActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE) {
            permissionGranted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_saved_projects) {
            if (!permissionGranted) {
                checkAndRequestPermission();
            } else {
                Intent intent = new Intent(DashboardActivity.this, ProjectsActivity.class);
                startActivity(intent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}