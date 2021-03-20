package com.king;



import java.io.*;

public class HttpServletResponse {
    private OutputStream oos;
    private HttpServletRequest request;

    public HttpServletResponse(OutputStream oos, HttpServletRequest request) {
        this.oos = oos;
        this.request = request;
    }

    /**
     * 拼接响应
     */
    public void sendRedirect() {
        String responseprotocol = null;//响应协议
        byte[] fileContent = null;//响应资源的内容
        String uri = request.getRequestURI();//请求的资源路径
        System.out.println("uri:"+uri);
        System.out.println("request.getRealPath():"+request.getRealPath());
        File f = new File(request.getRealPath(), uri);
        System.out.println("-----------" + f);
        if (!f.exists()) {
            //文件不存在,则回送404协议

            File file404 = new File(request.getRealPath(), "404.html");
            fileContent = readFile(file404);
            responseprotocol = gen404(file404, fileContent);
        } else {
            //存在文件,则读取文件
            fileContent = readFile(f);
            responseprotocol = gen200(f, fileContent);
        }
        try {
            //以输出 流输出 数据到客户端
            this.oos.write(responseprotocol.getBytes());//以输出响应协议的头部
            this.oos.flush();
            this.oos.write(fileContent);
            this.oos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (this.oos != null) {
                try {
                    this.oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public byte[] readFile(File file) {
        byte[] bs = null;
        InputStream iis = null;
        try {
            iis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bs = IoUtil.readFromInputStream(iis);
        return bs;
    }

    public String gen200(File file, byte[] fileContent) {
        String result = null;

        String uri = this.request.getRequestURI();
        int index = uri.lastIndexOf(".");
        if (index >= 0) {
            index += +1;
        }
        String fileExtension = uri.substring(index);
        if ("JPG".equalsIgnoreCase(fileExtension) || "JPEG".equalsIgnoreCase(fileExtension)) {
            result = "HTTP/1.1 200\r\nAccept-Ranges: bytes\r\nContent-Type: image/jpeg\r\nContent-Length: " + fileContent.length + "\r\n\r\n";
        } else if ("PNG".equalsIgnoreCase(fileExtension)) {
            result = "HTTP/1.1 200\r\nAccept-Ranges: bytes\r\nContent-Type: image/png\r\nContent-Length: " + fileContent.length + "\r\n\r\n";
        } else if ("json".equalsIgnoreCase(fileExtension)) {
            result = "HTTP/1.1 200\r\nAccept-Ranges: bytes\r\nContent-Type: application/json\r\nContent-Length: " + fileContent.length + "\r\n\r\n";
        } else if ("css".equalsIgnoreCase(fileExtension)) {
            result = "HTTP/1.1 200\r\nAccept-Ranges: bytes\r\nContent-Type: text/css\r\nContent-Length: " + fileContent.length + "\r\n\r\n";
        } else if ("js".equalsIgnoreCase(fileExtension)) {
            result = "HTTP/1.1 200\r\nAccept-Ranges: bytes\r\nContent-Type: application/javascript\r\nContent-Length: " + fileContent.length + "\r\n\r\n";
        } else {
            result = "HTTP/1.1 200\r\nAccept-Ranges: bytes\r\nContent-Type: text/html\r\nContent-Length: " + fileContent.length + "\r\n\r\n";
        }

        return result;
    }

    public String gen404(File file, byte[] fileContent) {
        String result = null;
        result = "HTTP/1.1 404\r\nAccept-Ranges: bytes\r\nContent-Type: text/html\r\nContent-Length: " + fileContent.length + "\r\n\r\n";
        return result;
    }

}
