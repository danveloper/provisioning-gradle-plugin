package gradle.plugins.provisioning.internal.disk

class Partitioning {
    private static final String PARTITION_CONFIG_NAME = "part"
    private static final String PARTITION_CLEAR_NAME = "clear"

    private Boolean clear = Boolean.FALSE
    private Boolean init = Boolean.FALSE

    List<Partition> details = []

    def invokeMethod(String name, args) {
        if (name == PARTITION_CONFIG_NAME) {
            def detail = new Partition()
            Closure clos = args[0]
            clos.delegate = detail
            clos.resolveStrategy = Closure.DELEGATE_FIRST
            clos.call()
            details << detail
        }
        if (name == PARTITION_CLEAR_NAME) {
            clear = Boolean.TRUE
            if (args.size() && args[0] instanceof Map) {
                init = args[0].init ?: Boolean.FALSE
            }
        }
    }
}
