package com.example.shifty.ui.dialogFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.shifty.R;

/**
 * Dialog fragment for adding a new update entry.
 * <p>
 * Provides UI elements for title, description, and pinned state, and sends
 * the result as a {@link Bundle} back to the parent fragment.
 * </p>
 *
 * <p>Example usage:</p>
 * <pre>
 * {@code
 * addUpdateDialog dialog = new addUpdateDialog();
 * dialog.show(getSupportFragmentManager(), "addUpdateDialog");
 * }
 * </pre>
 *
 * <p>
 * The parent fragment should register a {@link androidx.fragment.app.FragmentResultListener}
 * for the key <b>"UpdateAddRequest"</b> to receive the result.
 * </p>
 *
 * @author Eitan Navon
 * @see DialogFragment
 * @see androidx.fragment.app.FragmentManager#setFragmentResult(String, Bundle)
 */
public class addUpdateDialog extends DialogFragment implements View.OnClickListener {

    /** Input field for the update title. */
    EditText titleInput;
    /** Input field for the update description. */
    EditText descriptionInput;
    /** Button for adding the update. */
    Button addButton;
    /** Button for cancelling the dialog. */
    Button cancelButton;
    /** Checkbox for marking the update as pinned. */
    CheckBox isPinnedCheckBox;

    /**
     * Inflates the dialog layout and initializes its widgets.
     *
     * @param inflater the {@link LayoutInflater} used to inflate the layout
     * @param container the parent view that the dialog's UI should be attached to
     * @param savedInstanceState saved state, or {@code null}
     * @return the root view of the dialog
     * @throws NullPointerException if inflater is {@code null}
     * @see #initWidget(View)
     * @see DialogFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_add_dialog, container, false);
        initWidget(view);
        setCancelable(false);
        return view;
    }

    /**
     * Initializes dialog UI components and sets click listeners.
     *
     * @param view the root view containing the dialog's widgets
     * @throws NullPointerException if view is {@code null}
     */
    private void initWidget(View view) {
        titleInput = view.findViewById(R.id.titleInput);
        descriptionInput = view.findViewById(R.id.descriptionInput);
        addButton = view.findViewById(R.id.add);
        cancelButton = view.findViewById(R.id.cancel);
        isPinnedCheckBox = view.findViewById(R.id.pinCheckbox);
        addButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
    }

    /**
     * Handles click events for the dialog's buttons.
     *
     * @param view the clicked view
     * @throws NullPointerException if view is {@code null}
     */
    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.add) {
            onAddUpdateClick(view);
        } else if(view.getId() == R.id.cancel) {
            dismiss();
        }
    }

    /**
     * Handles the logic for the "add" button. Collects input, bundles data, and sends result to parent fragment.
     *
     * @param view the button view (unused)
     */
    private void onAddUpdateClick(View view) {
        String title = titleInput.getText().toString();
        String description = descriptionInput.getText().toString();
        long date = System.currentTimeMillis();
        boolean isPinned = isPinnedCheckBox.isChecked();

        Bundle result = new Bundle();
        result.putString("title", title);
        result.putString("description", description);
        result.putLong("date", date);
        result.putBoolean("isPinned", isPinned);

        getParentFragmentManager().setFragmentResult("UpdateAddRequest", result);
        dismiss();
    }

    /**
     * Interface for communicating dialog events to the host activity or fragment.
     */
    public interface Communicator {
        /**
         * Called when a dialog event needs to send a message.
         * @param message the message sent from the dialog
         */
        void onDialogMessage(String message);
    }
}
