
package librepdf.document;

import librepdf.Connection;
import java.util.Map;
import org.jruby.Ruby;
import org.jruby.RubyProc;
import com.sun.star.lang.XComponent;

class Draw extends Document
{
    Draw(Connection connection, Ruby runtime, XComponent document) {
        super(connection, runtime, document);
    }

    Draw(Connection connection, Ruby runtime, XComponent document, RubyProc proc) {
        super(connection, runtime, document, proc);
    }

    @Override
    protected void setDefaultOptions(Map<String, Object> options) {
        options.put("FilterName", "draw_pdf_Export");
    }
}

