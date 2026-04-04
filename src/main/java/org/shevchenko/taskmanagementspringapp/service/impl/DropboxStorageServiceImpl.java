package org.shevchenko.taskmanagementspringapp.service.impl;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.GetTemporaryLinkResult;
import com.dropbox.core.v2.files.WriteMode;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.shevchenko.taskmanagementspringapp.dto.dropbox.DropboxStoredFile;
import org.shevchenko.taskmanagementspringapp.exception.DropboxServiceException;
import org.shevchenko.taskmanagementspringapp.service.DropboxStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DropboxStorageServiceImpl implements DropboxStorageService {
    private final DbxClientV2 dbxClientV2;

    @Value("${dropbox.root-folder:/tasks}")
    private String rootFolder;

    @Override
    public DropboxStoredFile upload(Long taskId, String originalFilename, InputStream inputStream)
            throws IOException {
        try {
            String safeName = sanitizeFilename(originalFilename);
            String path = rootFolder
                    + "/task-" + taskId
                    + "/" + LocalDateTime.now().format(
                            DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                    + "-" + safeName;

            FileMetadata metadata = dbxClientV2.files()
                    .uploadBuilder(path)
                    .withMode(WriteMode.ADD)
                    .uploadAndFinish(inputStream);

            return new DropboxStoredFile(
                    metadata.getId(),
                    metadata.getPathLower(),
                    metadata.getName()
            );
        } catch (DbxException ex) {
            throw new IOException("Failed to upload file to Dropbox", ex);
        }
    }

    @Override
    public String createTemporaryDownloadLink(String dropboxPath) {
        try {
            GetTemporaryLinkResult result = dbxClientV2.files().getTemporaryLink(dropboxPath);
            return result.getLink();
        } catch (DbxException ex) {
            throw new DropboxServiceException("Failed to create Dropbox temporary link");
        }
    }

    @Override
    public void delete(String dropboxPath) {
        try {
            dbxClientV2.files().deleteV2(dropboxPath);
        } catch (DbxException ex) {
            throw new DropboxServiceException("Failed to delete file from Dropbox");
        }
    }

    private String sanitizeFilename(String filename) {
        return filename == null
                ? "file"
                : filename.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
