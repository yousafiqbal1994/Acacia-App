package official.com.windowdetailssharingapp.core.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 26/10/2018 3:01 AM
 */
public class Project implements Serializable {

    private final int jobNumber;
    private final String projectAddress;

    private String builder;
    private String siteContact;
    private String comments;

    private List<Item> itemList;

    public Project(int jobNumber, String projectAddress) {
        this.jobNumber = jobNumber;
        this.projectAddress = projectAddress;
        this.itemList = new ArrayList<>();
    }

    public String getProjectAddress() {
        return projectAddress;
    }

    public String getName() {
        return String.format(Locale.ENGLISH, "%d - %s - DCL", jobNumber, projectAddress).toUpperCase();
    }

    public int getJobNumber() {
        return jobNumber;
    }

    public String getBuilder() {
        return builder;
    }

    public void setBuilder(String builder) {
        this.builder = builder;
    }

    public String getSiteContact() {
        return siteContact;
    }

    public void setSiteContact(String siteContact) {
        this.siteContact = siteContact;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public void addItem(@NonNull Item item) {
        if (itemList == null) {
            itemList = new ArrayList<>();
        }

        if (this.containsItem(item.getId())) {
            setItem(item.getId(), item);
        } else {
            itemList.add(item);
        }
    }

    public boolean containsItem(@NonNull String id) {
        if (itemList != null) {
            for (Item item : itemList) {
                if (item.getId().equalsIgnoreCase(id)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Nullable
    public Item getItem(@NonNull String id) {
        if (itemList != null) {
            for (Item item : itemList) {
                if (item.getId().equalsIgnoreCase(id)) {
                    return item;
                }
            }
        }

        return null;
    }

    private void setItem(@NonNull String id, @NonNull Item doorWindow) {
        if (itemList != null) {
            for (Item item : itemList) {
                if (item.getId().equalsIgnoreCase(id)) {
                    item.setId(doorWindow.getId());
                    item.setType(doorWindow.getType());
                    item.setProperties(doorWindow.getProperties());
                    item.setType(doorWindow.getType());
                }
            }
        }
    }

    public void removeItem(String id) {
        if (itemList != null) {
            Item requiredItem = null;
            for (Item item : itemList) {
                if (item.getId().equalsIgnoreCase(id)) {
                    requiredItem = item;
                    break;
                }
            }

            if (requiredItem != null) itemList.remove(requiredItem);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return projectAddress;
    }

}