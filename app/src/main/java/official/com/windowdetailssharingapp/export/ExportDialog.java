package official.com.windowdetailssharingapp.export;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.crashlytics.android.Crashlytics;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.RejectedExecutionException;

import official.com.windowdetailssharingapp.R;
import official.com.windowdetailssharingapp.core.controllers.AdapterRecipientsList;
import official.com.windowdetailssharingapp.core.dao.RecipientsDao;
import official.com.windowdetailssharingapp.core.models.Project;
import official.com.windowdetailssharingapp.core.models.Recipient;
import official.com.windowdetailssharingapp.export.emailsender.GMailSender;

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 20/12/2018 9:16 AM
 */
public class ExportDialog extends Dialog {

    private final Project project;

    private AdapterRecipientsList adapterRecipientsList;
    private ListView recipientListView;

    private MaterialButton addRecipientButton;
    private MaterialButton emailButton;

    private ProgressDialog progressDialog;

    public ExportDialog(@NonNull Context context, Project project) {
        super(context);
        this.project = project;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_project_export);

        // Get UI references
        recipientListView = findViewById(R.id.recipientsList);
        addRecipientButton = findViewById(R.id.addRecipientButton);
        emailButton = findViewById(R.id.emailButton);

        // Populate recipients list
        List<Recipient> recipientList = RecipientsDao.getInstance().getAll();
        adapterRecipientsList = new AdapterRecipientsList(getContext(), recipientList);
        recipientListView.setAdapter(adapterRecipientsList);

        // Configure click handlers
        addRecipientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TextInputLayout emailWrapper = new TextInputLayout(getContext());
                emailWrapper.setHint("Recipient's Email");
                emailWrapper.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
                emailWrapper.setBoxCornerRadii(16, 16, 16, 16);
                emailWrapper.setBoxStrokeColor(getContext().getResources().getColor(R.color.colorAccent));
                emailWrapper.setHelperText("The email address will be saved in your contact list. " +
                        "You will be asked to select recipient(s) from this list when you export " +
                        "a project.\n" +
                        "\n" +
                        "Email addresses must be valid and unique.");

                final TextInputEditText emailField = new TextInputEditText(getContext());
                emailField.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                emailField.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        emailWrapper.setErrorEnabled(false);
                        if (emailField.getText() != null) {
                            String email = emailField.getText().toString().toLowerCase().trim();
                            if (RecipientsDao.getInstance().get(email) != null) {
                                emailWrapper.setErrorEnabled(true);
                                emailWrapper.setError("Email already exists");
                            }
                        }
                    }
                });
                final AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setPositiveButton("Add", null)
                        .create();

                dialog.setOnShowListener(new OnShowListener() {
                    @Override
                    public void onShow(final DialogInterface dialogInterface) {
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                .setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        try {
                                            // Reset error message
                                            emailWrapper.setErrorEnabled(false);

                                            // Get user input
                                            String email = emailField.getText().toString().toLowerCase().trim();

                                            // Is this email address valid?
                                            if (email.isEmpty() || !email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")) {
                                                emailWrapper.setErrorEnabled(true);
                                                emailWrapper.setError("Enter a valid email address");
                                            }

                                            // Is this email address unique?
                                            else if (RecipientsDao.getInstance().get(email) != null) {
                                                emailWrapper.setErrorEnabled(true);
                                                emailWrapper.setError("Email already exists");
                                            }

                                            // Create new recipient
                                            else {
                                                Recipient recipient = new Recipient();
                                                recipient.setEmail(email);

                                                RecipientsDao.getInstance().add(recipient);
                                                adapterRecipientsList.add(recipient);

                                                dialogInterface.dismiss();
                                            }
                                        } catch (Exception ex) {
                                            Crashlytics.logException(ex);

                                            emailWrapper.setErrorEnabled(true);
                                            emailWrapper.setError("Unknown error. Try again or contact system admin");
                                        }
                                    }
                                });
                    }
                });

                // Set view hierarchy
                LinearLayout layout = new LinearLayout(getContext());
                layout.setPadding(24, 24, 24, 24);

                emailWrapper.addView(emailField);
                layout.addView(emailWrapper);
                dialog.setView(layout);

                // Display dialog
                dialog.show();
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    List<Recipient> selectedRecipients = adapterRecipientsList.getRecipients();
                    if (selectedRecipients.size() == 0) {
                        return;
                    }

                    // Export project
                    String attachment = ExportWizard.exportProject(project);
                    if (attachment == null) throw new IOException();

                    // Email exported archive
                    emailProject(selectedRecipients, attachment);

                } catch (IOException ex) {
                    Crashlytics.logException(ex);
                    onTaskCompleted(false);
                }
            }
        });
    }

    private void emailProject(@NonNull List<Recipient> recipients, @NonNull String project)
            throws RejectedExecutionException {
        String subject = String.format(Locale.ENGLISH, "%d - %s - *DCL",
                this.project.getJobNumber(), this.project.getProjectAddress());

        EmailTask emailTask = new EmailTask(recipients, project, subject);
        emailTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void onTaskStart() {
        this.progressDialog = new ProgressDialog(getContext());
        this.progressDialog.setMessage("Emailing project archive ...");
        this.progressDialog.setIndeterminate(false);
        this.progressDialog.setCancelable(false);
        this.progressDialog.show();
    }

    private void onTaskCompleted(boolean success) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }

        Snackbar.make(recipientListView, success ?
                "Project successfully exported. If you can't find it, look under Spam :-)" :
                "Export failed. Check app permissions and network connection", Snackbar.LENGTH_LONG)
                .show();
    }

    @SuppressLint("StaticFieldLeak")
    public class EmailTask extends AsyncTask<String, Void, Boolean> {

        private final List<Recipient> recipients;
        private final String project;
        private final String subject;

        EmailTask(List<Recipient> recipients, String project, String subject) {
            this.recipients = recipients;
            this.project = project;
            this.subject = subject;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            onTaskStart();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                GMailSender sender = new GMailSender();
                String message = "Hi<br><br>" +
                        "Please find attached the archive for your project.<br><br>" +
                        "If you did not request this archive, please ignore this email. Do not reply, this is an auto-generated email.<br><br>" +
                        "Thank You";

                StringBuilder recipientList = new StringBuilder();
                for (Recipient recipient : recipients) {
                    recipientList.append(recipient.getEmail()).append(",");
                }

                sender.sendMail(subject, message, recipientList.toString(), project + ".zip");
                return true;
            } catch (Exception e) {
                Crashlytics.logException(e);
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            onTaskCompleted(result);
        }
    }

}