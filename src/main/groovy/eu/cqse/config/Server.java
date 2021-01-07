package eu.cqse.config;

import java.util.Objects;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Server {
    public String url;
    public String project;
    public String userName;
    public String userAccessToken;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Server server = (Server) o;
        return Objects.equals(url, server.url) &&
                Objects.equals(project, server.project) &&
                Objects.equals(userName, server.userName) &&
                Objects.equals(userAccessToken, server.userAccessToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, project, userName, userAccessToken);
    }
}
