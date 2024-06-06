package com.ravenl.htmlannotator.core.util

actual fun defaultLogger(level: Logger.Level): Logger = Logger(level, LogProxy())