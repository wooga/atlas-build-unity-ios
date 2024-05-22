package wooga.gradle.wooga.cocoapods.tasks


import org.gradle.api.tasks.TaskAction
import wooga.gradle.wooga.cocoapods.ArtRepositoriesSpec
import wooga.gradle.wooga.cocoapods.internal.PodUtils

class UpdateArtRepositories extends PodTask implements ArtRepositoriesSpec {

    UpdateArtRepositories() {
        onlyIf {
            def repos = artRepositories.getOrElse([])
            if(repos.empty) {
                logger.info("No artifactory repositories to add, skipping task")
            }
            return !repos.empty
        }
    }

    @TaskAction
    protected void install() {
        def allRepositories = artRepositories.get()
        def existingRepositories = PodUtils.listRepoArtsURLs(this)
        allRepositories.each { repoUrl ->
            def repoName = super.createArtRepoName(repoUrl)
            if (repoName in existingRepositories) {
                super.podProcessExecutor(['repo-art', 'update', repoName])
                        .execute()
                        .assertNormalExitValue()
            } else {
                logger.info("${repoUrl} not listed in artifactory repositories list, (`repo-art list` command), not invoking `repo-art update` for this entry")
            }
        }
    }

}
