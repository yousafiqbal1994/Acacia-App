package official.com.windowdetailssharingapp.core.views;

import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import official.com.windowdetailssharingapp.R;

public class PropertyView extends RecyclerView.ViewHolder {
    public TextInputLayout propertyNameWrapper;
    public EditText propertyNameField;

    public View sketchButton;

    public PropertyView(final View itemView) {
        super(itemView);
        this.propertyNameWrapper = itemView.findViewById(R.id.propertyNameWrapper);
        this.propertyNameField = itemView.findViewById(R.id.propertyNameField);

        this.sketchButton = itemView.findViewById(R.id.sketchButton);
    }

}