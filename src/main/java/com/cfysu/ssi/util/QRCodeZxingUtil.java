package com.cfysu.ssi.util;

import com.cfysu.ssi.model.dto.QRCodeZxingDto;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

public class QRCodeZxingUtil {
    private static final int BLACK = -16777216;
    private static final int WHITE = -1;

    public static BufferedImage encode(QRCodeZxingDto zxing) {
        BufferedImage bi = null;
        try {
            Hashtable<EncodeHintType, Object> hints = new Hashtable();

            hints.put(EncodeHintType.ERROR_CORRECTION, zxing.getErrorCorrectionLevel());

            hints.put(EncodeHintType.CHARACTER_SET, zxing.getCharacterSet());

            hints.put(EncodeHintType.MARGIN, Integer.valueOf(zxing.getMargin()));
            BitMatrix bitMatrix = new MultiFormatWriter().encode(zxing.getContents(), BarcodeFormat.QR_CODE, zxing.getWidth(), zxing.getHeight(), hints);
            bi = writeToResponse(bitMatrix, zxing.getFormat(), zxing);
        } catch (Exception e) {
            System.out.println("[store-jar] [QRCodeZxingUtil] [encode] " + e);
        }
        return bi;
    }

    public static BufferedImage writeToResponse(BitMatrix bitMatrix, String format, QRCodeZxingDto zxing)
    {
        BufferedImage bi = toBufferedImageContents(bitMatrix);
        boolean isLogo = zxing.isFlag();
        if (isLogo)
        {
            int width_4 = bitMatrix.getWidth() / 4;
            int width_8 = width_4 / 2;
            int height_4 = bitMatrix.getHeight() / 4;
            int height_8 = height_4 / 2;

            BufferedImage bi2 = bi.getSubimage(width_4 + width_8, height_4 + height_8, width_4, height_4);

            Graphics2D g2 = bi2.createGraphics();
            try
            {
                URL url = new URL(zxing.getLogoPath());
                try
                {
                    Image img = ImageIO.read(url);

                    int currentImgWidth = img.getWidth(null);
                    int currentImgHeight = img.getWidth(null);

                    int resultImgWidth = 0;
                    int resultImgHeight = 0;
                    if (currentImgWidth != width_4) {
                        resultImgWidth = width_4;
                    }
                    if (currentImgHeight != width_4) {
                        resultImgHeight = width_4;
                    }
                    g2.drawImage(img, 0, 0, resultImgWidth, resultImgHeight, null);
                    g2.dispose();
                    bi.flush();
                }
                catch (IOException e)
                {
                    System.out.println("[store-jar] ImageIO.read：：：：" + e);
                }
            }
            catch (MalformedURLException e)
            {
                System.out.println("[store-jar] MalformedURLException：：：：" + e);
            }
        }
        return bi;
    }

    public static BufferedImage toBufferedImageContents(BitMatrix bitMatrix)
    {
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, 1);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) == true ? -16777216 : -1);
            }
        }
        return image;
    }
}