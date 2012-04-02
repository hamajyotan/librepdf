
package librepdf;

import java.util.Map;
import java.util.Iterator;
import com.sun.star.beans.PropertyValue;

public final class Utils
{
    public static PropertyValue[] toPropertyValues(Map properties) {
        final PropertyValue[] ret = new PropertyValue[properties.size()];
        int i = 0;
        for (Iterator it = properties.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry)it.next();
            String key = entry.getKey().toString();
            Object val = entry.getValue();

            ret[i]       = new PropertyValue();
            ret[i].Name  = key;
            ret[i].Value = (val instanceof Map) ? Utils.toPropertyValues((Map)val) : val;

            i++;
        }
        return ret;
    }

    private Utils() {}
}

