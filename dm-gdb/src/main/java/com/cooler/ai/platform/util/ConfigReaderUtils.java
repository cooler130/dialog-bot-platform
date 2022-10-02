package com.cooler.ai.platform.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class ConfigReaderUtils {

    private static Logger logger = LoggerFactory.getLogger(ConfigReaderUtils.class);

    public static String readConfig(String fileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)))) {
            return readConfig(reader);
        } catch (Exception e) {
            logger.error("配置文件读取错误. path:{}", fileName, e);
            throw new RuntimeException(e);
        }
    }

    public static String readConfig(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            return readConfig(reader);
        } catch (Exception e) {
            logger.error("配置文件流读取错误", e);
            throw new RuntimeException(e);
        }
    }

    private static String readConfig(BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }
}