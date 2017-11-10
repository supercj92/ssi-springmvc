package com.cfysu.ssi.util;

import com.cfysu.ssi.model.dto.QRCodeZxingDto;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class UtilTest {

    @Test
    public void testPagination(){

        PaginationInfo paginationInfo = new PaginationInfo(11, 205 ,12);
        System.out.println("print something...");
    }

    @Test
    public void testQRCode(){
        QRCodeZxingDto zxing = new QRCodeZxingDto();
        zxing.setContents("http://www.baidu.com");
        zxing.setCharacterSet("UTF-8");
        zxing.setErrorCorrectionLevel(ErrorCorrectionLevel.H);
        zxing.setFlag(true);
        zxing.setFormat("jpg");
        zxing.setMargin(0);
        zxing.setWidth(300);
        zxing.setHeight(300);
        //zxing.setPath("f:/qrcode/");
        //zxing.setLogoPath("f:/qrcode/3.gif");
        BufferedImage bufferedImage = QRCodeZxingUtil.encode(zxing);
        try {
            ImageIO.write(bufferedImage, "jpg", new File("E:\\zxing.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
