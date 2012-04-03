
package librepdf.document;

import librepdf.Connection;
import librepdf.Utils;

import java.util.Map;
import java.util.HashMap;
import java.io.Closeable;
import java.io.IOException;
import org.jruby.*;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.javasupport.JavaEmbedUtils;

import com.sun.star.beans.PropertyValue;
import com.sun.star.frame.XStorable;
import com.sun.star.util.XCloseable;
import com.sun.star.util.CloseVetoException;
import com.sun.star.task.ErrorCodeIOException;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XServiceInfo;
import com.sun.star.uno.UnoRuntime;

public abstract class Document implements Closeable, Finalizable
{
    private final Connection    connection;
    private final Ruby          runtime;
    private final XComponent    document;

    private boolean closed = false;

    Document(Connection connection, Ruby runtime, XComponent document) {
        this.connection = connection;
        this.runtime    = runtime;
        this.document   = document;
        this.runtime.addInternalFinalizer(this);
    }

    Document(Connection connection, Ruby runtime, XComponent document, RubyProc proc) {
        this(connection, runtime, document);

        final ThreadContext context = this.runtime.getCurrentContext();
        final IRubyObject[] args = { JavaEmbedUtils.javaToRuby(this.runtime, this) };

        try {
            proc.call(context, args);
        } finally {
            try {
                this.close();
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void finalize() throws Throwable {
        try {
            close();
        } catch (IOException e) {}
    }

    @Override
    public void close() throws IOException {
        if (!this.closed) {
            final XCloseable closeable = (XCloseable) UnoRuntime.queryInterface(XCloseable.class, document);
            try {
                closeable.close(false);
            } catch (CloseVetoException e) {
                throw new IOException("chould not close document.", e);
            }
            this.runtime.removeInternalFinalizer(this);
            this.closed = true;
        }
    }
 
    public void convertPdf(String outputUrl) {
        this.convertPdf(outputUrl, new HashMap<String, Object>());
    }

    public void convertPdf(String outputUrl, Map<String, Object> options) {
        if (outputUrl == null) {
            throw new IllegalArgumentException("Null connot be set for outputUrl.");
        }
        if (this.connection.isClosed()) {
            throw new IllegalArgumentException("The connection has not established it.");
        }

        final XStorable storable = (XStorable) UnoRuntime.queryInterface(XStorable.class, document);
 
        if (!options.containsKey("FilterData")) options.put("FilterData", new HashMap<String, Object>());
        this.setDefaultOptions(options);
        final PropertyValue[] outputProperties = Utils.toPropertyValues(options);

        try {
            storable.storeToURL(outputUrl, outputProperties);

        } catch (ErrorCodeIOException e) {
            throw new RuntimeException("could not save output document. " + e.ErrCode, e);

        } catch (Exception e) {
            throw new RuntimeException("could not save output document", e);

        }
    }

    @Override
    public String toString() {
        return "#<Librepdf::Document::" + this.getClass().getSimpleName() + ">";
    }

    public boolean isClosed() {
        return this.closed;
    }

    public String inspect() {
        return this.toString();
    }

    protected void setDefaultOptions(Map<String, Object> options) {
        // do nothing.
    }
}

