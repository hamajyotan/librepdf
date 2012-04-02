
package librepdf;

import librepdf.document.Factory;
import librepdf.document.Document;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.net.ConnectException;
import org.jruby.*;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.javasupport.JavaEmbedUtils;

import com.sun.star.beans.XPropertySet;
import com.sun.star.beans.PropertyValue;
import com.sun.star.bridge.XBridge;
import com.sun.star.bridge.XBridgeFactory;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.connection.NoConnectException;
import com.sun.star.connection.XConnection;
import com.sun.star.connection.XConnector;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.frame.XStorable;
import com.sun.star.lang.EventObject;
import com.sun.star.lang.XComponent;
import com.sun.star.lang.XEventListener;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.task.ErrorCodeIOException;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;

public final class Connection implements Closeable, Finalizable
{
    public static String DEFAULT_HOST = "127.0.0.1";
    public static int    DEFAULT_PORT = 8100;
  
    private final Ruby   runtime;
    private final String host;
    private final int    port;
  
    private boolean closed = false;

    private XComponent             bridgeComponent;
    private XComponentContext      componentContext;
    private XMultiComponentFactory serviceManager;

    public Connection() throws ConnectException {
        this.runtime = Ruby.getGlobalRuntime();
        this.runtime.addInternalFinalizer(this);
        this.host = DEFAULT_HOST;
        this.port = DEFAULT_PORT;
        this.open();
    }

    public Connection(RubyProc proc) throws ConnectException {
        this();

        final ThreadContext context = this.runtime.getCurrentContext();
        final IRubyObject[] args = { JavaEmbedUtils.javaToRuby(this.runtime, this) };

        this.open();
        try {
            proc.call(context, args);
        } finally {
            try {
                this.close();
            } catch (Exception e) {
            }
        }
    }

    public Connection(Map<String, Object> options) throws ConnectException {
        this.runtime = Ruby.getGlobalRuntime();
        this.runtime.addInternalFinalizer(this);

        if (options.containsKey("host")) {
            this.host = options.get("host").toString();
        } else {
            this.host = DEFAULT_HOST;
        }
        if (options.containsKey("port")) {
            final Object p = options.get("port");
            if (!(p instanceof Long)) {
                throw new IllegalArgumentException("The port required the number between 0-65535."); 
            }

            final Long l = (Long)p;
            if (!(0 < l && l < 65536)) {
                throw new IllegalArgumentException("The port required the number between 0-65535."); 
            }
            this.port = l.intValue();
        } else {
            this.port = DEFAULT_PORT;
        }

        this.open();
    }

    public Connection(Map<String, Object> options, RubyProc proc) throws ConnectException {
        this(options);

        final ThreadContext context = this.runtime.getCurrentContext();
        final IRubyObject[] args = { JavaEmbedUtils.javaToRuby(this.runtime, this) };

        this.open();
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
        } catch(IOException e) {}
    }

    @Override
    public synchronized void close() throws IOException {
        if (!this.closed) {
            this.bridgeComponent.dispose();
            this.runtime.removeInternalFinalizer(this);
            this.closed = true;
        }
    }

    @Override
    public String toString() {
        return "#<Librepdf::Connection host=" + this.host + ",port=" + this.port + ">";
    }

    public boolean isClosed() {
        return this.closed;
    }

    public String inspect() {
        return this.toString();
    }

    private synchronized void open() throws ConnectException {
        final String connectionString = "socket,host=" + this.host + ",port=" + this.port + ",tcpNoDelay=1";

        try {
            final XComponentContext localContext = Bootstrap.createInitialComponentContext(null);
            final XMultiComponentFactory localServiceManager = localContext.getServiceManager();
            final XConnector connector = (XConnector) UnoRuntime.queryInterface(XConnector.class, localServiceManager
                    .createInstanceWithContext("com.sun.star.connection.Connector", localContext));
            final XConnection connection = connector.connect(connectionString);
            final XBridgeFactory bridgeFactory = (XBridgeFactory) UnoRuntime.queryInterface(XBridgeFactory.class,
                    localServiceManager.createInstanceWithContext("com.sun.star.bridge.BridgeFactory", localContext));
            final XBridge bridge = bridgeFactory.createBridge("", "urp", connection, null);

            this.bridgeComponent = (XComponent) UnoRuntime.queryInterface(XComponent.class, bridge);
            this.serviceManager = (XMultiComponentFactory) UnoRuntime.queryInterface(XMultiComponentFactory.class,
                    bridge.getInstance("StarOffice.ServiceManager"));

            final XPropertySet properties = (XPropertySet) UnoRuntime.queryInterface(XPropertySet.class,
                    this.serviceManager);
            this.componentContext = (XComponentContext) UnoRuntime.queryInterface(XComponentContext.class, properties
                    .getPropertyValue("DefaultContext"));

        } catch (Exception e) {
            throw new ConnectException("connection failed: " + connectionString + ": " + e.getMessage());
        }
    }

    public Document load(String inputUrl) {
        return this.load(inputUrl, new HashMap<String, Object>());
    }

    public Document load(String inputUrl, Map<String, Object> options) {
        if (inputUrl == null) {
            throw new IllegalArgumentException("Null connot be set for inputUrl.");
        }
        if (this.closed) {
            throw new IllegalArgumentException("The connection has not established it.");
        }

        options.put("Hidden", true);
        options.put("ReadOnly", true);
        final PropertyValue[] inputProperties = Utils.toPropertyValues(options);

        final XComponent document = this.loadInternal(inputUrl, inputProperties);

        return Factory.factory(this, this.runtime, document);
    }

    public Document load(String inputUrl, RubyProc proc) {
        return this.load(inputUrl, new HashMap<String, Object>(), proc);
    }

    public Document load(String inputUrl, Map<String, Object> options, RubyProc proc) {
        if (inputUrl == null) {
            throw new IllegalArgumentException("Null connot be set for inputUrl.");
        }
        if (this.closed) {
            throw new IllegalArgumentException("The connection has not established it.");
        }

        options.put("Hidden", true);
        options.put("ReadOnly", true);
        final PropertyValue[] inputProperties = Utils.toPropertyValues(options);

        final XComponent document = this.loadInternal(inputUrl, inputProperties);

        return Factory.factory(this, this.runtime, document, proc);
    }

    public XComponentLoader getDesktop() {
        return (XComponentLoader) UnoRuntime.queryInterface(XComponentLoader.class,
                getService("com.sun.star.frame.Desktop"));
    }

    public Object getService(String className) {
        if (className == null) {
            throw new IllegalArgumentException("Null connot be set for className.");
        }

        try {
            return this.serviceManager.createInstanceWithContext(className, this.componentContext);

        } catch (Exception e) {
            throw new RuntimeException("could not obtain service: " + className, e);
        }
    }

    private XComponent loadInternal(String inputUrl, PropertyValue[] properties) {
        final XComponentLoader desktop = this.getDesktop();
        try {
            return desktop.loadComponentFromURL(inputUrl, "_blank", 0, properties);

        } catch (ErrorCodeIOException e) {
            throw new RuntimeException("could not load input document. " + e.ErrCode, e);

        } catch (Exception e) {
            throw new RuntimeException("could not load input document.", e);

        }
    }
}

