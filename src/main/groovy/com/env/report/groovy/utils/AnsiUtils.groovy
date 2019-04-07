package com.env.report.groovy.utils

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

final class AnsiUtils {
  public static def red(text) {
    return ansi().fg(RED).a(text).reset()
  }

  public static def green(text) {
    return ansi().fg(GREEN).a(text).reset()
  }

  public static def white(text) {
    return ansi().fg(WHITE).a(text).reset()
  }

  public static def yellow(text) {
    return ansi().fg(YELLOW).a(text).reset()
  }

  public static def bold(text) {
    return ansi().bold().a(text).reset()
  }
}
