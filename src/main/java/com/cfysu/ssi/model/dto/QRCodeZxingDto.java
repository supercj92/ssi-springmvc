package com.cfysu.ssi.model.dto;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.Serializable;

public class QRCodeZxingDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 二维码内容
     */
    private String contents;
    /**
     * 图片的宽度
     */
    private int width;
    /**
     * 图片的高度
     */
    private int height;
    /**
     * 生成图片的地址（不包含图片名称）
     */
    private String path;
    /**
     * logo图地址
     */
    private String logoPath;
    /**
     * 生成图片的格式
     */
    private String format;
    /**
     * 纠错级别
     */
    private Object errorCorrectionLevel = ErrorCorrectionLevel.H;
    /**
     * 编码格式
     */
    private String characterSet="UTF-8";
    /**
     * 二维码边缘留白
     */
    private int margin;
    /**
     * 是否中间贴图
     * @return
     */
    private boolean flag;

    /**getter and setter*/

    public String getContents() {
        return contents;
    }
    public void setContents(String contents) {
        this.contents = contents;
    }
    public int getWidth() {
        return width;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getFormat() {
        return format;
    }
    public void setFormat(String format) {
        this.format = format;
    }
    public Object getErrorCorrectionLevel() {
        return errorCorrectionLevel;
    }
    public void setErrorCorrectionLevel(Object errorCorrectionLevel) {
        this.errorCorrectionLevel = errorCorrectionLevel;
    }
    public String getCharacterSet() {
        return characterSet;
    }
    public void setCharacterSet(String characterSet) {
        this.characterSet = characterSet;
    }
    public int getMargin() {
        return margin;
    }
    public void setMargin(int margin) {
        this.margin = margin;
    }
    public boolean isFlag() {
        return flag;
    }
    public void setFlag(boolean flag) {
        this.flag = flag;
    }
    public String getLogoPath() {
        return logoPath;
    }
    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }
}
