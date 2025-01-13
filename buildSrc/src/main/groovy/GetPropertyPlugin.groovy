import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 *
 * @author ziy
 * @date 2025-01
 */
class GetPropertyPlugin implements Plugin<Project> {
    @Override
    void apply(Project target) {
        target.extensions.extraProperties.set("findProp", (String prop, String defProp) -> {
            def value = getProperty(prop)
            if (value == null && defProp != null) {
                value = getProperty(defProp)
            }
            return value;
        })
    }
}
