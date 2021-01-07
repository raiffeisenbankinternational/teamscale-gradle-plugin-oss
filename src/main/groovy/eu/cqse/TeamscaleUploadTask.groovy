package eu.cqse

import eu.cqse.config.Server
import upload.CommitDescriptor
import upload.TeamscaleClient
import upload.TeamscaleService
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class TeamscaleUploadTask extends DefaultTask {

    private class ReportUploadKey {
        public Server teamscaleServer
        public CommitDescriptor commitDescriptor
        public String partition
        public String message

        boolean equals(o) {
            if (this.is(o)) return true
            if (getClass() != o.class) return false
            ReportUploadKey that = (ReportUploadKey) o
            if (commitDescriptor != that.commitDescriptor) return false
            if (message != that.message) return false
            if (partition != that.partition) return false
            if (teamscaleServer != that.teamscaleServer) return false
            return true
        }

        int hashCode() {
            return Objects.hash(teamscaleServer, commitDescriptor, partition, message)
        }
    }

    private Map<ReportUploadKey, Map<TeamscaleService.EReportFormat, List<File>>> reports = new HashMap<>()

    void addReport(TeamscaleService.EReportFormat format, TeamscalePluginExtension config, File report) {
        ReportUploadKey key = new ReportUploadKey()
        key.teamscaleServer = config.getServer()
        key.message = config.getMessage()
        key.commitDescriptor = config.getCommit(project.rootDir)
        key.partition = config.partition
        def map = reports.computeIfAbsent(key, { _ -> new HashMap<>() })
        map.computeIfAbsent(format, { _ -> new ArrayList<>() }).add(report)
    }

    TeamscaleUploadTask() {
        group = 'Teamscale'
        description = 'Uploads reports to Teamscale'
    }

    @TaskAction
    def action() {
        for (ReportUploadKey key : reports.keySet()) {
            logger.info("Uploading to " + key.commitDescriptor + "...")
            TeamscaleClient client = new TeamscaleClient(key.teamscaleServer)

            for (TeamscaleService.EReportFormat format : reports.get(key).keySet()) {
                def filterForFormat = getFilterForFormat(format)
                List<File> reportFiles = reports.get(key).get(format).collect { report -> listFileTree(report, filterForFormat) }.flatten() as List<File>
                logger.info("Uploading ${reportFiles.size()} ${format.name()} report(s)...")
                if(reportFiles.isEmpty()) {
                    logger.info("Skipped empty upload!")
                    continue
                }
                String partition = getPartition(format, key.partition)
                String message = "${format.name()} ${key.message}"
                logger.info("format=" + format + ",key.commitDescriptor=" + key.commitDescriptor + ", partition=" + partition + ", message=" + message)
                for (File file : reportFiles) {
                    logger.info("reportFile=" + file.getName())
                }
                client.uploadReports(format, reportFiles, key.commitDescriptor, partition, message)
            }
        }
    }

    static FilenameFilter getFilterForFormat(TeamscaleService.EReportFormat format) {
        switch (format) {
            case TeamscaleService.EReportFormat.JACOCO:
            case TeamscaleService.EReportFormat.JUNIT:
            default:
                return new FilenameFilter() {
                    @Override
                    boolean accept(File dir, String name) {
                        return name.toLowerCase().endsWith(".xml")
                    }
                }
        }
    }

    static String getPartition(TeamscaleService.EReportFormat format, String partition) {
        if (format == TeamscaleService.EReportFormat.JACOCO) {
            return partition
        } else {
            return "$partition ($format)"
        }
    }

    Collection<File> listFileTree(File file, FilenameFilter filter) {
        if (file.isFile()) {
            return Collections.singletonList(file)
        }
        Set<File> fileTree = new HashSet<>()
        if (file == null || file.listFiles() == null) {
            return fileTree
        }
        for (File entry : file.listFiles(filter)) {
            if (entry.isFile()) fileTree.add(entry)
            else fileTree.addAll(listFileTree(entry, filter))
        }
        return fileTree
    }
}
