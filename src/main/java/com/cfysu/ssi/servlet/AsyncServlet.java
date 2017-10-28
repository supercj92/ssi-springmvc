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
        response.setCharacterEncoding("utf-8");
        final PrintWriter writer = response.getWriter();
        writer.println("老板布置任务...");
        //writer.flush();
        final AsyncContext asyncContext = request.startAsync();
        asyncContext.setTimeout(1000*60*60);//1个小时
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(3000L);
                    System.out.println("---async res start---");
                    PrintWriter w = asyncContext.getResponse().getWriter();
                    w.println("working...");
                    //w.flush();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                asyncContext.complete();
            }
        }).start();
//        asyncContext.start(new Runnable() {
//            public void run() {
//                PrintWriter asyncWriter = null;
//                try {
//                    asyncWriter = asyncContext.getResponse().getWriter();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    for (int i =0;i< 5;i++){
//                        Thread.sleep(1000L);
//                        if(asyncWriter != null){
//                            asyncWriter.println(i+ "任务处理中...");
//                            asyncWriter.flush();
//                        }
//                    }
//                    asyncContext.complete();
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }
//        });
        writer.println("老板离开...");
        //writer.flush();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}
