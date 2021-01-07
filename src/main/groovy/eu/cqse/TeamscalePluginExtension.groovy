package eu.cqse

import eu.cqse.config.Server
import test.GitRepositoryHelper
import upload.CommitDescriptor
import upload.TeamscaleService.EReportFormat

class TeamscalePluginExtension {
    public String url
    public String user
    public String accessToken
    public String projectId
    public String branch
    public Long timestamp
    public String partition
    public String message
    public List<EReportFormat> reportFormats

    static EReportFormat JACOCO = EReportFormat.JACOCO
    static EReportFormat JUNIT = EReportFormat.JUNIT

    /* package */ Server getServer() {
        def server = new Server()
        server.url = url
        server.userName = user
        server.userAccessToken = accessToken
        server.project = projectId
        return server
    }



    String getMessage() {
        return message
    }

    CommitDescriptor getCommit(File rootDir) {
        if (branch == null || timestamp == null) {
            return GitRepositoryHelper.getHeadCommitDescriptor(rootDir)
        } else {
            return new CommitDescriptor(branch, timestamp)
        }
    }

    static TeamscalePluginExtension merge(TeamscalePluginExtension root, TeamscalePluginExtension task) {
        TeamscalePluginExtension merged = new TeamscalePluginExtension()
        merged.url = task.url ?: root.url
        merged.user = task.user ?: root.user
        merged.accessToken = task.accessToken ?: root.accessToken
        merged.projectId = task.projectId ?: root.projectId
        merged.branch = task.branch ?: root.branch
        merged.timestamp = task.timestamp ?: root.timestamp
        merged.partition = task.partition ?: root.partition
        merged.message = task.message ?: root.message ?: "Gradle Upload"
        merged.reportFormats = task.reportFormats ?: root.reportFormats ?: [JUNIT, JACOCO]
        return merged
    }

    @Override
    String toString() {
        return "TeamscalePluginExtension{" +
                "url='" + url + '\'' +
                ", user='" + user + '\'' +
                ", accessToken='" + accessToken + '\'' +
                ", projectId='" + projectId + '\'' +
                ", branch='" + branch + '\'' +
                ", timestamp=" + timestamp +
                ", partition='" + partition + '\'' +
                ", message='" + message + '\'' +
                ", reportFormats=" + reportFormats +
                '}';
    }
}
