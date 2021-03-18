package com.cfysu.ssi.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.document.AbstractPdfView;

/**
 * @Author canglong
 * @Date 2020/8/26
 */
public class PDFView extends AbstractPdfView {

    @Override
    protected void buildPdfDocument(Map<String, Object> model, com.lowagie.text.Document document,
        com.lowagie.text.pdf.PdfWriter writer, HttpServletRequest request, HttpServletResponse response)
        throws Exception {
        document.addTitle("test pdf");
    }
}
