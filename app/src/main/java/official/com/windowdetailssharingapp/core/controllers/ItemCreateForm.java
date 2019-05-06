package official.com.windowdetailssharingapp.core.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;

import official.com.windowdetailssharingapp.R;
import official.com.windowdetailssharingapp.core.ItemDetailsActivity;
import official.com.windowdetailssharingapp.core.models.Item;
import official.com.windowdetailssharingapp.core.models.Project;

import static official.com.windowdetailssharingapp.core.db.LocalStorage.ItemTypes.WINDOW_TYPES;

public class ItemCreateForm {

    private final Context context;
    private final Project project;

    private String id;
    private TextInputEditText idField;
    private TextInputLayout idFieldWrapper;

    private String type;
    private TextInputEditText typeField;
    private TextInputLayout typeFieldWrapper;

    public ItemCreateForm(@NonNull final Activity context, @NonNull Project project) {
        this.context = context;
        this.project = project;

        idField = context.findViewById(R.id.windowId);
        idField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                id = charSequence.toString().trim();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        idFieldWrapper = context.findViewById(R.id.windowIdWrapper);

        typeField = context.findViewById(R.id.windowType);
        typeField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                type = charSequence.toString().trim();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        typeFieldWrapper = context.findViewById(R.id.windowTypeWrapper);

        typeField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("D/W Type")
                        .setAdapter(new ArrayAdapter<>(context,
                                        android.R.layout.simple_list_item_1, WINDOW_TYPES),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        typeField.setText(WINDOW_TYPES[i]);
                                    }
                                })
                        .show();
            }
        });
    }

    private boolean isValidInput() {
        // Disable error messages
        idFieldWrapper.setErrorEnabled(false);
        typeFieldWrapper.setErrorEnabled(false);

        // Validate inputs
        if (id.isEmpty()) {
            idFieldWrapper.setErrorEnabled(true);
            idFieldWrapper.setError("This field is required");
            return false;
        } else if (type.isEmpty()) {
            typeFieldWrapper.setErrorEnabled(true);
            typeFieldWrapper.setError("This field is required");
            return false;
        }

        return true;
    }

    private void createProject() {
        // Does the door/window id already exist?
        if (project.containsItem(id)) {
            idFieldWrapper.setErrorEnabled(true);
            idFieldWrapper.setError("Item already exists.");
        }

        // If this is a new door/window
        else {
            // Create new door/window item
            Item item = new Item();
            item.setId(id);
            item.setType(type);
            item.setProject(project.getName());
            item.createPropertiesFromType(type);

            // Save item
            project.addItem(item);

            // Open new item in editor
            Intent intent = new Intent(context, ItemDetailsActivity.class);
            intent.putExtra("doorWindow", item);
            context.startActivity(intent);
        }
    }

    public void clear() {
        idField.setText("");
        typeField.setText("");
    }

    public void submit() {
        if (isValidInput()) {
            createProject();
        }
    }

}
