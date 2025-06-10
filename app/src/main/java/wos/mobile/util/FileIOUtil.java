/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package wos.mobile.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件读写操作
 *
 * @author Tyr Tao
 */
public class FileIOUtil {
    /**
     * @param filePath
     * @return
     */
    public static long getFileLength(String filePath) {
        try {
            File f = new File(filePath);
            if (f.exists() && f.isFile()) {
                return f.length();
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 读入文件
     */
    public static void readFile(String filePath) {
        try (FileReader reader = new FileReader(filePath); BufferedReader br = new BufferedReader(reader) // 建立一个对象，它把文件内容转成计算机能读懂的语言
        ) {
            String line;
            // 网友推荐更加简洁的写法
            while ((line = br.readLine()) != null) {
                // 一次读入一行数据
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写入文件
     *
     * @param filePath
     * @param content
     */
    public static void writeFile(String filePath, String content) {
        File file = new File(filePath);
        Writer out = null;
        try {
            out = new FileWriter(file);
            out.write(content);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除文件非目录
     *
     * @param filePath
     */
    public static boolean deleteFile(String filePath) {
        if (isFile(filePath)) {
            File file = new File(filePath);
            try {
                return file.delete();
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 坚持查文件或者文件夹是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean checkExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * 检查文件是否是文件
     *
     * @param filePath
     * @return
     */
    public static boolean isFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        return file.isFile();
    }

    /**
     * @param is 要读取的输入流
     */
    public static List<String> readLines(InputStream is) {
        if (is == null) {
            throw new RuntimeException("inputStream不能为null");
        }
        List<String> ret = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!"".equals(line)) {
                    ret.add(line);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return ret;
    }

    /**
     * @param is 要读取的输入流
     */
    public static String readContent(InputStream is) {
        if (is == null) {
            throw new RuntimeException("inputStream不能为null");
        }
        String ret = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder sb = new StringBuilder();
            String line = reader.readLine();
            while (line != null) {
                sb.append(line);
                line = reader.readLine();
            }
            ret = sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return ret;
    }

    /**
     * @param file 要读取的文件
     */
    public static List<String> readLines(File file) {
        if (!file.exists()) {
            throw new RuntimeException("文件不存在");
        }
        List<String> ret = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!"".equals(line)) {
                    ret.add(line);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return ret;
    }

    /**
     * @param file 要读取的文件
     */
    public static byte[] readBytes(File file) {
        if (!file.exists()) {
            throw new RuntimeException("文件不存在");
        }
        byte[] ret = new byte[(int) file.length()];
        if (ret.length > 0) {
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(file))) {
                while (in.read(ret) != -1) {
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return ret;
    }

    /**
     * 获取文件扩展名
     * @param filename
     * @return 文件扩展名，不包括.
     */
    public static String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * 通过流方式复制文件
     * @param srcPathStr 起始位置
     * @param desPathStr 目标位置
     */
    public static void copy(String srcPathStr, String desPathStr)
    {
        File file=new File(desPathStr);
        if (file.exists()) {
            file.deleteOnExit();
        } else {
            File fileParent=new File(file.getParent());
            fileParent.mkdirs();
        }
        try
        {
            FileInputStream fis = new FileInputStream(srcPathStr);//创建输入流对象
            FileOutputStream fos = new FileOutputStream(desPathStr); //创建输出流对象
            byte datas[] = new byte[1024*8];//创建搬运工具
            int len = 0;//创建长度
            while((len = fis.read(datas))!=-1)//循环读取数据
            {
                fos.write(datas,0,len);
            }
            fis.close();//释放资源
            fos.close();//释放资源


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件MD5编码
     * @param path
     * @return
     */
    public static String getFileMD5(String path) {
        BigInteger bi = null;
        try {
            byte[] buffer = new byte[8192];
            int len = 0;
            MessageDigest md = MessageDigest.getInstance("MD5");
            File f = new File(path);
            FileInputStream fis = new FileInputStream(f);
            while ((len = fis.read(buffer)) != -1) {
                md.update(buffer, 0, len);
            }
            fis.close();
            byte[] b = md.digest();
            bi = new BigInteger(1, b);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bi.toString(16);
    }
}
