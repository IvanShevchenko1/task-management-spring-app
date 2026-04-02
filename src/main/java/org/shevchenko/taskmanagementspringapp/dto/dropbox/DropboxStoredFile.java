package org.shevchenko.taskmanagementspringapp.dto.dropbox;

public record DropboxStoredFile(
        String fileId,
        String path,
        String filename
) {
}
