package com.github.qinyou.common.render;

import com.github.qinyou.common.utils.WebUtils;
import com.github.qinyou.common.utils.ZipUtils;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


/**
 * zip render
 * 通过字符串数据 和 文件名 集合 下载 数据压缩文件
 *
 * @author chuang
 */
@SuppressWarnings("unused")
@Slf4j
public class ZipRender extends Render {
    private final static String CONTENT_TYPE = "application/x-zip-compressed;charset=" + getEncoding();

    private String fileName;      // 下载文件名

    private List<String> datas;     // 字符串数据 集合
    private List<String> filenames; // 文件名 集合

    public static ZipRender me() {
        return new ZipRender();
    }

    public ZipRender fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public ZipRender datas(List<String> datas) {
        this.datas = datas;
        return this;
    }

    public ZipRender filenames(List<String> filenames) {
        this.filenames = filenames;
        return this;
    }

    @SuppressWarnings("Duplicates")
    @Override
    public void render() {
        response.reset();
        fileName = WebUtils.buildDownname(request, fileName);
        response.setHeader("Content-disposition", "attachment;" + fileName);
        response.setContentType(CONTENT_TYPE);
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            ZipUtils.toZip(datas, filenames, os);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RenderException(e);
        } finally {
            try {
                if (os != null) {
                    os.flush();
                    os.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

}
