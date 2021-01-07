package upload;

import eu.cqse.config.Server;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Response;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class TeamscaleClient {
    private final TeamscaleService service;
    private final String projectId;

    public TeamscaleClient(Server server) {
        this(server.url, server.userName, server.userAccessToken, server.project);
    }

    public TeamscaleClient(String baseUrl, String user, String accessToken, String projectId) {
        this.projectId = projectId;
        service = TeamscaleServiceGenerator
                .createService(TeamscaleService.class, baseUrl, user, accessToken);
    }

    public void uploadReports(TeamscaleService.EReportFormat reportFormat, Collection<File> reports, CommitDescriptor commitDescriptor, String partition, String message) throws IOException {
        uploadReportBodys(reportFormat, commitDescriptor, partition, message, reports);
    }

    private void uploadReportBodys(TeamscaleService.EReportFormat reportFormat, CommitDescriptor commitDescriptor, String partition, String message, Collection<File> files) throws IOException {
        System.out.println("Uploading reports to " + commitDescriptor.toString() + " (" + partition + ")");
        List<MultipartBody.Part> partList = files.stream().map(file -> {
            RequestBody requestBody = RequestBody.create(MultipartBody.FORM, file);
            return MultipartBody.Part.createFormData("report", file.getName(), requestBody);
        }).collect(Collectors.toList());

        Response<ResponseBody> response = service.uploadExternalReports(projectId, reportFormat, commitDescriptor, true,
                partition, message, partList)
                .execute();
        if (!response.isSuccessful() || !response.body().string().equals(TeamscaleService.SUCCESS_STRING)) {
            throw new IOException(response.errorBody().string());
        }
    }

    public void uploadReport(TeamscaleService.EReportFormat reportFormat, String report, CommitDescriptor commitDescriptor, String partition, String message) throws IOException {
        System.out.println("Uploading report with params: commitDescriptor="
                + commitDescriptor.toString() + ", partition=" + partition + ",report=" + report
                + ",reportFormat=" + reportFormat + ",message=" + message);
        RequestBody requestFile = RequestBody.create(MultipartBody.FORM, report);
        uploadReportBody(reportFormat, commitDescriptor, partition, message, requestFile);
    }

    private void uploadReportBody(TeamscaleService.EReportFormat reportFormat, CommitDescriptor commitDescriptor, String partition, String message, RequestBody requestFile) throws IOException {
        System.out.println("Uploading report to " + commitDescriptor.toString() + " (" + partition + ")");
        Response<ResponseBody> response = service.uploadExternalReport(projectId, reportFormat, commitDescriptor, true,
                partition, message, requestFile)
                .execute();
        if (!response.isSuccessful() || !response.body().string().equals(TeamscaleService.SUCCESS_STRING)) {
            throw new IOException(response.errorBody().string());
        }
    }
}
