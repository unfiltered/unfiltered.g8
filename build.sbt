import scala.jdk.CollectionConverters._
import java.lang.management.ManagementFactory

// https://github.com/foundweekends/giter8/issues/1049
scriptedKeepTempDirectory := false

scriptedBufferLog := false

scriptedLaunchOpts ++= ManagementFactory.getRuntimeMXBean.getInputArguments.asScala.toList.filter(
  a => Seq("-Xmx", "-Xms", "-XX", "-Dsbt.log.noformat").exists(a.startsWith)
)
