package ch.unibe.sport.taskmanager;

/**
 * Simple progress tracker interface for asynctask
 * @author Aliaksei Syrel
 */
public interface IProgressTracker {
    /**
     * Updates progress message
     * @param message to be set sa progress message
     */
    void onProgress(String message);
    /**
     * Notifies about task completeness
     */
    void onCompleted();
}