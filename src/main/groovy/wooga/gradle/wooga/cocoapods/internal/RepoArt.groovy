package wooga.gradle.wooga.cocoapods.internal

import java.nio.file.Path
import java.nio.file.Paths

class RepoArt {
    /*
   `pod repo-art list` command outputs a list like the following:
   artifactory.playticorp.com-443-artifactory-api-pods-Octopus-cocoapods-STAGING
   - URL: https://artifactory.playticorp.com/artifactory/api/pods/Octopus-cocoapods-STAGING
   - Path: /Users/joaquim.neto/.cocoapods/repos-art/artifactory.playticorp.com-443-artifactory-api-pods-Octopus-cocoapods-STAGING/

   name
   - URL: https://artifactory.playticorp.com/artifactory/api/pods/Octopus-cocoapods-STAGING
   - Path: /Users/joaquim.neto/.cocoapods/repos-art/name/

   2 repos
    */
    static List<RepoArt> fromListLog(String stdout) {
        def result = new ArrayList<RepoArt>()
        Queue<String> lines = new LinkedList<>(stdout.readLines())
        while(!lines.empty) {
            def current = lines.poll()
            if(!current.empty && !current.startsWith("-")) {
                def maybeRepo = nextFromLog([current] + lines)
                if(maybeRepo.present) {
                    result.add(maybeRepo.get())
                }
            }
        }
        return result
    }

    static Optional<RepoArt> nextFromLog(List<String> stdout) {
        def normalizedLines = stdout.collect{it.trim()}.findAll { !it.empty }
        if(normalizedLines.size() == 0) {
            return Optional.empty()
        }

        def maybeName = normalizedLines.first()
        if(maybeName.endsWith("repos")) {
            return Optional.empty()
        }

        def name = maybeName
        def url = nextRepoArtListLogItem(normalizedLines, "URL:")
        def path = nextRepoArtListLogItem(normalizedLines, "Path:").map {
            Paths.get(it)
        }
        if(url.present && path.present) {
            return Optional.of(new RepoArt(name, url.get(), path.get()))
        }
        return Optional.empty()
    }

    static Optional<String> nextRepoArtListLogItem(List<String> lines, String itemName) {
        def path = lines.find { it.contains(itemName) }
        return path.split(itemName).with { String[] it ->
            if(it.size() > 1) {
                return Optional.of(it[1].trim())
            }
            return Optional.empty() as Optional<String>
        }
    }

    final String name
    final String url
    final Path path

    RepoArt(String name, String url, Path path) {
        this.name = name
        this.url = url
        this.path = path
    }
}