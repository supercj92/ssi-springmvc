package com.cfysu.ssi.servlet;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by cj on 17-9-17.
 */
public class AsyncServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final PrintWriter writer = response.getWriter();
        writer.println("老板布置任务...");
        AsyncContext asyncContext = request.startAsync();
        asyncContext.setTimeout(1000*10L);
        asyncContext.start(new Runnable() {
            public void run() {
                try {
                    for (int i =0;i< 5;i++){
                        Thread.sleep(1000L);
                        writer.println(i+ "任务处理中...");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        writer.println("老板离开...");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
