
docinfo_processor { document ->
  def attrs = document.attributes.findAll { key, value ->
    String val = value.toString()
    val.startsWith('{{') && val.endsWith('}}')
  }.keySet()

  if (!attrs.empty) {
    """<script>
  var attributes = [${attrs.collect { "'${it}'" }.join(', ')}];
</script>"""
  }
}

