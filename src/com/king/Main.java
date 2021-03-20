package com.king;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Main {
    //TODO: 日志的创建
    private static Logger logger = Logger.getLogger(Main.class.getName());
    static {
        String userDir = System.getProperty("user.dir") + File.separator + "conf" + File.separator + "log4j.propreties";
        PropertyConfigurator.configure(userDir);
    }





    public static void main(String[] args) {
        Map<String, String> xmlPros = initXml();
        try (ServerSocket ss = new ServerSocket(Integer.parseInt(xmlPros.get("port")));) {
            logger.info(ss.getInetAddress() + "正常启动，监听" + ss.getLocalPort() + "端口");
            while (true) {
                Socket s = ss.accept();
                logger.info(s.getRemoteSocketAddress() + "联接到服务器");
                //TODO：考虑线程和普通线程
                Thread t = new Thread(new NetTask(s));
                t.setDaemon(true);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }
    }

    /**
     * 读取xml文件．
     *
     * @return
     */
    private static Map<String, String> initXml() {
        Map<String, String> xmlPros = new HashMap<String, String>();
        //TODO：读取ｘｍｌ
        //     dom, sax
        //   ***  dom4j   jdom
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse("conf/server.xml");

            NodeList nl = doc.getElementsByTagName("Server");
            for (int i = 0; i < nl.getLength(); i++) {
                Element node = (Element) nl.item(i);

                String value = node.getAttribute("shutdown");
                if ("SHUTDOWN".equals(value)) {
                    String port = node.getAttribute("port");
                    xmlPros.put("shutdown_port", port);
                }

                NodeList nls = node.getElementsByTagName("Connector");
                for (int j = 0; j < nls.getLength(); j++) {
                    Element node2 = (Element) nls.item(j);
                    String port = "9090";
                    String threadpool = "false";
                    if (node2.getAttribute("port") != null) {
                        port = node2.getAttribute("port");
                    }
                    if (node2.getAttribute("threadpool") != null) {
                        threadpool = node2.getAttribute("threadpool");
                    }
                    xmlPros.put("port", port);
                    xmlPros.put("threadpool", threadpool);
                    System.out.println(xmlPros);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }


        return xmlPros;
    }
}
