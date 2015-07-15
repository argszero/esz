package com.github.esz.sdk;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by shaoaq on 7/15/15.
 */
@Service
public class ApiService {
    @Value("${api.dir}")
    private String uploadDir;
    @Autowired
    private AutowireCapableBeanFactory beanFactory;
    private Map<String, ApiImpl> apiMap = new HashMap<>();


    @PostConstruct
    private void init() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        File rootDir = new File(uploadDir);
        rootDir.mkdirs();
        for (File userDir : rootDir.listFiles()) {
            if (userDir.isDirectory()) {
                String user = userDir.getName();
                for (File jar : userDir.listFiles()) {
                    if (jar.isFile()) {
                        URLClassLoader loader = new URLClassLoader(new URL[]{jar.toURI().toURL()}, getClass().getClassLoader());
                        ZipInputStream zip = new ZipInputStream(new FileInputStream(jar));
                        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
                            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                                String className = entry.getName().replace('/', '.');
                                className = className.substring(0, className.length() - ".class".length());
                                System.out.println(className);
                                Class<?> clz = loader.loadClass(className);
                                if (clz.isAnnotationPresent(Api.class)) {
                                    Object instance = clz.newInstance();
                                    beanFactory.autowireBean(instance);
                                    System.out.println(instance.toString());
                                    Api classApi = clz.getAnnotation(Api.class);
                                    String classPath = classApi.value();
                                    if (!StringUtils.isEmpty(classPath)) {
                                        classPath = "/" + classPath;
                                    }
                                    Method[] declaredMethods = clz.getDeclaredMethods();
                                    for (Method method : declaredMethods) {
                                        if (method.isAnnotationPresent(Api.class)) {
                                            Api api = method.getAnnotation(Api.class);
                                            ApiImpl apiImpl = new ApiImpl(method, loader, jar, api, user, instance);
                                            String path = api.value();
                                            if (StringUtils.isEmpty(path)) {
                                                path = method.getName();
                                            }
                                            synchronized (apiMap) {
                                                apiMap.put("/api/" + user + classPath + "/" + path, apiImpl);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public JsonNode invoke(String servletPath, ObjectNode node) throws InvocationTargetException, IllegalAccessException {
        ApiImpl impl = apiMap.get(servletPath);
        Method method = impl.method;
        JsonNode result = (JsonNode) method.invoke(impl.instance, node);
        return result;
    }

    private static class ApiImpl {
        private final Method method;
        private final ClassLoader classLoader;
        private final File jar;
        private final Api annotation;
        private final String user;
        private Object instance;

        public ApiImpl(Method method, ClassLoader classLoader, File jar, Api annotation, String user, Object instance) {
            this.method = method;
            this.classLoader = classLoader;
            this.jar = jar;
            this.annotation = annotation;
            this.user = user;
            this.instance = instance;
        }
    }


}
