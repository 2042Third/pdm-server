package pw.pdm.pdmserver.tasks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pw.pdm.pdmserver.services.SessionKeyService;

@Component
public class SessionCleanupTask {

    @Autowired
    private SessionKeyService sessionKeyService;

    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupExpiredSessions() {

        sessionKeyService.cleanupExpiredSessions();
    }
}