
package librepdf.document;

import librepdf.Connection;
import java.util.Map;
import org.jruby.Ruby;
import org.jruby.RubyProc;
import com.sun.star.lang.XComponent;

class Calc extends Document
{
    Calc(Connection connection, Ruby runtime, XComponent document) {
        super(connection, runtime, document);
    }

    Calc(Connection connection, Ruby runtime, XComponent document, RubyProc proc) {
        super(connection, runtime, document, proc);
    }

    @Override
    protected void setDefaultOptions(Map<String, Object> options) {
        options.put("FilterName", "calc_pdf_Export");
    }
}

