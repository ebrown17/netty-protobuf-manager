// logback.groovy config file must be on classpath along with Groovy to be read

// logs configuration details
statusListener(OnConsoleStatusListener)

// period to scan for config file changes
scan('1 Minutes')

// config file constants
def logFileDate = timestamp('yyyy-MM-dd_HHmmss')
def defaultLogPattern = "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"

appender("STDOUT", ConsoleAppender) {
	encoder(PatternLayoutEncoder) {
	  Pattern = defaultLogPattern
	}
}
/*appender("FILE", FileAppender) {
	file = "test-${logFileDate}.log"
	encoder(PatternLayoutEncoder) {
	  Pattern = defaultLogPattern
	}
}*/
appender("ROLLING", RollingFileAppender) {
	file = "test.log"
	
	encoder(PatternLayoutEncoder) {
	  Pattern = defaultLogPattern
	}
	rollingPolicy(TimeBasedRollingPolicy) {
	  FileNamePattern = "test-%d.log.gz"
	  MaxHistory = 14
	  TotalSizeCap = "30GB"
	}
}


// Log will display all levels that are greater than set level
// TRACE < DEBUG < INFO < WARN < ERROR < OFF

// Log level for logger in these classes, will override root level if set, will duplicate logs if same appender is added 
logger('Foo', INFO)//["appender"])

// Root log level for all logging
root(DEBUG, ["STDOUT","ROLLING"])