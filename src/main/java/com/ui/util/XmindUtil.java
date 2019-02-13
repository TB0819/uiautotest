package com.ui.util;

import com.ui.entity.ComConstant;
import org.xmind.core.*;

import java.io.IOException;

public class XmindUtil {
    private ITopic rootTopic;
    private IWorkbook workbook;
    public String rootTopicId;

    public static XmindUtil getInstance(){
        return XmindUtilHolder.instance;
    }

    private static class XmindUtilHolder {
        private static XmindUtil instance = new XmindUtil();
    }

    /**
     * 创建根节点
     */
    public void createWorkBook(String text){
        IWorkbookBuilder builder = Core.getWorkbookBuilder();
        workbook = builder.createWorkbook();
        //创建主题
        ISheet defSheet = workbook.getPrimarySheet();
        rootTopic = defSheet.getRootTopic();
        rootTopic.setTitleText(text);
        rootTopicId = rootTopic.getId();
    }

    /**
     * 保存xmind文件
     */
    public void saveWorkBook(){
        try {
            if (workbook != null){
                workbook.save(ComConstant.XMIND_PATH);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据节点id创建子节点
     * @param parentTopicId     父节点
     * @param text              节点名称
     * @param status            状态：成功success, 失败fail
     * @return  返回节点id
     */
    public String createSonNode(String parentTopicId, String text, String status ){
        ITopic parentTopic = workbook.findTopic(parentTopicId);
        ITopic topic = workbook.createTopic();
        topic.setTitleText(text);
        if (status != null && !status.trim().isEmpty()) {
            topic.addMarker(status);
        }
        parentTopic.add(topic, ITopic.ATTACHED);
        return topic.getId();
    }
}
