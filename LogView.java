import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.*;

public class LogView {
    private long lastTimeFileSize = 0;  //上次文件大小
    /**
     * 实时输出日志信息
     * @param
     * @throws IOException
     */
    public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?text=" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            /*
             * Map<String, List<String>> map = connection.getHeaderFields(); //
             * 遍历所有的响应头字段 for (String key : map.keySet()) {
             * System.out.println(key + "--->" + map.get(key)); }
             */
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
    public void realtimeShowLog(File logFile) throws IOException{
        //指定文件可读可写
        final RandomAccessFile randomFile = new RandomAccessFile(logFile,"rw");
        //启动一个线程每1秒钟读取新增的日志信息
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
        exec.scheduleWithFixedDelay(new Runnable(){
            public void run() {
                try {
                    //获得变化部分的
                    randomFile.seek(lastTimeFileSize);
					String tmp = new String(randomFile.readLine().getBytes("ISO-8859-1"),"utf-8");
                    while(tmp !=null){
                        System.out.println(tmp);
						tmp=tmp.substring(tmp.indexOf("本地"));
                        if(tmp.indexOf("YOUR ID")!=-1&&((tmp.indexOf("黑暗血袭者")!=-1)||(tmp.indexOf("德拉克")!=-1)||(tmp.indexOf("阿勒门")!=-1)||(tmp.indexOf("莱塞勒")!=-1)||(tmp.indexOf("泰瑞")!=-1)
								||(tmp.indexOf("拖运船")!=-1)||(tmp.indexOf("装运舰")!=-1)||(tmp.indexOf("运载舰")!=-1)||(tmp.indexOf("护卫舰")!=-1)||(tmp.indexOf("航空母舰")!=-1))) {
                        sendGet("http://sc.ftqq.com/YOUR KEY.send",tmp);
                        }
                        tmp = new String(randomFile.readLine().getBytes("ISO-8859-1"), "utf-8");
                    }
                    lastTimeFileSize = randomFile.length();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws Exception {
        Calendar cal=Calendar.getInstance();
        int m,d;
        m=cal.get(Calendar.MONTH)+1;
        d=cal.get(Calendar.DATE);
        String fileName = "日志记录-"+m+"月"+d+"日.txt";
		if(d<10)
			fileName = "日志记录-"+m+"月0"+d+"日.txt";
        LogView view = new LogView();
        File tmpLogFile = new File(fileName);
        while(!tmpLogFile.exists()){
			System.out.println(fileName);
            d=d-1;
            if(d == 0){
                m=m-1;
                if(m==1||m==3||m==5||m==7||m==8||m==10){
                    d=31;
                }else if(m==2)
                    d=28;
                else d=30;
            }
			if(d<10)
				fileName = "日志记录-"+m+"月0"+d+"日.txt";
			else
				fileName = "日志记录-"+m+"月"+d+"日.txt";
            tmpLogFile = new File(fileName);
        }
        view.realtimeShowLog(tmpLogFile);
    }

}
