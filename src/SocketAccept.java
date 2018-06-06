import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

/********************************************************************************
 *
 * Title: socket报文接收
 *
 * Description:
 *
 * @author chenlm
 * create date on 2018/6/6
 *
 *******************************************************************************/
public class SocketAccept {
    private BufferedReader reader;
    private ServerSocket serverSocket;
    //请求的文件/url 路径
    private String requestPath;
    //处理multipart用的boundary
    private String boundary = null;
    //post提交方式的数据长度
    private int contentLength = 0;

    /**
     * 创建socket服务
     */
    public void accept() {
        Socket socket = null;
        try {
            serverSocket = new ServerSocket(6666);
            //循环监听
            while (true) {
                socket = serverSocket.accept();
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                String line = reader.readLine();
//                System.out.println("获得输入信息" + line);
//                ClintRequestBean requestBean = new ClintRequestBean(line);
//                System.out.println("客户端请求：" + requestBean.toReadString());
//                System.out.println("请求参数[路径]：" + requestBean.getRequestParm().get("path"));
//                System.out.println("请求参数[参数表]：" + requestBean.getRequestParm().get("attrs"));
//                callservice(requestBean);
                roviInfo();
                System.out.println("下一次");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                    socket.close();
                } catch (Exception e1) {
                    e1.printStackTrace();

                }
            }
        }
    }

    /**
     * 根据请求调用servlet
     *
     * @param info
     * @throws Exception
     */
    public void callservice(ClintRequestBean info) throws Exception {
        //加载
        if (info.getData() != null) {
            Class c = Class.forName(((String) info.getRequestParm().get("path")).replace("/", ""));
            //获取方法
            Method[] methods = c.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(info.getMethod())) {
                    method.invoke(c.newInstance(), null);
                    break;
                }
            }


        }
    }

    public void roviInfo() throws Exception {
        //这边不能使用(ch=r.read())!=-1读取，原因是因为－1代表流结束，而如果服务端不向客户端发送应答，流是不会结束的。
        String line = reader.readLine();
        while (line != null) {
            System.out.println(line);
            line = reader.readLine();
            if ("".equals(line)) {
                break;
            } else if (line.indexOf("Content-Length") != -1) {
                this.contentLength = Integer.parseInt(line.substring(line.indexOf("Content-Length") + 16));
                System.out.println("contentLength: " + this.contentLength);
            } else if (line.indexOf("boundary") != -1) {
                this.boundary = line.substring(line.indexOf("boundary") + 9);
                System.out.println("********" + boundary);
            }
        }
    }


}
