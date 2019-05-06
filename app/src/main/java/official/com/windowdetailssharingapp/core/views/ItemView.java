package official.com.windowdetailssharingapp.core.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import official.com.windowdetailssharingapp.R;

public class ItemView extends RecyclerView.ViewHolder {
    public TextView idView;
    public TextView typeView;

    public View viewButton;
    public View editButton;
    public View deleteButton;

    public ItemView(final View itemView) {
        super(itemView);
        this.idView = itemView.findViewById(R.id.idView);
        this.typeView = itemView.findViewById(R.id.typeView);

        this.viewButton = itemView.findViewById(R.id.viewButton);
        this.editButton = itemView.findViewById(R.id.editButton);
        this.deleteButton = itemView.findViewById(R.id.deleteButton);
    }

}