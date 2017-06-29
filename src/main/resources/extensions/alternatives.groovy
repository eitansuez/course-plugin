
block(name: "alternatives", contexts: [":paragraph"]) {
  parent, reader, attributes ->

    def id = attributes.id

    def tabinfo = reader.readLines().join("\n")
    def tabs = tabinfo.split(/\|/).collect{it.trim()}.findAll{!it.empty}

    def writer = new StringWriter()
    def builder = new groovy.xml.MarkupBuilder(writer)
    builder.doubleQuotes = true

    builder.div(class: "tabs", 'data-for': id) {
      ul {
        tabs.eachWithIndex { tab, i ->
          li { a(href: "#tabs-${id}-${i+1}", tab) }
        }
      }
    }

    createBlock(parent, "pass", writer.toString(), attributes, [:])
}
