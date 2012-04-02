
package librepdf.document;

import librepdf.Connection;
import org.jruby.Ruby;
import org.jruby.RubyProc;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XServiceInfo;
import com.sun.star.uno.UnoRuntime;

public final class Factory
{
    public static Document factory(Connection connection, Ruby runtime, XComponent document) {
        final XServiceInfo info = (XServiceInfo) UnoRuntime.queryInterface(XServiceInfo.class, document);

        if (info.supportsService("com.sun.star.text.TextDocument")) {
            return new Writer(connection, runtime, document);
        } else if (info.supportsService("com.sun.star.text.WebDocument")) {
            return new Web(connection, runtime, document);
        } else if (info.supportsService("com.sun.star.text.GlobalDocument")) {
            return new Global(connection, runtime, document);
        } else if (info.supportsService("com.sun.star.sheet.SpreadsheetDocument")) {
            return new Calc(connection, runtime, document);
        } else if (info.supportsService("com.sun.star.presentation.PresentationDocument")) {
            return new Impress(connection, runtime, document);
        } else if (info.supportsService("com.sun.star.drawing.DrawingDocument")) {
            return new Draw(connection, runtime, document);
        } else if (info.supportsService("com.sun.star.formula.FormulaProperties")) {
            return new Math(connection, runtime, document);
        }

        throw new RuntimeException("unsupported document type");
    }

    public static Document factory(Connection connection, Ruby runtime, XComponent document, RubyProc proc) {
        final XServiceInfo info = (XServiceInfo) UnoRuntime.queryInterface(XServiceInfo.class, document);

        if (info.supportsService("com.sun.star.text.TextDocument")) {
            return new Writer(connection, runtime, document, proc);
        } else if (info.supportsService("com.sun.star.text.WebDocument")) {
            return new Web(connection, runtime, document, proc);
        } else if (info.supportsService("com.sun.star.text.GlobalDocument")) {
            return new Global(connection, runtime, document, proc);
        } else if (info.supportsService("com.sun.star.sheet.SpreadsheetDocument")) {
            return new Calc(connection, runtime, document, proc);
        } else if (info.supportsService("com.sun.star.presentation.PresentationDocument")) {
            return new Impress(connection, runtime, document, proc);
        } else if (info.supportsService("com.sun.star.drawing.DrawingDocument")) {
            return new Draw(connection, runtime, document, proc);
        } else if (info.supportsService("com.sun.star.formula.FormulaProperties")) {
            return new Math(connection, runtime, document, proc);
        }

        throw new RuntimeException("unsupported document type");
    }

    private Factory() {}
}

