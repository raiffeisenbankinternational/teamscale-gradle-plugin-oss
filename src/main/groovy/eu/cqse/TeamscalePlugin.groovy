package eu.cqse

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.testing.Test
import upload.TeamscaleService

class TeamscalePlugin implements Plugin<Project> {

    private static final Logger BUILD_LOGGER = Logging.getLogger(TeamscalePlugin.class)

    void apply(Project project) {
        boolean configureUploadAfterEvaluate = true
        if (project.plugins.hasPlugin('com.android.application') || project.plugins.hasPlugin('com.android.library')) {
            configureUploadAfterEvaluate = false
        } else {
            project.pluginManager.apply('java')
        }

        project.extensions.add("teamscale", TeamscalePluginExtension)

        project.tasks.withType(Test) { gradleTestTask ->
            gradleTestTask.extensions.create('teamscale', TeamscalePluginExtension)

            if (configureUploadAfterEvaluate) {
                project.afterEvaluate {
                    appendReportUpload(project, gradleTestTask)
                }
            } else {
                appendReportUpload(project, gradleTestTask)
            }
        }
    }

    private static void appendReportUpload(Project project, Test gradleTestTask) {
        TeamscalePluginExtension root = project.extensions.teamscale
        TeamscalePluginExtension overwritten = gradleTestTask.extensions.teamscale
        BUILD_LOGGER.info("root project: " + root)
        BUILD_LOGGER.info("overwritten in task: " + gradleTestTask)
        TeamscalePluginExtension config = TeamscalePluginExtension.merge(root, overwritten)
        BUILD_LOGGER.info("merged config: " + config)
        TeamscaleUploadTask teamscaleUploadTask = getOrCreateTask(project.rootProject, TeamscaleUploadTask, gradleTestTask.name, "ReportUpload")

        teamscaleUploadTask.with {
            if (config.reportFormats.contains(TeamscaleService.EReportFormat.JUNIT)) {
                addReport(TeamscaleService.EReportFormat.JUNIT, config, gradleTestTask.reports.junitXml.destination)
            }
            if (config.reportFormats.contains(TeamscaleService.EReportFormat.JACOCO)) {
                Task jacocoReportTask = project.tasks.findByName("jacocoTestReport")
                dependsOn(jacocoReportTask)
                jacocoReportTask.reports.xml.enabled = true
                addReport(TeamscaleService.EReportFormat.JACOCO, config, jacocoReportTask.reports.xml.destination)
            }
        }
        teamscaleUploadTask
    }

    private
    static <T> T getOrCreateTask(Project project, Class<T> type, String namePrefix, String postfix) {
        T testDetailsUploadTask = project.tasks.findByName(namePrefix + postfix) as T
        if (testDetailsUploadTask == null) {
            testDetailsUploadTask = project.task(namePrefix + postfix, type: type) as T
        }
        testDetailsUploadTask
    }

    private static Collection<Project> getAllProjectsRecursive(Project project) {
        def all = project.subprojects.collect { getAllProjectsRecursive(it) }.flatten()
        all.add(project)
        return all as Collection<Project>
    }
}
