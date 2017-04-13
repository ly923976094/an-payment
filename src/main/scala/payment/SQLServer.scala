package payment

import java.sql.{Connection, DriverManager}

/**
  * Created by liyang on 17/4/12.
  */
class PaymentSqlServer {

  // 访问本地MySQL服务器，通过3306端口访问mysql数据库
  val url = "jdbc:mysql://localhost:3306"
  //驱动名称
  val driver = "com.mysql.jdbc.Driver"
  //用户名
  val username = "root"
  //密码
  val password = ""
  //初始化数据连接
  var connection: Connection = _

  def mysqlconnection(uid_json: String, orderId_json: String, productId_json: String) {
    try {
      //注册Driver
      Class.forName(driver).newInstance()
      //得到连接
      connection = DriverManager.getConnection(url, username, password)

      val statement = connection.createStatement
      val dropPaymentBase = "drop database if exists payment;"
      //if exists payment drop payment base
      val createPaymentBase = "create database if not exists payment;"
      val usePaymentBase = "use payment;"
      val createPaymentTable = "create table if not exists an_payment(id INT NOT NULL AUTO_INCREMENT," +
        "uid VARCHAR(100) NOT NULL," +
        "orderId VARCHAR(100) NOT NULL," +
        "productId VARCHAR(100) NOT NULL," +
        "is_check_orderId BOOLEAN DEFAULT FALSE NOT NULL," +
        "is_check_fc BOOLEAN DEFAULT FALSE NOT NULL," +
        "PRIMARY KEY (id));"
      /**
        * is_check_orderId : 是否检查过订单号，检查了则为1，没检查则为0，默认为0，检查后设置为1
        *
        * is_check_fc : 是for在android封测的时候购买过，如果购买成功则设置为1，没有购买成功设置为0，默认为0，上线后将为1玩家的货币双倍返还
        */


      //      statement.execute(dropPaymentBase)
      //      println("删除已经存在的payment数据库 成功")
      statement.execute(createPaymentBase)
      println("创建payment数据库成功")
      statement.execute(usePaymentBase)
      println("使用payment数据库成功")
      statement.execute(createPaymentTable)
      println("创建表成功")


      //      val insertData = "insert into an_payment (uid,orderId,productId) values ('115','2222','33333')"
      //
      //      statement.execute(insertData)
      val insertData = "insert into an_payment (uid,orderId,productId) values (?,?,?)"
      val prep = connection.prepareStatement(insertData)
      prep.setString(1, uid_json)
      prep.setString(2, orderId_json)
      prep.setString(3, productId_json)
      prep.executeUpdate

      println("插入数据成功")
      //执行查询语句，并返回结果
      val rs = statement.executeQuery("select * from an_payment")
      //打印返回结果
      while (rs.next) {
        val uid = rs.getString("uid")
        val orderId = rs.getString("orderId")
        val productId = rs.getString("productId")
        val is_check_orderId = rs.getBoolean("is_check_orderId")
        val is_check_fc = rs.getBoolean("is_check_fc")
        println("uid: " + uid +
          " orderId: " + orderId +
          " productId: " + productId +
          " is_check_orderId: " + is_check_orderId +
          " is_check_fc: " + is_check_fc)
        //  println("uid = %s, orderid = %s, productId = %s, is_check_orderId = %d, is_check_fc = %s".format(uid, orderid,productId,is_check_orderId,is_check_fc))
      }
    } catch {
      case e: Exception => e.printStackTrace
    } finally {
      //关闭连接，释放资源
      connection.close
    }

  }
}


class AndroidPaymentSqlServer {

  def androidMysqlServer (uid: String, orderId: String, productId: String) {
    val sqlServer = new PaymentSqlServer
    sqlServer.mysqlconnection(uid, orderId, productId)
  }

}
