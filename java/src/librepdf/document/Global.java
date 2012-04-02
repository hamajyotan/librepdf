
package librepdf.document;

import librepdf.Connection;
import java.util.Map;
import org.jruby.Ruby;
import org.jruby.RubyProc;
import com.sun.star.lang.XComponent;

class Global extends Document
{
    Global(Connection connection, Ruby runtime, XComponent document) {
        super(connection, runtime, document);
    }

    Global(Connection connection, Ruby runtime, XComponent document, RubyProc proc) {
        super(connection, runtime, document, proc);
    }

    @Override
    protected void setDefaultOptions(Map<String, Object> options) {
        options.put("FilterName", "writer_globaldocument_pdf_Export");
    }
}

