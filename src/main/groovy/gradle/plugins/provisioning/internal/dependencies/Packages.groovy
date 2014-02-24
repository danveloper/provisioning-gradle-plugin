package gradle.plugins.provisioning.internal.dependencies

class Packages {
    List<PackageDefinition> details = []
    List<Repository> repositories = []
    String url

    void group(String name) {
        details << new PackageDefinition(isGroup: true, name: name)
    }

    void pkg(String name) {
        details << new PackageDefinition(name: name)
    }

    void repo(String name, Closure clos) {
        def repo = new Repository(name: name)
        clos.delegate = repo
        clos.resolveStrategy = Closure.DELEGATE_FIRST
        repo.url = clos.call()
        repositories << repo
    }

    void url(String url) {
        this.url = url
    }
}
