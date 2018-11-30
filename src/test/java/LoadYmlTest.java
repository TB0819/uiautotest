import com.ui.entity.ComConstant;
import com.ui.entity.Config;
import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class LoadYmlTest {
    @Test
    public void test_loadYml() throws FileNotFoundException {
        Yaml yaml = new Yaml();
        Config config = yaml.loadAs(new FileInputStream(new File(ComConstant.CONFIG_PATH)), Config.class);
        System.err.println(config);
    }

    @Test
    public void test_listStream(){
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        list.stream().forEach(p ->{
            if (p.equals("b")){
                System.err.println(p);
            }
        });
    }
}
