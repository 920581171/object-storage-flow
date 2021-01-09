package com.luoyk.osf.demo;

import com.luoyk.osf.core.definition.AbstractOsf;
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
    public AbstractOsf abstractOsf;

    @Test
    public void fileActionTest() throws InterruptedException, FileNotFoundException {

        FileInputStream fileInputStream = new FileInputStream("D:\\test.jpg");

        final String tempId = abstractOsf.getFileAction().saveTemp("test.jpg", fileInputStream);
        logger.info("[FileActionTest] tempId is " + tempId);
        final String filePath = abstractOsf.getFileAction().transferFile(tempId);
        logger.info("[FileActionTest] filePath is " + filePath);
        final boolean deleted = abstractOsf.getFileAction().delete(filePath);
        logger.info("[FileActionTest] delete state is " + deleted);

        for (int i = 0; i < 40; i++) {
            Thread.sleep(1000);
            System.out.println(i);
        }
    }

    @Test
    public void pictureActionTest() throws InterruptedException, FileNotFoundException {

        FileInputStream fileInputStream = new FileInputStream("D:\\test.jpg");

        final String tempId = abstractOsf.getPictureAction().saveTemp("test.jpg", fileInputStream);
        logger.info("[PictureActionTest] tempId is " + tempId);
        final String filePath = abstractOsf.getPictureAction().transferFile(tempId);
        logger.info("[PictureActionTest] filePath is " + filePath);
        final boolean deleted = abstractOsf.getPictureAction().delete(filePath);
        logger.info("[PictureActionTest] delete state is " + deleted);

        for (int i = 0; i < 40; i++) {
            Thread.sleep(1000);
            System.out.println(i);
        }
    }
}