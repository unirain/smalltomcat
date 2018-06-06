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

    /**
     * 创建socket服务
     */
    public void accept() {
        Socket socket;
        try {
            serverSocket = new ServerSocket(6666);
            //循环监听
            while (true) {
                socket = serverSocket.accept();
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line = reader.readLine();
                System.out.println("获得输入信息" + line);
                ClintRequestBean requestBean = new ClintRequestBean(line);
                System.out.println("客户端请求：" + requestBean.toReadString());
                System.out.println("请求参数[路径]：" + requestBean.getRequestParm().get("path"));
                System.out.println("请求参数[参数表]：" + requestBean.getRequestParm().get("attrs"));
                callservice(requestBean);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
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


}
