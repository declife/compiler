package com.liu.test.compile.service;

import com.liu.test.compile.JavaStringCompiler;
import com.liu.test.compile.on.DynamicInterface;
import com.liu.test.controller.SpringUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class service {
    private final String code = "package com.liu.test.compile.service;\n" +
            "\n" +
            "import com.alibaba.fastjson.JSONObject;import com.liu.test.compile.on.DynamicInterface;\n" +
            "\n" +
            "public class DynamicClass implements DynamicInterface { \n" +
            "\n" +
            "       @Override                                        \n" +
            "        public String hello() {                          \n" +
            "            return \"Hello,Liu\";                        \n" +
            "        }                                                 \n" +
            "}";

    public String hello() {
        JavaStringCompiler compiler = new JavaStringCompiler();
        try {
            Map<String,byte[]> classBytes = compiler.compile("DynamicClass.java", code);
            System.out.println("编译过了");
            Class<?> clazz = compiler.loadClass("com.liu.test.compile.service.DynamicClass", classBytes);
            DynamicInterface dynamicInterface = (DynamicInterface) (clazz.getDeclaredConstructor().newInstance());
            return dynamicInterface.hello();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
