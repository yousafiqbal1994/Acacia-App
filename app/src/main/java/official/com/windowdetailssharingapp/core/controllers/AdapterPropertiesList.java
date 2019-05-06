package official.com.windowdetailssharingapp.core.controllers;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import official.com.windowdetailssharingapp.R;
import official.com.windowdetailssharingapp.core.models.Item;
import official.com.windowdetailssharingapp.core.models.Property;
import official.com.windowdetailssharingapp.core.views.PropertyView;
import official.com.windowdetailssharingapp.drawing.DrawingActivity;

public class AdapterPropertiesList extends ArrayAdapter<Property> {

    private final Context context;
    private final Item item;
    private final List<Property> properties;

    public AdapterPropertiesList(Context context, Item item, List<Property> properties) {
        super(context, -1, properties);
        this.context = context;
        this.item = item;
        this.properties = properties;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_property, parent, false);

        PropertyView holder = new PropertyView(view);
        final Property property = properties.get(position);

        holder.propertyNameWrapper.setHint(property.getName());
        holder.propertyNameField.setText(property.getValue());

        // Bind property field with property object
        holder.propertyNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                properties.get(position).setValue(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // Header and footer properties have no sketches
        if (position < 4 || position == properties.size() - 1)
            holder.sketchButton.setVisibility(View.GONE);

        holder.sketchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DrawingActivity.class);
                intent.putExtra("index", position);
                intent.putExtra("property", property.getName());
                intent.putExtra("doorWindow", item.getId());
                intent.putExtra("project", item.getProject());
                context.startActivity(intent);
            }
        });

        return view;
    }


}