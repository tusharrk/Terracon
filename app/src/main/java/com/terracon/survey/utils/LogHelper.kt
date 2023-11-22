package com.terracon.survey.utils

import android.content.Context

import com.michaelflisar.lumberjack.core.L
import com.michaelflisar.lumberjack.implementation.LumberjackLogger
import com.michaelflisar.lumberjack.implementation.plant
import com.michaelflisar.lumberjack.loggers.console.ConsoleLogger
import com.michaelflisar.lumberjack.loggers.file.FileLogger
import com.michaelflisar.lumberjack.loggers.file.FileLoggerSetup

object LogHelper {

    lateinit var FILE_LOGGING_SETUP: FileLoggerSetup

    fun init(context: Context) {
        // 1) install the implemantion
        L.init(LumberjackLogger)

        // 2) install loggers
        L.plant(ConsoleLogger())
        val setup = FileLoggerSetup.Daily(context).also {
            FILE_LOGGING_SETUP = it
        }
        L.plant(FileLogger(setup))
//        L.plant(ConsoleTree())
//        // OPTIONAL: we could plant a file logger here if desired
//        //FILE_LOGGING_SETUP = FileLoggingSetup.DateFiles(
//        //    context,
//        //    setup = FileLoggingSetup.Setup(fileExtension = "txt")
//        //)
//        FILE_LOGGING_SETUP = FileLoggingSetup.NumberedFiles(
//            context,
//            setup = FileLoggingSetup.Setup(
//                fileName = "log",
//                fileExtension = "txt",
//                logsToKeep = 20
//            ),
//            sizeLimit = "1MB"
//        )
//        L.plant(FileLoggingTree(FILE_LOGGING_SETUP))

        // we disable logs in release inside this demo
        //L.enabled = BuildConfig.DEBUG

        // we can filter out logs based on caller class names like following e.g.:
//        L.packageNameFilter = {
//            Log.d("PACKAGE_NAME_FILTER", "packageName = $it")
//             !it.startsWith(MainActivity::class.java.name)
//        }
    }

    fun clearLogFiles() {
        // do NOT delete all files directly, just delete old ones and clear the newest one => the following function does do that for you
       // FILE_LOGGING_SETUP.clearLogFiles()
    }
}