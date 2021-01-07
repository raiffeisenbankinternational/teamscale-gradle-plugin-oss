Notes

The plugin uses jgit to communicate with the git repository.
In order to avoid conflicts with the git-properties plugin the jgit version is fixed to 4.11.0.201803080745-r. Starting 
with the 5.0 versions the jgit plugin introduces incompatible changes, so prior to upgrade it the compatability must be tested.

More detailed output from the plugin can be gained using the --info option to the gradle.


For the latest version of the plugin see build.gradle

Build command:

```
./gradlew clean build
```

Configuration in build.gradle

```groovy
buildscript {
	repositories {
		mavenLocal()
	}
	dependencies {
		classpath 'eu.cqse:teamscale-gradle-plugin:$version'
	}
}

apply plugin: 'java'
apply plugin: 'jacoco'
apply plugin: 'teamscale'

teamscale {
    url = 'https://mycompany.com/teamsale'
    user = 'build'
    accessToken = '7fa5.....'
    projectId = 'example-project-id'

    // Partition
    partition = 'Unit Tests'

    // The commit message to show for the uploaded reports (optional, Default: 'Gradle Upload')
    message = 'Gradle Upload'

    // The report formats that should be uploaded to Teamscale (optional, Default: [JACOCO])
    reportFormats = [JUNIT, JACOCO]

    // The following is optional. By default the plugin looks for a git
    // repository in the project's root directory and takes the branch and
    // timestamp of the currently checked out commit.
    // Only has an effect if both branch and timestamp are set.
    branch = 'master'
    timestamp = 1521800427000L // Timestamp in milliseconds
}
```

Any of those settings can be overridden in the test task's closure. This is comes in handy if you have multiple test tasks.

```
task unitTest(type: Test) {
    useJUnitPlatform {
        excludeTags 'integration'
    }
    teamscale {
        partition = 'Unit Tests'
    }
}

task integrationTest(type: Test) {
    useJUnitPlatform {
        includeTags 'integration'
    }
    teamscale {
        partition = 'Integration Tests'
    }
}
```

Uploading reports can be triggered with `unitTestReportUpload` whereas `unitTest` must be replaced with your test task's name.
If no custom unit test task is used then the triggering task is simply `testReportUpload`. Example:

```
gradle clean build testReportUpload
```

The plugin requires the usage of the `jacoco` plugin and properly configured `jacocoTestReport` 
to generate the necessary jacoco report in xml format:

```
jacocoTestReport {
	group = "Reporting"
	reports {
		xml.enabled true
        xml.destination "${buildDir}/reports/jacoco/test/${project.name}.xml"
	}
}

```