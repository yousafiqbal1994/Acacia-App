package official.com.windowdetailssharingapp.core;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import official.com.windowdetailssharingapp.R;
import official.com.windowdetailssharingapp.core.controllers.AdapterItemsList;
import official.com.windowdetailssharingapp.core.controllers.ItemCreateForm;
import official.com.windowdetailssharingapp.core.dao.ProjectDao;
import official.com.windowdetailssharingapp.core.models.Item;
import official.com.windowdetailssharingapp.core.models.Project;
import official.com.windowdetailssharingapp.export.ExportDialog;

public class ProjectDetailsActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_STORAGE = 100;
    private boolean permissionGranted;

    private ItemCreateForm itemCreateForm;
    private Project project;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        // Open given project
        Intent i = getIntent();
        project = (Project) i.getSerializableExtra("project");
        itemCreateForm = new ItemCreateForm(this, project);
        if (project == null) {
            finish();
            return;
        }

        // Get UI references
        TextView projectNmeView = findViewById(R.id.projectName);
        Button exportButton = findViewById(R.id.exportButton);

        ((TextInputEditText) findViewById(R.id.builder)).setText(project.getBuilder());
        ((TextInputEditText) findViewById(R.id.siteContact)).setText(project.getSiteContact());
        ((TextInputEditText) findViewById(R.id.comments)).setText(project.getComments());

        findViewById(R.id.addButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.newWindowForm).setVisibility(View.VISIBLE);
                findViewById(R.id.addButton).setVisibility(View.GONE);
            }
        });

        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.newWindowForm).setVisibility(View.GONE);
                findViewById(R.id.addButton).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.detailsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.detailsView).setVisibility(View.VISIBLE);
                findViewById(R.id.addButton).setVisibility(View.GONE);
            }
        });

        findViewById(R.id.dismissDetailsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.detailsView).setVisibility(View.GONE);
                findViewById(R.id.addButton).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.createButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!permissionGranted) {
                    checkAndRequestPermission();
                } else {
                    itemCreateForm.submit();
                }
            }
        });

        // Show project name
        projectNmeView.setText(project.getName());

        // Configure export button
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ExportDialog(ProjectDetailsActivity.this, project).show();
            }
        });
    }

    private void populateItemsList() {
        Project project = ProjectDao.getInstance().get(this.project.getName());
        if (project == null) {
            finish();
            return;
        }

        List<Item> items = project.getItemList();
        Collections.sort(items, new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                return item1.getId().compareToIgnoreCase(item2.getId());
            }
        });

        AdapterItemsList adapter = new AdapterItemsList(this, project, items);
        ListView doorWindowsList = findViewById(R.id.doorWindowsList);
        doorWindowsList.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkAndRequestPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateItemsList();

        // Clear input fields
        itemCreateForm.clear();

        // Check permissions
        permissionGranted = ContextCompat.checkSelfPermission(ProjectDetailsActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void checkAndRequestPermission() {
        // Are storage permissions granted?
        if (ContextCompat.checkSelfPermission(ProjectDetailsActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ProjectDetailsActivity.this,
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


}