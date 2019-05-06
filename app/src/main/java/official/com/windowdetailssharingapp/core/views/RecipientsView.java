package official.com.windowdetailssharingapp.core.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import official.com.windowdetailssharingapp.R;

public class RecipientsView extends RecyclerView.ViewHolder {
    public CheckBox enabledView;
    public TextView emailView;

    public View deleteButton;

    public RecipientsView(final View itemView) {
        super(itemView);
        this.enabledView = itemView.findViewById(R.id.recipientEnabled);
        this.emailView = itemView.findViewById(R.id.emailView);

        this.deleteButton = itemView.findViewById(R.id.deleteButton);
    }

}