package com.xuecheng.media.service.jobhandler;

import com.xuecheng.base.utils.Mp4VideoUtil;
import com.xuecheng.media.model.po.MediaProcess;
import com.xuecheng.media.service.MediaFileProcessService;
import com.xuecheng.media.service.MediaFileService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * XxlJob开发示例（Bean模式）
 * <p>
 * 开发步骤：
 * 1、任务开发：在Spring Bean实例中，开发Job方法；
 * 2、注解配置：为Job方法添加注解 "@XxlJob(value="自定义jobhandler名称", init = "JobHandler初始化方法", destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 * 3、执行日志：需要通过 "XxlJobHelper.log" 打印执行日志；
 * 4、任务结果：默认任务结果为 "成功" 状态，不需要主动设置；如有诉求，比如设置任务结果为失败，可以通过 "XxlJobHelper.handleFail/handleSuccess" 自主设置任务结果；
 *
 * @author xuxueli 2019-12-11 21:52:51
 */
@Component
@Slf4j
public class VideoTask {

    @Autowired
    MediaFileProcessService mediaFileProcessService;

    @Autowired
    MediaFileService mediaFileService;

    @Value("${videoprocess.ffmpegpath}")
    private String ffmpegpath;

    /**
     * 2、分片广播任务
     */
    @XxlJob("videoJobHandler")
    public void shardingJobHandler() throws Exception {

        // 分片参数
        int shardIndex = XxlJobHelper.getShardIndex();
        int shardTotal = XxlJobHelper.getShardTotal();
        //确定CPU核心数
        int processors = Runtime.getRuntime().availableProcessors();
        //查询待处理任务
        List<MediaProcess> mediaProcessList = mediaFileProcessService.getMediaProcessList(shardIndex, shardTotal, processors);
        int size = mediaProcessList.size();
        log.debug("取到的任务数:{}", size);
        if (size <= 0) {
            return;
        }
        //创建线程池
        ExecutorService executorService = Executors.newFixedThreadPool(size);
        CountDownLatch countDownLatch = new CountDownLatch(size);

        mediaProcessList.forEach(mediaProcess -> {
            //将任务加入线程池
            executorService.execute(() -> {
                try {
                    Long taskId = mediaProcess.getId();
                    //任务执行逻辑
                    //开启任务
                    boolean b = mediaFileProcessService.startTask(taskId);
                    if (!b) {
                        log.debug("抢占任务失败，任务id:{}", taskId);

                        return;
                    }
                    //抢占成功
                    //执行视频转码
                    //ffmpeg的路径
                    String ffmpeg_path = ffmpegpath;//ffmpeg的安装位置
                    String bucket = mediaProcess.getBucket();
                    String objectName = mediaProcess.getFilePath();
                    File file = mediaFileService.downloadFileFromMinIO(bucket, objectName);
                    if (file == null) {
                        log.debug("下载视频出错,任务:{}", taskId);
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", mediaProcess.getFileId(), null, "下载视频到本地失败");
                        return;
                    }

                    //下载minio视频到本地

                    //源avi视频的路径
                    String fileId = mediaProcess.getFileId();
                    String video_path = file.getAbsolutePath();
                    //转换后mp4文件的名称
                    String mp4_name = fileId + ".mp4";
                    File mp4File = null;
                    //转换后mp4文件的路径
                    try {
                        mp4File = File.createTempFile("minio", ".mp4");
                    } catch (IOException e) {
                        log.debug("创建临时文件异常:taskId:{}", taskId);
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", mediaProcess.getFileId(), null, "下载视频到本地失败");
                        return;
                    }
                    String mp4_path = mp4File.getAbsolutePath();

                    //创建工具类对象
                    Mp4VideoUtil videoUtil = new Mp4VideoUtil(ffmpeg_path, video_path, mp4_name, mp4_path);
                    //开始视频转换，成功将返回success
                    String result = videoUtil.generateMp4();
                    if (!result.equals("success")) {
                        log.debug("视频转码失败");
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", mediaProcess.getFileId(), null, "视频转码失败");
                        return;
                    }
                    boolean b1 = mediaFileService.addMediaFilesToMinIO(mp4File.getAbsolutePath(), "video/mp4", bucket, objectName);
                    if (!b1) {
                        log.debug("上传MP4文件到minio失败");
                        mediaFileProcessService.saveProcessFinishStatus(taskId, "3", mediaProcess.getFileId(), null, "上传MP4文件到minio失败");
                        return;
                    }
                    String url = getFilePath(fileId, ".mp4");
                    mediaFileProcessService.saveProcessFinishStatus(taskId, "2", mediaProcess.getFileId(), url, "下载视频到本地失败");
                } finally {
                    //计数器减一
                    countDownLatch.countDown();
                }
            });
        });
        //指定最大限度等待时间
        countDownLatch.await(30, TimeUnit.MINUTES);

    }

    private String getFilePath(String fileMd5, String fileExt) {
        return fileMd5.substring(0, 1) + "/" + fileMd5.substring(1, 2) + "/" + fileMd5 + "/" + fileMd5 + fileExt;
    }


}
