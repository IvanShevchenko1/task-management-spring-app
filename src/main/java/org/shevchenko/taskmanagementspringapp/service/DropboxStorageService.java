package org.shevchenko.taskmanagementspringapp.service;

import java.io.IOException;
import java.io.InputStream;
import org.shevchenko.taskmanagementspringapp.dto.dropbox.DropboxStoredFile;

public interface DropboxStorageService {
    DropboxStoredFile upload(Long taskId,
                             String originalFilename,
                             InputStream inputStream) throws IOException;

    String createTemporaryDownloadLink(String dropboxPath);

    void delete(String dropboxPath);

}
