package wos.mobile.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HttpUtil {

    private static int ConnectionTimeout = 10000;
    private static int SoTimeout = 10000;

    /**
     * 执行一个HTTP GET请求，返回请求响应的结果字符串
     *
     * @param url     请求的URL地址(可带查询参数)
     * @param charset 字符集
     * @return 返回请求响应的结果字符串
     */
    public static String doGet(String url, String charset) {

        try {
            URL _url = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) _url.openConnection();
            connection.setConnectTimeout(SoTimeout);
            connection.setRequestMethod("GET");
            //获得结果码
            int responseCode = connection.getResponseCode();
            if(responseCode ==200){
                //请求成功 获得返回的流
                InputStream is = connection.getInputStream();
                return (changeInputStream(is, charset));
            }else {
                //请求失败
                return "";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * post发送JSON字符串
     *
     * @param urlPath 服务器URL
     * @param json    Json字符串
     * @return 服务器返回结果
     */
    public static String doJsonPost(String urlPath, String json) {
        String result = "";
        BufferedReader reader = null;
        try {
            URL url = new URL(urlPath);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            // 设置文件类型:
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            // 设置接收类型否则返回415错误
            conn.setRequestProperty("accept", "*/*"); //此处为暴力方法设置接受所有类型，以此来防范返回415;
            //conn.setRequestProperty("accept", "application/json");
            // 往服务器里面发送数据
            if (!StringUtil.isEmpty(json)) {
                byte[] writebytes = json.getBytes("UTF-8");
                // 设置文件长度
                conn.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
                OutputStream outwritestream = conn.getOutputStream();
                outwritestream.write(writebytes);
                outwritestream.flush();
                outwritestream.close();

            }
            if (conn.getResponseCode() == 200) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                result = reader.readLine();
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e(e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * 执行一个HTTP POST请求，返回请求响应的HTML
     *
     * @param url     请求的URL地址
     * @param params  请求的查询参数,可以为null
     * @param charset 字符集
     * @return 返回请求响应的HTML
     */
    public static String doPost(String url, Map<String, Object> params, String charset) {
        StringBuffer stringBuffer = new StringBuffer();

        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                try {

                    if (entry.getValue() != null) {
                        stringBuffer.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue().toString(), charset)).append("&");
                    }
                } catch (Exception e) {
                    LogUtil.e(e.getMessage());
                    e.printStackTrace();
                }
            }
            // 删掉最后一个 & 字符
            if (stringBuffer.length() > 0) stringBuffer.deleteCharAt(stringBuffer.length() - 1);

        }

        try {
            URL http_url = new URL(url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) http_url.openConnection();
            httpURLConnection.setConnectTimeout(3000);
            httpURLConnection.setReadTimeout(SoTimeout);
            httpURLConnection.setDoInput(true);// 从服务器获取数据
            httpURLConnection.setDoOutput(true);// 向服务器写入数据

            // 获得上传信息的字节大小及长度
            byte[] mydata = stringBuffer.toString().getBytes(charset);
            // 设置请求体的类型
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setRequestProperty("Content-Lenth", String.valueOf(mydata.length));

            // 获得输出流，向服务器输出数据
            OutputStream outputStream = (OutputStream) httpURLConnection.getOutputStream();
            outputStream.write(mydata);

            // 获得服务器响应的结果和状态码
            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == 200) {

                // 获得输入流，从服务器端获得数据
                InputStream inputStream = (InputStream) httpURLConnection.getInputStream();
                return (changeInputStream(inputStream, charset));

            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 把从输入流InputStream按指定编码格式encode变成字符串String
     *
     * @param inputStream 输入流
     * @param encode      编码
     * @return
     */
    public static String changeInputStream(InputStream inputStream, String encode) {
        // ByteArrayOutputStream 一般叫做内存流
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        String result = "";
        if (inputStream != null) {
            try {
                while ((len = inputStream.read(data)) != -1) {
                    byteArrayOutputStream.write(data, 0, len);

                }
                result = new String(byteArrayOutputStream.toByteArray(), encode);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        return result;
    }

    /**
     * 上传文件
     *
     * @param serverUrl     服务器地址
     * @param localFilePath 本地文件路径
     * @return
     */
    public static String doUpload(
            String serverUrl,
            String localFilePath) {
        try {
            File file = new File(localFilePath);
            //上传素材            
            String result = connectHttpsByPost(serverUrl, file);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 分块上传
     *
     * @param serverUrl
     * @param localFilePath
     * @param size
     * @return
     */
    public static String uploadFilePart(String serverUrl, String localFilePath, int size) throws Exception {
        File file = new File(localFilePath);
        if (!file.exists()) throw new Exception("文件不存在");
        if (file.isDirectory()) throw new Exception("文件路径为文件夹");
        String fileMD5 = FileIOUtil.getFileMD5(localFilePath);
        if (StringUtil.isEmpty(fileMD5)) throw new Exception("获取文件MD5编码失败");
        if (size < 0) throw new Exception("上传文件块大小必须大于0");
        long fileLength=file.length();

        DataInputStream in = new DataInputStream(new FileInputStream(file));
        byte[] bufferOut = new byte[size];

        int bytes = 0, off = 0;

        while ((bytes = in.read(bufferOut)) != -1) {
            long timeMSecond=System.currentTimeMillis();
            HttpURLConnection con=getHttpURLConnection(serverUrl+"?position="+off+"&size="+size+"&fileMD5="+fileMD5+"&fileLength="+fileLength,timeMSecond);
            OutputStream out=getOutStream(con,file.getName(),fileMD5,file.length(),off,size,timeMSecond);
            out.write(bufferOut);
            String BOUNDARY = "----------" + timeMSecond;
            byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线
            out.write(foot);
            out.flush();
            out.close();
            String result=getOutStreamResult(con);
            System.out.println(result);
            bufferOut = new byte[size];
            off += size;
        }
        in.close();
        return fileMD5;
    }

    public static HttpURLConnection getHttpURLConnection(String serverUrl,long timeMSecond) throws Exception {
        //连接
        HttpURLConnection con = (HttpURLConnection) (new URL(serverUrl)).openConnection();

        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false); // post方式不能使用缓存

        // 设置请求头信息
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");
        // 设置边界
        String BOUNDARY = "----------" + timeMSecond;
        con.setRequestProperty("Content-Type",
                "multipart/form-data; boundary="
                        + BOUNDARY);
        return con;
    }

    /**
     * 获取OutputStream
     * @param con
     * @param fileName
     * @param fileLength
     * @param position
     * @param size
     * @return
     * @throws Exception
     */
    public static OutputStream getOutStream(HttpURLConnection con,String fileName,String fileMD5,long fileLength,int position,int size,long timeMSecond) throws Exception {

        String BOUNDARY = "----------" + timeMSecond;
        // 请求正文信息 第一部分：
        StringBuilder sb = new StringBuilder();
        sb.append("--"); // 必须多两道线
        sb.append(BOUNDARY);
        sb.append("\r\n");
        sb.append("Content-Disposition: form-data;name=\"media\";filelength=\"" + fileLength + "\";filename=\""
                + fileName + "\"\r\n");
        sb.append("Content-Type:multipart/form-data\r\n\r\n");
        byte[] head = sb.toString().getBytes("utf-8");
        // 获得输出流
        OutputStream out = new DataOutputStream(con.getOutputStream());
        // 输出表头
        out.write(head);

        return out;
    }

    /**
     * 获取结构
     * @param con
     * @return
     * @throws Exception
     */
    public static String getOutStreamResult(HttpURLConnection con) throws Exception {
        String result="";
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = null;
        try { // 定义BufferedReader输入流来读取URL的响应
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            result = buffer.toString();
        } catch (IOException e) {
            throw e;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return result;
    }

    public static String connectHttpsByPost(String path, File file) throws Exception {
        URL urlObj = new URL(path);
        //连接
        HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
        String result = null;
        con.setDoInput(true);
        con.setDoOutput(true);
        con.setUseCaches(false); // post方式不能使用缓存

        // 设置请求头信息
        con.setRequestProperty("Connection", "Keep-Alive");
        con.setRequestProperty("Charset", "UTF-8");
        // 设置边界
        String BOUNDARY = "----------" + System.currentTimeMillis();
        con.setRequestProperty("Content-Type",
                "multipart/form-data; boundary="
                        + BOUNDARY);

        // 请求正文信息
        // 第一部分：
        StringBuilder sb = new StringBuilder();
        sb.append("--"); // 必须多两道线
        sb.append(BOUNDARY);
        sb.append("\r\n");
        sb.append("Content-Disposition: form-data;name=\"media\";filelength=\"" + file.length() + "\";filename=\""
                + file.getName() + "\"\r\n");
        sb.append("Content-Type:application/octet-stream\r\n\r\n");
        byte[] head = sb.toString().getBytes("utf-8");
        // 获得输出流
        OutputStream out = new DataOutputStream(con.getOutputStream());
        // 输出表头
        out.write(head);

        // 文件正文部分
        // 把文件已流文件的方式 推入到url中
        DataInputStream in = new DataInputStream(new FileInputStream(file));
        int bytes = 0;
        byte[] bufferOut = new byte[1024];
        while ((bytes = in.read(bufferOut)) != -1) {
            out.write(bufferOut, 0, bytes);
        }
        in.close();
        // 结尾部分
        byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");// 定义最后数据分隔线
        out.write(foot);
        out.flush();
        out.close();
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = null;
        try { // 定义BufferedReader输入流来读取URL的响应
            reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            if (result == null) {
                result = buffer.toString();
            }
        } catch (IOException e) {
            System.out.println("发送POST请求出现异常！" + e);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return result;
    }

    /**
     * 获取网络图片流
     *
     * @param url
     * @return
     */
    public static InputStream getImageStream(String url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                return inputStream;
            }
        } catch (IOException e) {
            System.out.println("获取网络图片出现异常，图片路径为：" + url);
            e.printStackTrace();
        }
        return null;
    }

}
