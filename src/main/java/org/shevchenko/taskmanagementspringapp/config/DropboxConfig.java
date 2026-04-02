package org.shevchenko.taskmanagementspringapp.config;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.oauth.DbxCredential;
import com.dropbox.core.v2.DbxClientV2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DropboxConfig {
    @Bean
    public DbxClientV2 dbxClientV2(
            @Value("${dropbox.app-name}") String appName,
            @Value("${dropbox.access-token:}") String accessToken,
            @Value("${dropbox.refresh-token:}") String refreshToken,
            @Value("${dropbox.client-id:}") String clientId,
            @Value("${dropbox.client-secret:}") String clientSecret
    ) {
        DbxRequestConfig config = DbxRequestConfig.newBuilder(appName).build();

        if (refreshToken != null && !refreshToken.isBlank()
                && clientId != null && !clientId.isBlank()
                && clientSecret != null && !clientSecret.isBlank()) {
            DbxCredential credential = new DbxCredential(
                    accessToken == null ? "" : accessToken,
                    -1L,
                    refreshToken,
                    clientId,
                    clientSecret
            );
            return new DbxClientV2(config, credential);
        }

        return new DbxClientV2(config, accessToken);
    }
}
