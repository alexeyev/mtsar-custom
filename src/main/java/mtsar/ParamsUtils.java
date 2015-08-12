package mtsar;

import org.apache.commons.collections4.CollectionUtils;

import javax.ws.rs.core.MultivaluedMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public final class ParamsUtils {
    public final static Set<String> extract(MultivaluedMap<String, String> params, String prefix) {
        final String regexp = "^" + Pattern.quote(prefix) + "(\\[\\d+\\]|)$";
        final Set<String> values = new HashSet<>();
        for (final Map.Entry<String, List<String>> entries : params.entrySet()) {
            if (!entries.getKey().matches(regexp)) continue;
            if (CollectionUtils.isEmpty(entries.getValue())) continue;
            for (final String answer : entries.getValue()) values.add(answer);
        }
        return values;
    }
}
