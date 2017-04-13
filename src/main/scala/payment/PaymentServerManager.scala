package payment


//import java.io._
//import java.net.InetSocketAddress
import java.io.{BufferedReader, IOException, InputStreamReader}
import java.net.InetSocketAddress
import java.util.concurrent.Executors

import org.json.JSONObject
import org.json.JSONException
import java.util.concurrent.Executors

import com.sun.net.httpserver.{HttpExchange, HttpHandler, HttpServer}

/**
  * CN android payment
  * payment server
  * Created by liyang on 17/4/9.
  */
object PaymentServerManager {
  def main(args: Array[String]): Unit = {
    val addr: InetSocketAddress = new InetSocketAddress(8888)
    val server: HttpServer = HttpServer.create(addr, 100)
    server.createContext("/userRechargeSync", new userRechargeSync) //userRechargeSync
    server.createContext("/userLoginSync", new userLoginSync) //userLoginSync
    server.createContext("/elb_monitor", new elb_monitor) //elb_monitor

    server.setExecutor(Executors.newCachedThreadPool)
    server.start()
    println("Server is listening on port 8888")
  }

}

class userRechargeSync extends HttpHandler {
  @throws[IOException]
  @throws[JSONException]
  def handle(exchange: HttpExchange) {
    val requestMethod = exchange.getRequestMethod
    val anPaymentSql = new AndroidPaymentSqlServer
    if (requestMethod.equalsIgnoreCase("POST")) {
      println("POST userReCharge")
      /**
        * request
        */
      // System.out.println(jsonObject.toString());
      val reqBody = exchange.getRequestBody
      val reader = new BufferedReader(new InputStreamReader(reqBody))
      var temp = reader.readLine()

      var lines = Array[String]()
     // lines = lines :+ temp //读取第一行
      while (temp != null) {
        lines = lines :+ temp
        temp = reader.readLine()
      }
      println(s"lines: ${lines.toBuffer}")

      var result: String = ""
      lines.foreach { s =>
        result += s
      }

      println("result: " + result)
      var reqJson1: JSONObject = null
      //第一层
      var reqJson2: JSONObject = null //第二层
      try {
        reqJson1 = new JSONObject(result)
        reqJson2 = new JSONObject(new String(reqJson1.getString("data").getBytes, "utf-8"))
        // System.out.println(reqJson1.getString("data"));
        println(s"uid: ${reqJson2.getString("uid")} orderid: ${reqJson2.getString("orderid")} item_code: ${reqJson2.getString("item_code")}")
      //mysql存储数据
        anPaymentSql.androidMysqlServer(reqJson2.getString("uid"),reqJson2.getString("orderid"),reqJson2.getString("item_code"))

      }

      catch {
        case e: JSONException => {
          e.printStackTrace()
        }
      }
      reader.close()
      reqBody.close()

      //   System.out.println();
      /**
        * response
        */
      val responseHeaders = exchange.getResponseHeaders
      responseHeaders.set("Content-Type", "application/json;charset=utf-8")
      //         responseHeaders.set("Content-Type", "text/plain");
      exchange.sendResponseHeaders(200, 0)
      var resJson: JSONObject = null
      try
        resJson = new JSONObject("{'ok1':'11','ok':'1'}")

      catch {
        case e: JSONException => {
          e.printStackTrace()
        }
      }
      val responseBody = exchange.getResponseBody
      responseBody.write(resJson.toString.getBytes)
      responseBody.close()
    }
  }
}

class userLoginSync extends HttpHandler {
  @throws[IOException]
  def handle(exchange: HttpExchange) {
    val requestMethod = exchange.getRequestMethod
    if (requestMethod.equalsIgnoreCase("POST")) {
      /**
        * response
        */
      println("POST userLogin")
      val responseHeaders = exchange.getResponseHeaders
      responseHeaders.set("Content-Type", "application/json;charset=utf-8")
      //         responseHeaders.set("Content-Type", "text/plain");
      exchange.sendResponseHeaders(200, 0)
      var resJson: JSONObject = null
      try
        resJson = new JSONObject("{'ok1':'11','ok':'1'}")

      catch {
        case e: JSONException => {
          e.printStackTrace()
        }
      }
      val responseBody = exchange.getResponseBody
      responseBody.write(resJson.toString.getBytes)
      responseBody.close()
    }
  }
}

class elb_monitor extends HttpHandler {
  @throws[IOException]
  def handle(exchange: HttpExchange) {
    val requestMethod = exchange.getRequestMethod
    if (requestMethod.equalsIgnoreCase("GET")) {
      /**
        * response
        */
      println("GET elb_monitor")
      val responseHeaders = exchange.getResponseHeaders
      responseHeaders.set("Content-Type", "application/json;charset=utf-8")
      //         responseHeaders.set("Content-Type", "text/plain");
      exchange.sendResponseHeaders(200, 0)
      var resJson: JSONObject = null
      try
        resJson = new JSONObject("{'ok1':'11','ok':'1'}")

      catch {
        case e: JSONException => {
          e.printStackTrace()
        }
      }
      val responseBody = exchange.getResponseBody
      responseBody.write(resJson.toString.getBytes)
      responseBody.close()
    }
  }
}

