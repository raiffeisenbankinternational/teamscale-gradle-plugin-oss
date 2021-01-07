package upload;

import java.io.Serializable;
import java.util.Objects;

public class CommitDescriptor implements Serializable {
    public final String branch;
    public final String timestamp;

    public CommitDescriptor(String branch, long timestamp) {
        this(branch, String.valueOf(timestamp));
    }

    public CommitDescriptor(String branch, String timestamp) {
        this.branch = branch;
        this.timestamp = timestamp;
    }

    public static CommitDescriptor parse(String commit) {
        if (commit.contains(":")) {
            String[] split = commit.split(":");
            return new CommitDescriptor(split[0], split[1]);
        } else {
            return new CommitDescriptor("master", commit);
        }
    }

    @Override
    public String toString() {
        return branch + ":" + timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommitDescriptor that = (CommitDescriptor) o;
        return Objects.equals(branch, that.branch) &&
                Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(branch, timestamp);
    }
}
