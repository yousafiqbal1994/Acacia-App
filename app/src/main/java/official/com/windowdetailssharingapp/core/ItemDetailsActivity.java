package official.com.windowdetailssharingapp.core;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;

import official.com.windowdetailssharingapp.R;
import official.com.windowdetailssharingapp.core.controllers.AdapterPropertiesList;
import official.com.windowdetailssharingapp.core.dao.ProjectDao;
import official.com.windowdetailssharingapp.core.models.Item;
import official.com.windowdetailssharingapp.core.models.Project;

public class ItemDetailsActivity extends AppCompatActivity {

    private Project project;
    private Item item;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        // Get intent extras
        Intent i = getIntent();
        item = (Item) i.getSerializableExtra("doorWindow");

        // Close activity if no project and item provided
        if (item == null) {
            finish();
            return;
        }

        // Read from database
        project = ProjectDao.getInstance().get(item.getProject());
        if (project == null) {
            finish();
            return;
        }

        // Display door/window details
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setSubtitle(String.format("%s (%s)", item.getId(), item.getType()));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Display property fields
        ListView propertiesListView = findViewById(R.id.propertiesListView);
        ListAdapter adapter = new AdapterPropertiesList(ItemDetailsActivity.this, item, item.getProperties());
        propertiesListView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save_file:
                saveProject();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveProject() {
        try {
            // Add new item to project
            ProjectDao dao = ProjectDao.getInstance();
            project.addItem(item);
            dao.save(project);
            Toast.makeText(ItemDetailsActivity.this, "Item saved", Toast.LENGTH_SHORT).show();
            finish();
        } catch (IOException e) {
            Toast.makeText(ItemDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Close Editor")
                .setMessage("Any unsaved changes will be lost. Do you wish to continue?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ItemDetailsActivity.super.onBackPressed();
                    }
                })
                .setNeutralButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveProject();
                        ItemDetailsActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

}