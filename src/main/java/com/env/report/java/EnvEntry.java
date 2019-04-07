package com.env.report.java;

/**
 * Created by handsomeyang on 2019-04-06.
 *
 * @author handsomeyang
 */
public class EnvEntry {

  private String tool;
  private String toolInfo;

  public EnvEntry(String tool, String toolInfo) {
    this.tool = tool;
    this.toolInfo = toolInfo;
  }

  String getTool() {
    return tool;
  }

  String getToolInfo() {
    return toolInfo;
  }
}
