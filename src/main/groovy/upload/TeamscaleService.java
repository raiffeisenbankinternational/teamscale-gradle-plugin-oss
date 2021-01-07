package upload;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface TeamscaleService {

    String SUCCESS_STRING = "success";

    enum EReportFormat {
        JACOCO,
        COBERTURA,
        GCOV,
        LCOV,
        CTC,
        XR_BABOON,
        MS_COVERAGE,
        DOT_COVER,
        ROSLYN,
        JUNIT,
        SIMPLE,
        CPPCHECK,
        PCLINT,
        CLANG
    }

    @Multipart
    @POST("p/{projectName}/external-report/")
    Call<ResponseBody> uploadExternalReport(@retrofit2.http.Path("projectName") String projectName,
                                            @Query("format") EReportFormat format,
                                            @Query("t") CommitDescriptor commit,
                                            @Query("adjusttimestamp") boolean adjustTimestamp,
                                            @Query("partition") String partition,
                                            @Query("message") String message,
                                            @Part("report") RequestBody report
    );

    @Multipart
    @POST("p/{projectName}/external-report/")
    Call<ResponseBody> uploadExternalReports(@retrofit2.http.Path("projectName") String projectName,
                                            @Query("format") EReportFormat format,
                                            @Query("t") CommitDescriptor commit,
                                            @Query("adjusttimestamp") boolean adjustTimestamp,
                                            @Query("partition") String partition,
                                            @Query("message") String message,
                                            @Part List<MultipartBody.Part> report
    );
}