package bito.util.textparser;

import java.util.Map;

import bito.util.dba.SQLRunner;
import bito.util.logger.Log;

public interface SegmentParser
{
	/**
	 * 设置当前段落解析脚本
	 * 通过系统配置信息获取脚本名称对应的脚本文件，参数指定为name
	 * parser.script.name=script.file.path
	 * 通过系统配置信息获取脚本名称对应的脚本内容，参数指定为name，对应配置信息以"{"开始,"}"结束
	 * parser.script.name={script.file.path}
	 * 直接设置脚本内容，参数指定字符串以"{"开始,"}"结束
	 */
	public void setParserScript(String parserScriptName) throws Exception;

	/**
	 * 设置输出日志
	 */
	public void setLogger(Log log);

	/**
	 * 设置 SQLRunner
	 */
	public void setSQLRunner(SQLRunner sqlrunner);

	/**
	 * 获取当前段落解析程序的标识
	 * 即setParserScript中指定的parserScriptName
	 */
	public String getParserName() throws Exception;

	/**
	 * 设置当前段落解析脚本中的变量
	 */
	public void setVariable(String key, Object value) throws Exception;

	/**
	 * 设置当前段落
	 */
	public void setParentSegment(SegmentParser parentSegment) throws Exception;

	/**
	 * 获取父级段落解析程序
	 */
	public SegmentParser getParentSegment() throws Exception;

	/**
	 * 解析一行，返回解析该行的段落解析程序
	 * 
	 * 当前段是否结束，当前行是否属于当前段
	 *       No                 Yes          => 正在解析，返回当前段
	 *       Yes                Yes          => 解析完成，返回当前段
	 *       Yes                No           => 未处理行，需交给父级解析段落处理，返回null
	 *       No                 No           => 段落嵌套，返回新的解析段落
	 */
	public SegmentParser parseLine(String line, int lineno) throws Exception;

	/**
	 * 当前段落是否结束
	 */
	public boolean isEndOfSegment() throws Exception;

	/**
	 * 获取当前段落解析结果
	 */
	public Map getResult() throws Exception;

	/**
	 * 解析结束
	 */
	public void parseEnd() throws Exception;
}
