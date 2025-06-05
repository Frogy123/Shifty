package com.example.shifty.viewmodel.fragment.Employee;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.shifty.model.Update;
import com.example.shifty.model.UpdateManager;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

/**
 * ViewModel for managing updates and their error state in the employee updates screen.
 * <p>
 * This class provides business logic for creating and tracking updates, interacting with
 * {@link UpdateManager}, and exposing LiveData for UI components to observe error or success messages.
 * Follows the MVVM pattern, separating UI from business logic.
 * </p>
 *
 * <h3>Main Responsibilities:</h3>
 * <ul>
 *   <li>Creating new updates (add/update pin status, title, and description)</li>
 *   <li>Posting result messages (success/failure) for UI to display</li>
 *   <li>Exposing LiveData for error/success feedback to be observed by Fragments/Activities</li>
 * </ul>
 *
 * @author Eitan Navon
 * @see UpdateManager
 */
public class UpdatesViewModel extends ViewModel {

    /**
     * LiveData string used to communicate success or error messages to the UI.
     */
    MutableLiveData<String> errorMsg;

    /**
     * Constructs a new UpdatesViewModel, initializing error message LiveData.
     */
    public UpdatesViewModel() {
        errorMsg = new MutableLiveData<>();
    }

    /**
     * Adds a new update, delegating the creation to {@link UpdateManager}.
     * Posts a success or error message to {@link #errorMsg} once completed.
     * <p>
     * This method is asynchronous and uses a {@link CompletableFuture} to post results when the operation completes.
     * </p>
     *
     * @param title       The title of the update.
     * @param description The body or message of the update.
     * @param isPinned    Whether the update is pinned (highlighted) in the UI.
     * @param date        The date/time of the update, as a Unix timestamp in milliseconds.
     * @see UpdateManager#addNewUpdate(Update)
     */
    public void addUpadate(String title, String description, boolean isPinned, long date) {
        Update newUpdate = new Update(title, description, isPinned, new Date(date));
        CompletableFuture<Boolean> isDone =  UpdateManager.getInstance().addNewUpdate(newUpdate);

        isDone.thenAccept((result) -> {
            if(result) errorMsg.postValue("Update added successfully");
            else errorMsg.postValue("Failed to add update");
        });
    }

    /**
     * Returns the LiveData object for error or success messages related to update actions.
     * UI components should observe this LiveData to display feedback to users.
     *
     * @return {@link MutableLiveData} containing the latest error or success message as a String.
     */
    public MutableLiveData<String> getErrorMsg() {
        return errorMsg;
    }
}
