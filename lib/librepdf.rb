require "librepdf/version"

require 'java'

module Librepdf
  require 'librepdf/java/juh'
  require 'librepdf/java/jurt'
  require 'librepdf/java/ridl'
  require 'librepdf/java/unoil'
  require 'librepdf/java/librepdf'
 
  import 'librepdf.Connection'
  import 'librepdf.document.Document'
  class Document
    import 'librepdf.document.Calc'
    import 'librepdf.document.Chart'
    import 'librepdf.document.Draw'
    import 'librepdf.document.Global'
    import 'librepdf.document.Impress'
    import 'librepdf.document.Math'
    import 'librepdf.document.Web'
    import 'librepdf.document.Writer'
  end
end

