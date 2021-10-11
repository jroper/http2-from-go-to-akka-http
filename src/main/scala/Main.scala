import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._

object Main extends App {
  implicit val system = ActorSystem(Behaviors.empty, "my-system")
  val route = get {
    complete("Hello, world")
  }
  import java.io.InputStream
import java.security.{ KeyStore, SecureRandom }

import javax.net.ssl.{ KeyManagerFactory, SSLContext, TrustManagerFactory }
import akka.actor.ActorSystem
import akka.http.scaladsl.server.{ Directives, Route }
import akka.http.scaladsl.{ ConnectionContext, Http, HttpsConnectionContext }
import com.typesafe.sslconfig.akka.AkkaSSLConfig

// Manual HTTPS configuration

val password: Array[Char] = "abcdef".toCharArray // do not store passwords in code, read them from somewhere safe!

val ks: KeyStore = KeyStore.getInstance("PKCS12")
val keystore: InputStream = getClass.getClassLoader.getResourceAsStream("server.p12")

require(keystore != null, "Keystore required!")
ks.load(keystore, password)

val keyManagerFactory: KeyManagerFactory = KeyManagerFactory.getInstance("SunX509")
keyManagerFactory.init(ks, password)

val tmf: TrustManagerFactory = TrustManagerFactory.getInstance("SunX509")
tmf.init(ks)

val sslContext: SSLContext = SSLContext.getInstance("TLS")
sslContext.init(keyManagerFactory.getKeyManagers, tmf.getTrustManagers, new SecureRandom)
val https: HttpsConnectionContext = ConnectionContext.httpsServer(sslContext)
  Http().newServerAt("localhost", 8000).bind(route)
  Http()
    .newServerAt("localhost", 8001)
    .enableHttps(https)
    .bind(route)
}
