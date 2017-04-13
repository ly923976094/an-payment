

import java.sql.{DriverManager, Connection, ResultSet}

/**
  * Created by liyang on 17/4/12.
  */
object Mysql extends App{

  // 访问本地MySQL服务器，通过3306端口访问mysql数据库
  val url = "jdbc:mysql://localhost:3306/test"
  //驱动名称
  val driver = "com.mysql.jdbc.Driver"
  //用户名
  val username = "root"
  //密码
  val password = ""
  //初始化数据连接
  var connection: Connection = _
  try {
    //注册Driver
    Class.forName(driver).newInstance()
    //得到连接
    connection = DriverManager.getConnection(url, username, password)
    val statement = connection.createStatement
    //执行查询语句，并返回结果
    val rs = statement.executeQuery("SELECT * FROM solution")
    //打印返回结果
    while (rs.next) {
      val clide = rs.getString("clide")
      val orderid = rs.getString("orderid")
      println("host = %s, user = %s".format(clide, orderid))
    }
  } catch {
    case e: Exception => e.printStackTrace
  }
  //关闭连接，释放资源
  connection.close

}
