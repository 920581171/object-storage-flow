package com.luoyk.osf.demo;

import com.luoyk.osf.core.definition.achieve.AbstractOsf;
import com.luoyk.osf.core.helper.OsfTemplate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

@SpringBootTest
class DemoApplicationTest {

    Logger logger = Logger.getLogger(this.getClass().getName());

    @Autowired
    public OsfTemplate osfTemplate;

    @Test
    public void fileActionTest() throws InterruptedException, FileNotFoundException {

        FileInputStream fileInputStream = new FileInputStream("D:\\test.jpg");

        final String tempId = osfTemplate.file().saveTemp("test.jpg", fileInputStream);
        logger.info("[FileActionTest] tempId is " + tempId);
        final String filePath = osfTemplate.file().transferFile(tempId);
        logger.info("[FileActionTest] filePath is " + filePath);
        final boolean deleted = osfTemplate.file().delete(filePath);
        logger.info("[FileActionTest] delete state is " + deleted);

        for (int i = 0; i < 40; i++) {
            Thread.sleep(1000);
            System.out.println(i);
        }
    }

    @Test
    public void pictureActionTest() throws InterruptedException, FileNotFoundException {

        FileInputStream fileInputStream = new FileInputStream("D:\\test.jpg");

        final String tempId = osfTemplate.picture().saveTemp("test.jpg", fileInputStream);
        logger.info("[PictureActionTest] tempId is " + tempId);
        final String filePath = osfTemplate.picture().transferFile(tempId);
        logger.info("[PictureActionTest] filePath is " + filePath);
        final boolean deleted = osfTemplate.picture().delete(filePath);
        logger.info("[PictureActionTest] delete state is " + deleted);

        for (int i = 0; i < 40; i++) {
            Thread.sleep(1000);
            System.out.println(i);
        }
    }
}