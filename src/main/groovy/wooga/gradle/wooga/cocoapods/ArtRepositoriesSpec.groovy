package wooga.gradle.wooga.cocoapods

import com.wooga.gradle.BaseSpec
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input

trait ArtRepositoriesSpec implements BaseSpec {

    private final ListProperty<String> artRepositories = objects.listProperty(String)

    @Input
    ListProperty<String> getArtRepositories() {
        return artRepositories
    }

    void setArtRepositories(Collection<String> artRepositories) {
        this.artRepositories.set(artRepositories)
    }

    void setArtRepositories(Provider<Collection<String>> artRepositories) {
        this.artRepositories.set(artRepositories)
    }

    void setArtRepository(String artRepo) {
        artRepositories.set([artRepo])
    }
}
