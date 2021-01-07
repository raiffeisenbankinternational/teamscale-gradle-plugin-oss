package test;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import upload.CommitDescriptor;

import java.io.File;
import java.io.IOException;

public class GitRepositoryHelper {
    private GitRepositoryHelper() {
    }

    public static CommitDescriptor getHeadCommitDescriptor(File baseDirectory) throws IOException {
        Git git = Git.open(baseDirectory);
        Repository repository = git.getRepository();
        String branch = repository.getBranch();
        RevCommit commit = getCommit(repository, branch);
        long time = commit.getCommitTime();
        return new CommitDescriptor(branch, time * 1000L);
    }


    /** Returns the commit denoted by the given commit id/tag/head. */
    private static RevCommit getCommit(Repository repository, String revisionBranchOrTag) throws IOException {
        RevWalk revWalk = new RevWalk(repository);
        try {
            Ref head = repository.getRef(revisionBranchOrTag);
            if (head != null) {
                return revWalk.parseCommit(head.getLeaf().getObjectId());
            }
            return revWalk.parseCommit(ObjectId.fromString(revisionBranchOrTag));
        } finally {
            revWalk.close();
        }
    }
}
