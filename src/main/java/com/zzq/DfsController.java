package com.zzq;

import com.zzq.util.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@Controller
public class DfsController {

    private Logger log = LoggerFactory.getLogger(DfsController.class);

    @Value("${basepath}")
    private String basepath;

    @Value("${logpath}")
    private String logpath;

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public Result upload(@RequestParam("file") MultipartFile file) {
        String path = saveFile(file);
        if (path != null) {
            HashMap<String, String> result = new HashMap<>();
            result.put("link1", "http://103.118.42.35:82/download?path=" + path.replace(basepath, "/"));
            result.put("link2", "http://103.118.42.35:81/file/" + path.replace(basepath, "/"));
            result.put("loglink", "http://103.118.42.35:82/seelog");
            result.put("rmark", "可以通过loglink 查看上传进度");

            return Result.scuess(result);
        } else {
            return Result.fail("失败！");
        }
    }

    @RequestMapping(value = "download", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public void download(String path, HttpServletResponse response) {
        try {
            writeFile(path, response);
        } catch (IOException e) {
            try {
                response.getWriter().print(e.getMessage());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    @RequestMapping(value = "/seelog", method = RequestMethod.GET)
    public void seelog(HttpServletResponse response) throws IOException {
        File file = new File(logpath);
        ArrayList<String> arr = new ArrayList<>();

        response.setHeader("Content-Type", "text/html");
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String line = null;
        while ((line = reader.readLine()) != null) {
            arr.add(line);
        }

        PrintWriter writer = response.getWriter();
        writer.print("<meta charset=\"utf-8\" />");

        for (int i = arr.size() - 1; i >= 0; i--) {
            writer.print(arr.get(i) + "<br/>");
        }

        writer.flush();

    }

    public String saveFile(MultipartFile file) {

        try {
            String originalFilename = file.getOriginalFilename();
            // 附件的后缀
            int suffixIdx = originalFilename.lastIndexOf(".");
            String suffixVaue = suffixIdx == -1 ? "" : originalFilename.substring(suffixIdx);

            // 生成相对路径
            String path = basepath + "/" + new SimpleDateFormat("yyMMdd").format(new Date()) + "/" + UUID.randomUUID().toString() + suffixVaue;

            File f = new File(path);
            if (!f.exists()) {
                f.getParentFile().mkdirs();
            }
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(f));

            BufferedInputStream inputStream = new BufferedInputStream(file.getInputStream());

            byte[] data = new byte[2048];
            int len = 0;
            int count = 0;
            while ((len = inputStream.read(data)) != -1) {
                outputStream.write(data, 0, len);
                count += len;
                log.info(file.getOriginalFilename() + "==>" + (Double.valueOf(count) / Double.valueOf(file.getSize())) * 100 + "%");
            }
            outputStream.flush();

            return path;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void writeFile(String path, HttpServletResponse response) throws IOException {
        File file = new File(path);
        if (!file.exists()) {
            response.getWriter().print(path + "==>文件不存在！");
        }

        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));

        BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());

        response.setHeader("Content-Length:", String.valueOf(((int) file.length())));
        int len = 0;
        byte[] data = new byte[2048];
        while ((len = inputStream.read(data)) != -1) {
            outputStream.write(data, 0, len);
        }
        outputStream.flush();
    }

}
