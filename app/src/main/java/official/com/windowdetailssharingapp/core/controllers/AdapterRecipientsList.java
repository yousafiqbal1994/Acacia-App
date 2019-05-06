package official.com.windowdetailssharingapp.core.controllers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

import official.com.windowdetailssharingapp.R;
import official.com.windowdetailssharingapp.core.dao.RecipientsDao;
import official.com.windowdetailssharingapp.core.models.Recipient;
import official.com.windowdetailssharingapp.core.views.RecipientsView;

public class AdapterRecipientsList extends ArrayAdapter<Recipient> {

    private final List<Recipient> recipients;
    private final List<Recipient> selectedRecipients = new ArrayList<>();

    public AdapterRecipientsList(Context context, List<Recipient> recipients) {
        super(context, -1, recipients);
        this.recipients = recipients;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_recipient, parent, false);

        RecipientsView holder = new RecipientsView(view);
        final Recipient recipient = recipients.get(position);

        holder.enabledView.setChecked(false);
        holder.enabledView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    selectedRecipients.add(recipient);
                } else {
                    selectedRecipients.remove(recipient);
                }
            }
        });
        holder.emailView.setText(recipient.getEmail());

        // Set up click actions
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove(recipient);
                recipients.remove(recipient);
                RecipientsDao.getInstance().remove(recipient);
            }
        });
        return view;
    }

    public List<Recipient> getRecipients() {
        return selectedRecipients;
    }

}