package sunhill.DataPool

import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Basic class for different Items that use the same data base. So the data base is only created once and
 * all items access this pool to provide their data. Datapools store the raw data not json answers. This data
 * has to be converted by the item and/or marketeer
 */
open class DataPoolBase {

    fun String.runCommand(
        workingDir: File = File("."),
        timeoutAmount: Long = 60,
        timeoutUnit: TimeUnit = TimeUnit.SECONDS
    ): String? = runCatching {
        ProcessBuilder("\\s".toRegex().split(this))
            .directory(workingDir)
            .redirectOutput(ProcessBuilder.Redirect.PIPE)
            .redirectError(ProcessBuilder.Redirect.PIPE)
            .start().also { it.waitFor(timeoutAmount, timeoutUnit) }
            .inputStream.bufferedReader().readText()
    }.onFailure { it.printStackTrace() }.getOrNull()
}