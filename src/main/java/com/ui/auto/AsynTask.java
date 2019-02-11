package com.ui.auto;


import com.ui.entity.ElementNode;
import com.ui.util.Log;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.*;

/**
 * Created by Administrator on 2018-07-24.
 * 异步处理截图圈中操作元素
 */
public class AsynTask {

    /**
     * 异步执行任务
     * @param picturePath
     * @param elementNode
     */
    public Future<Boolean> executeTask(String picturePath, ElementNode elementNode){
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        new Thread(()->{
            boolean flag = false;
            try {
                highlight(picturePath,elementNode);
                flag =true;
            }catch (Exception e){
                Log.logError("截图圈中失败", e);
            }
            future.complete(flag);
        }).start();

        //无需等待还没结束的计算,直接返回Future对象
        return future;
    }

    /**
     * 高亮元素截图，速度较慢，后续优化
     * @param picturePath   截图路径
     * @param elementNode   元素节点
     */
    private void highlight(String picturePath, ElementNode elementNode){
        //获取元素坐标、宽度、高度
        String bounds = elementNode.getBounds();
        String[] strings =bounds.substring(1,bounds.length()-1).split("]\\[");
        String[] startIndex = strings[0].split(",");
        String[] endIndex = strings[1].split(",");
        int x = Integer.parseInt(startIndex[0]);
        int y = Integer.parseInt(startIndex[1]);
        int width = Integer.parseInt(endIndex[0]) - x;
        int height = Integer.parseInt(endIndex[1]) - y;
        //元素高亮
        getImage(picturePath, x, y, width, height);
    }

    /**
     * 截图元素高亮
     * @param filePath  截图路径
     * @param x         起始横轴坐标
     * @param y         起始纵轴坐标
     * @param width     截图长度
     * @param height    截图宽度
     */
    private void getImage(String filePath, int x, int y, int width, int height) {
        File file = new File(filePath);
        // 截图中点击的元素用红色框标记
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            Graphics2D graphics2d = (Graphics2D) bufferedImage.getGraphics();
            graphics2d.setColor(Color.RED);
            graphics2d.setStroke(new BasicStroke(13));
            graphics2d.drawRect(x, y , width, height);
            ImageIO.write(bufferedImage, "png", new FileOutputStream(filePath));
        } catch (IOException e) {
            Log.logError("Get screen shot failed!",e);
        }
    }
}
