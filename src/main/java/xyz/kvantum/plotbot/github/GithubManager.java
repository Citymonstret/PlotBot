package xyz.kvantum.plotbot.github;

import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import xyz.kvantum.plotbot.BotConfig;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor public class GithubManager {
    private final Logger logger;
    private GitHub gitHub;
    private GHRepository repository;

    public void connect() {
        try {
            this.gitHub = GitHub.connectAnonymously();
        } catch (final IOException e) {
            logger.error("Failed to connect to github...", e);
            return;
        }
        try {
            this.repository = this.gitHub.getOrganization(BotConfig.Github.organization)
                .getRepository(BotConfig.Github.repository);
        } catch (final IOException e) {
            logger.error("Failed to fetch repository...", e);
        }
    }

    public Optional<GHIssue> getIssue(final int id) {
        try {
            return Optional.ofNullable(this.repository.getIssue(id));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

}
