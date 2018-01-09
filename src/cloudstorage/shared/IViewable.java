package cloudstorage.shared;

/**
 * Defines a IViewable, a item which can be shown in the UI.
 * This includes files and folders.
 */
public interface IViewable {
    /**
     * Gets the id.
     * @return the id.
     */
    Integer getId();

    /**
     * Gets the name.
     * @return the name.
     */
    String getName();

    /**
     * Overload for the to string method, this is what will
     * be shown in the UI.
     * @return name which will be shown in the UI.
     */
    String toString();
}
