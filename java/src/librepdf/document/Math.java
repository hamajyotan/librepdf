
package librepdf.document;

import librepdf.Connection;
import java.util.Map;
import org.jruby.Ruby;
import org.jruby.RubyProc;
import com.sun.star.lang.XComponent;

class Math extends Document
{
    Math(Connection connection, Ruby runtime, XComponent document) {
        super(connection, runtime, document);
    }

    Math(Connection connection, Ruby runtime, XComponent document, RubyProc proc) {
        super(connection, runtime, document, proc);
    }

    @Override
    protected void setDefaultOptions(Map<String, Object> options) {
        options.put("FilterName", "math_pdf_Export");
    }
}

