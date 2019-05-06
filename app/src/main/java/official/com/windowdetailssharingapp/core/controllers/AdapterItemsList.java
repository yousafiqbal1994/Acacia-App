package official.com.windowdetailssharingapp.core.controllers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import official.com.windowdetailssharingapp.R;
import official.com.windowdetailssharingapp.core.ItemDetailsActivity;
import official.com.windowdetailssharingapp.core.dao.ProjectDao;
import official.com.windowdetailssharingapp.core.models.Item;
import official.com.windowdetailssharingapp.core.models.Project;
import official.com.windowdetailssharingapp.core.views.ItemView;
import official.com.windowdetailssharingapp.export.ExportWizard;

public class AdapterItemsList extends ArrayAdapter<Item> {

    private final Context context;
    private final Project project;
    private List<Item> items;

    public AdapterItemsList(Context context, Project project, List<Item> items) {
        super(context, -1, items);
        this.context = context;
        this.project = project;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_item, parent, false);

        ItemView holder = new ItemView(view);
        final Item item = items.get(position);

        holder.idView.setText(item.getId());
        holder.typeView.setText(item.getType());

        // Set up click actions
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Are you sure?")
                        .setMessage("This action cannot be reverted. Proceed with caution.")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    project.removeItem(item.getId());
                                    ProjectDao.getInstance().save(project);

                                    remove(item);
                                    items.remove(item);
                                } catch (IOException ignored) {

                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .show();
            }
        });

        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open door/window in editor
                Intent intent = new Intent(context, ItemDetailsActivity.class);
                intent.putExtra("doorWindow", item);
                context.startActivity(intent);
            }
        });

        holder.viewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // Export PDF document
                    String path = ExportWizard.exportItem(item);
                    assert path != null;

                    // Open PDF document in external app
                    File pdfFile = new File(path);
                    Uri uri = FileProvider.getUriForFile(context,
                            context.getApplicationContext().getPackageName() + ".provider",
                            pdfFile);

                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(uri, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    context.startActivity(intent);

                } catch (Exception ex) {
                    Toast.makeText(context, "Cannot open file", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }


}