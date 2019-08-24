import java.util.List;

public class StringUtils {
    public static String join(List<String> target, String join) {
        if(target.isEmpty())
            return "";
        else {
            String result = target.get(0);
            for(int i = 1; i < target.size(); i++) {
                String next = target.get(i);
                result = result.concat(join).concat(next);
            }
            return result;
        }
    }

    public static String setPropertiesToNull(List<String> properties) {
        if(properties.isEmpty())
            return "";
        else {
            String result = properties.get(0).concat("=NULL");
            for(int i = 1; i < properties.size(); i++) {
                String next = properties.get(i);
                result = result.concat(",").concat(next).concat("=NULL");
            }
            return result;
        }
    }

    public static String setPropertiesToValues(List<String> properties, List<String> values) {
        if(properties.isEmpty())
            return "";
        else {
            String result = properties.get(0).concat("=").concat(values.get(0));
            for(int i = 1; i < properties.size(); i++) {
                String nextProperty = properties.get(i);
                String nextValue = values.get(i);
                result = result.concat(",").concat(nextProperty).concat("=").concat(nextValue);
            }
            return result;
        }
    }
}
