package wooga.gradle.wooga.cocoapods.tasks


import org.gradle.api.tasks.TaskAction
import wooga.gradle.wooga.cocoapods.ArtRepositoriesSpec
import wooga.gradle.wooga.cocoapods.internal.PodUtils

class AddArtRepositories extends PodTask implements ArtRepositoriesSpec {

    AddArtRepositories() {
        onlyIf {
            def repos = artRepositories.getOrElse([])
            if(repos.empty) {
                logger.info(executableName.get())
                logger.info("No art-repository to add, skipping task")
            }
            return !repos.empty
        }
    }

    @TaskAction
    protected void install() {
        def existingRepositories = PodUtils.listRepoArtsURLs(this)
        def repositories = artRepositories.get()
        repositories.each { repoUrl ->
            def repoName = super.createArtRepoName(repoUrl)
            if(repoUrl in existingRepositories) {
                logger.info("${repoUrl} already registered as a repository, not invoking 'repo-art' for this entry")
            } else {
                super.podProcessExecutor(['repo-art', 'add', repoName, repoUrl, "--silent"])
                        .execute()
                        .assertNormalExitValue()
            }
        }
    }
}
