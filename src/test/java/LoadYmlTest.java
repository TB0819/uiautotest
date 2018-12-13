import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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

    @Test
    public void test_quchong(){
        List<String> list1 = Lists.newArrayList("a","b","c");
        List<String> list2 = Lists.newArrayList("b","c","d");

        List<String> list3 = Lists.newArrayList(Sets.difference(Sets.newHashSet(list1), Sets.newHashSet(list2)));

        System.out.println(list3);
    }
}
