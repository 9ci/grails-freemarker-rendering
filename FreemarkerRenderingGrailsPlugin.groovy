class FreemarkerRenderingGrailsPlugin {
    // the plugin version
    def version = "0.2"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "1.3.0 > *"
    // the other plugins this plugin depends on
    def dependsOn = [:]
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
		"grails-app/views/**/*",
		"grails-app/controllers/**/*",
		"grails-app/services/grails/plugin/freemarker/test/**/*",
		"src/groovy/grails/plugin/freemarker/test/**/*",
		"plugins/**/*",
		"web-app/**/*"
	]

    // TODO Fill in these fields
    def author = "Joshua Burnett"
    def authorEmail = "joshua@greenbill.com"
    def title = "services to render "
    def description = '''\\
Renders Freemarker templates. Service allows output to pdf or xhtml. Uses the redering plugin (flying saucer XHTML) to generate pdfs
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/freemarker-rendering"

    def doWithWebDescriptor = { xml ->
        // TODO Implement additions to web.xml (optional), this event occurs before 
    }

    def doWithSpring = {
        // TODO Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // TODO Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { applicationContext ->
        // TODO Implement post initialization spring config (optional)
    }

    def onChange = { event ->
        // TODO Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // TODO Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }
}
