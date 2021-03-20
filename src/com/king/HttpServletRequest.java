package com.king;



import java.io.File;
import java.io.InputStream;
import java.net.Socket;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

//标准的J2ee中定诉request的模样
public class HttpServletRequest {
    private InputStream iis;
    private Socket socket;

    private String realPath;
    private String requestURI;
    private String requestURL;
    private String queryString;

    private String method;//方法
    private Map<String, String> headers = new ConcurrentHashMap<String, String>();//头域
    private String uri;//请求的资源地址
    private String protocol;//协议的版本

    private Map<String, String[]> parameterMap = new ConcurrentHashMap<String, String[]>();

    public HttpServletRequest(InputStream iis,Socket socket) {
        this.iis = iis;
        this.socket = socket;
    }

    public void parse() {
        //1.从iis中取出协议 String
        String protocolContent = readProtocolFromInputStream();
        //2.解析
        parseProtocol(protocolContent);
        System.out.println("hello world");

    }

    //解析:请求行,请求头域,实体...参数 存信息到 headers 及其它的域中
    private void parseProtocol(String protocolContent) {
        if (protocolContent == null || "".equals(protocolContent)) {
            return;//TODO: 注意:此时,应通过response生成404响应
        }
        //字符串分隔类:对字符串自动以空格、回车、换行来切割
        StringTokenizer st = new StringTokenizer(protocolContent, "\r\n");//此处指定以\r\n分隔
        int index = 0;//标识第一行
        while (st.hasMoreElements()) {//按行循环
            String line = st.nextToken();//取每一行
            if (index == 0) {//如果是第一行,则解析第一行
                String[] first = line.split(" ");
                this.method = first[0];
                this.uri = first[1];//TODO: 还要做进一步的拆分,以防止有参数的情况
                this.protocol = first[2];

                //解析出realPath
                this.realPath = System.getProperty("user.dir") + File.separator + "webapps" + File.separator + this.uri.split("/")[0];
                this.requestURI = this.uri.split("\\?")[0];
                if ("HTTP/1.1".equals(this.protocol) || "HTTP/1.0".equals(this.protocol)){
                    this.requestURL = "http://" + this.socket.getLocalSocketAddress() + this.requestURI;
                }
                if (this.uri.indexOf("?") >= 0){
                    this.queryString = this.uri.split("\\?")[1];
                    String[] params = this.queryString.split("&");
                    for (int i = 0; i < params.length; i++) {
                        String[] pv = params[i].split("=");
                        if (pv[1].indexOf(",") >= 0){
                            String[] values = pv[1].split(",");
                            this.parameterMap.put(pv[0],values);
                        }else {
                            this.parameterMap.put(pv[0],new String[]{pv[1]});
                        }
                    }
                }
            } else if ("".equals(line)) {
                if ("POST".equals(this.method)) {
                    //以下的数据都是请求实体部分的数据了,比如post的参数
                    parseParams(st);
                }
                break;
            } else {
                String[] heads = line.split(":");
                headers.put(heads[0], heads[1]);
            }
            index++;
        }
    }

    //解析请求的实体参数
    private void parseParams(StringTokenizer st) {
        while (st.hasMoreElements()) {
            String line = st.nextToken();
            String[] params = line.split("&");
            for (int i = 0; i < params.length; i++) {
                String[] pv = params[i].split("=");
                if (pv[1].indexOf(",") >= 0) {
                    String[] values = pv[1].split(",");
                    this.parameterMap.put(pv[0], values);
                } else {
                    this.parameterMap.put(pv[0], new String[]{pv[1]});
                }
            }
        }
    }

    //从iis中取出协议 String
    private String readProtocolFromInputStream() {
        String protocolContent = null;
        //byte[] bs = IOUtil.readFromInputStream(this.iis);
        //protocolContent = new String(bs);
        //System.out.println(protocolContent);
        StringBuffer sb = new StringBuffer(1024 * 30);
        int length = -1;
        byte[] bs = new byte[1024 * 30];
        try {
            length = this.iis.read(bs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < length; i++) {
            sb.append((char) bs[i]);
        }
        protocolContent = sb.toString();
        return protocolContent;

    }

    public Map<String, String[]> getParameterMap() {
        return this.parameterMap;
    }

    public String[] getParameterValues(String key){
        return this.parameterMap.get(key);
    }

    public String getParameter(String key){
        String[] values = getParameterValues(key);
        if (values != null && values.length > 0){
            return values[0];
        }
        return null;
    }

    public String getHeader(String headerName) {
        if (headers != null) {
            return headers.get(headerName);
        }
        return null;
    }

    public InputStream getInputStream() {
        return this.iis;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getMethod() {
        return this.method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getUri() {
        return uri;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public InputStream getIis() {
        return iis;
    }

    public void setIis(InputStream iis) {
        this.iis = iis;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getRealPath() {
        return realPath;
    }

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }

    public String getRequestURL() {
        return requestURL;
    }

    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setParameterMap(Map<String, String[]> parameterMap) {
        this.parameterMap = parameterMap;
    }
}
