package com.ui.util;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * 思维导图工具类
 * Created by Administrator on 2018-07-18.
 */
public class MindUtil {
    private Document document;
    private Element root;
    private Element rootNode;
    private static final String mindFilePath = "D://testMind.mm";

    /**
     * mind初始化
     */
    public void init(){
        document = DocumentHelper.createDocument();
        root = document.addElement("map"); //根节点
        root.addAttribute("version", "0.9.0");
        rootNode = root.addElement("node");//节点跟目录
        rootNode.addAttribute("TEXT", "Start");
    }

    /**
     * 创建失败的节点
     * @param xpath     指定节点路径
     * @param nodeName  节点名称
     */
    public void createFailNode(String xpath, String nodeName){
        //创建节点
        Element element = getElement(xpath);
        Element node = element.addElement("node"); //子节点
        node.addAttribute("TEXT", nodeName);
        node.addAttribute("POSITION", "right");
        node.addAttribute("COLOR", "#ff3366");
        Element flag = node.addElement("icon"); //子节点
        flag.addAttribute("BUILTIN", "button_cancel");
    }

    /**
     * 创建成功的节点
     * @param xpath     指定节点路径
     * @param nodeName  节点名称
     */
    public void createSuccessNode(String xpath, String nodeName){
        //创建节点
        Element element = getElement(xpath);
        Element node = element.addElement("node"); //子节点
        node.addAttribute("TEXT", nodeName);
        node.addAttribute("POSITION", "right");
        Element flag = node.addElement("icon"); //子节点
        flag.addAttribute("BUILTIN", "button_ok");
    }

    private Element getElement(String xpath){
        Element element = null;
        List<Element> elements = root.selectNodes(xpath);
        //如未找到节点放在跟节点上
        if (elements.isEmpty()){
            element = rootNode;
        }else {
            element = elements.get(0);
        }
        return element;
    }
    /**
     * 写入mind文件，并关闭
     */
    public void closeMind(){
        //用于格式化xml内容和设置头部标签
        OutputFormat xmlFormat = new OutputFormat();
        xmlFormat.setNewlines(true);
        xmlFormat.setIndent(true);
        xmlFormat.setSuppressDeclaration(true);
        Writer out;
        try {
            //创建一个输出流对象
            out = new FileWriter(mindFilePath);
            //创建一个dom4j创建xml的对象
            XMLWriter writer = new XMLWriter(out, xmlFormat);
            //调用write方法将doc文档写到指定路径
            writer.write(document);
            writer.close();
            System.out.print("生成XML文件成功");
        } catch (IOException e) {
            System.out.print("生成XML文件失败");
            e.printStackTrace();
        }
    }
    public static MindUtil getInstance() {
        return MindUtil.MindUtilHolder.instance;
    }

    private static class MindUtilHolder {
        private static MindUtil instance = new MindUtil();
    }
}
