package pw.pdm.pdmserver.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pw.pdm.pdmserver.services.SessionKeyService;

@Component
public class SessionCleanupTask {
    private static final Logger logger = LoggerFactory.getLogger(SessionCleanupTask.class);

    @Autowired
    private SessionKeyService sessionKeyService;

    @Scheduled(fixedRate = 3600000) // Run every hour
    public void cleanupExpiredSessions() {

        logger.info("Running session cleanup task.");
        sessionKeyService.cleanupExpiredSessions();
    }
}