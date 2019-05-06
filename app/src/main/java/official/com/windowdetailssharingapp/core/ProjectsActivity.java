package official.com.windowdetailssharingapp.core;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import official.com.windowdetailssharingapp.R;
import official.com.windowdetailssharingapp.core.dao.ProjectDao;
import official.com.windowdetailssharingapp.core.models.Project;

public class ProjectsActivity extends AppCompatActivity implements
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private ArrayAdapter<String> projectsAdapter;
    private List<String> projects;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        projects = ProjectDao.getInstance().getNames();
        for (int i = 0; i < projects.size(); i++)
            projects.set(i, projects.get(i));

        ListView projectsListView = findViewById(R.id.projectsListView);
        projectsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, projects);
        projectsListView.setAdapter(projectsAdapter);

        projectsListView.setOnItemLongClickListener(this);
        projectsListView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String projectId = projects.get(i);
        Project project = ProjectDao.getInstance().get(projectId);

        Intent intent = new Intent(getApplicationContext(), ProjectDetailsActivity.class);
        intent.putExtra("project", project);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        final String projectId = projects.get(i);

        new AlertDialog.Builder(this)
                .setTitle("Delete " + projects.get(i))
                .setMessage("You cannot undo this action. Proceed with caution.")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ProjectDao.getInstance().remove(projectId);
                        projectsAdapter.remove(projectId);
                        projects.remove(projectId);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .show();
        return true;
    }

}