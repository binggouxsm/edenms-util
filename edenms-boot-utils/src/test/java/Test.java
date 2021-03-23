import com.eden.msutils.boot.handler.GlobalResponseHandler;
import org.springframework.util.AntPathMatcher;

public class Test {
    public static void main(String[] args) {
        Class clazz = GlobalResponseHandler.class;
        System.out.println(clazz.getName());

        String basePackage = "com.eden.msutils.boot";
        String antPattern = basePackage+".**";
        System.out.println(new AntPathMatcher(".").match(antPattern, clazz.getName()));

    }
}
